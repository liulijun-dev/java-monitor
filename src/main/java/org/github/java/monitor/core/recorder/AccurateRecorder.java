package org.github.java.monitor.core.recorder;

import org.github.java.monitor.core.buffer.LongBuf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * <p>
 * 默认使用该类作为 java-monitor 的 Recorder
 * 该类用于精确存储某一个方法在指定时间片内的响应时间
 * 为了减小内存占用，利用 数组 + Map 的方式:
 * 1、将小于等于 mostTimeThreshold 的响应时间记录在 AtomicIntArray 中；
 * 2、将大于 mostTimeThreshold 的响应时间记录到 AtomicIntHashCounter 中。
 *
 * @author LiJun.Liu
 * @since 2022/12/9
 */
public final class AccurateRecorder extends Recorder {
    /**
     * 存储响应时间对应的数量
     */
    private final AtomicIntegerArray timeLessOrEqualThresholdCounter;

    private final Map<Integer, Integer> timeHighThanThresholdCounter;

    /**
     * 响应时间发出变化的数量统计。由于环境、负载等的变化，会导致调用同一个函数的响应时间发生变化，
     * 如响应时间从2ms变成3ms，则diffCount加1。
     */
    private final AtomicInteger diffCount;

    private AccurateRecorder(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        super(methodTagId);
        this.timeLessOrEqualThresholdCounter = new AtomicIntegerArray(mostTimeThreshold + 1);
        this.timeHighThanThresholdCounter = new HashMap<>(outThresholdCount);
        this.diffCount = new AtomicInteger(0);
    }

    @Override
    public void recordTime(final long startNanoTime, final long endNanoTime) {
        if (startNanoTime > endNanoTime) {
            return;
        }

        final int elapsedTimeInMill = (int) ((endNanoTime - startNanoTime) / 1_000_000);
        if (elapsedTimeInMill < timeLessOrEqualThresholdCounter.length()) {
            final int oldValue = timeLessOrEqualThresholdCounter.getAndIncrement(elapsedTimeInMill);
            if (oldValue == 0) {
                diffCount.incrementAndGet();
            }
            return;
        }

        synchronized (this) {
            final Integer oldValue = timeHighThanThresholdCounter.put(elapsedTimeInMill,
                timeHighThanThresholdCounter.getOrDefault(elapsedTimeInMill, 0) + 1);
            if (oldValue == null) {
                diffCount.incrementAndGet();
            }
        }
    }

    @Override
    public long fillSortedRecords(LongBuf longBuf) {
        return fillSortedKvs(longBuf);
    }

    @Override
    public int getDiffCount() {
        return diffCount.get();
    }

    @Override
    public void resetRecord() {
        if (hasRecord()) {
            for (int i=0; i<timeLessOrEqualThresholdCounter.length(); i++) {
                timeLessOrEqualThresholdCounter.set(i, 0);
            }
            timeHighThanThresholdCounter.clear();
            diffCount.set(0);
        }
    }

    @Override
    public boolean hasRecord() {
        return diffCount.get() > 0;
    }

    public static AccurateRecorder getInstance(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        return new AccurateRecorder(methodTagId, mostTimeThreshold, outThresholdCount);
    }

    private long fillSortedKvs(LongBuf longBuf) {
        long totalCount = 0L;
        for (int i = 0, len = timeLessOrEqualThresholdCounter.length(); i < len; ++i) {
            final int count = timeLessOrEqualThresholdCounter.get(i);
            if (count > 0) {
                longBuf.write(i, count);
                totalCount += count;
            }
        }

        int offset = longBuf.writerIndex();
        for (Map.Entry<Integer, Integer> entry : timeHighThanThresholdCounter.entrySet()) {
            Integer value = entry.getValue();
            if (value > 0) {
                longBuf.write(entry.getKey(), entry.getValue());
                totalCount += entry.getValue();
            }
        }
        Arrays.sort(longBuf._buf(), offset, longBuf.writerIndex());

        return totalCount;
    }
}
