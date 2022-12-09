package org.github.java.monitor.core.recorder;

import org.github.java.monitor.core.buffer.LongBuf;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * <p>
 * java-monitor默认使用的是 AccurateRecorder，如果需要使用 RoughRecorder，则在配置文件里加上 RecorderMode=rough
 * <p>
 * 该类用于粗略存储某一个方法在指定时间片内的响应时间
 * 为了进一步减小内存占用，只利用数组方式:
 * 1、将小于等于 mostTimeThreshold 的响应时间记录在数组中；
 * 2、将大于 mostTimeThreshold 的响应时间记录到 timingArr[mostTimeThreshold+1]中。
 * <p>
 * 注意：由于该 Recorder 会将大于 mostTimeThreshold 的响应时间记录为 mostTimeThreshold+1
 * 所以为了保证 RoughRecorder 记录的准确性，请把 mostTimeThreshold 设置的偏大一些。
 *
 * @author LiJun.Liu
 * @since 2022/12/8
 */
public final class RoughRecorder extends Recorder {
    /**
     * 存储响应时间对应的数量
     */
    private final AtomicIntegerArray timeCounter;

    /**
     * 响应时间发出变化的数量统计。由于环境、负载等的变化，会导致调用同一个函数的响应时间发生变化，
     * 如响应时间从2ms变成3ms，则diffCount加1。
     */
    private final AtomicInteger diffCount;

    public RoughRecorder(int methodTag, int mostTimeThreshold) {
        super(methodTag);
        this.timeCounter = new AtomicIntegerArray(mostTimeThreshold + 2);
        this.diffCount = new AtomicInteger(0);
    }

    @Override
    public void recordTime(final long startNanoTime, final long endNanoTime) {
        if (startNanoTime > endNanoTime) {
            return;
        }

        int oldValue;
        final int elapsedTimeInMill = (int) ((endNanoTime - startNanoTime) / 1000000);
        final AtomicIntegerArray timingArr = this.timeCounter;
        if (elapsedTimeInMill < timingArr.length() - 1) {
            oldValue = timingArr.getAndIncrement(elapsedTimeInMill);
        } else {
            oldValue = timingArr.getAndIncrement(timingArr.length() - 1);
        }

        if (oldValue <= 0) {
            diffCount.incrementAndGet();
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
        if (!hasRecord()) {
            return;
        }

        for (int i = 0; i< timeCounter.length(); i++) {
            timeCounter.set(i, 0);
        }
        diffCount.set(0);
    }

    @Override
    public boolean hasRecord() {
        return diffCount.get() > 0;
    }

    public static RoughRecorder getInstance(int methodTagId, int mostTimeThreshold) {
        return new RoughRecorder(methodTagId, mostTimeThreshold);
    }

    private long fillSortedKvs(LongBuf longBuf) {
        long totalCount = 0L;
        for (int i = 0, len = timeCounter.length(); i < len; ++i) {
            final int count = timeCounter.get(i);
            if (count > 0) {
                longBuf.write(i, count);
                totalCount += count;
            }
        }
        return totalCount;
    }
}
