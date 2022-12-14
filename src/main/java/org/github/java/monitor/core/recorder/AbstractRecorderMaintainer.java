package org.github.java.monitor.core.recorder;

import lombok.extern.slf4j.Slf4j;
import org.github.java.monitor.bootstrap.ExecutorManager;
import org.github.java.monitor.config.ProfilingParams;
import org.github.java.monitor.core.MethodTag;
import org.github.java.monitor.core.MethodTagMaintainer;
import org.github.java.monitor.metrics.MethodMetrics;
import org.github.java.monitor.metrics.MethodMetricsCalculator;
import org.github.java.monitor.metrics.MethodMetricsHistogram;
import org.github.java.monitor.metrics.exporter.MethodMetricsExporter;
import org.github.java.monitor.util.LogUtil;
import org.github.java.monitor.util.ThreadUtil;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.github.java.monitor.constant.PropertyKeys.Metrics.TIME_SLICE_METHOD;
import static org.github.java.monitor.constant.PropertyKeys.Recorder.BACKUP_COUNT;
import static org.github.java.monitor.constant.PropertyValues.Recorder.MAX_BACKUP_RECORDERS_COUNT;
import static org.github.java.monitor.constant.PropertyValues.Recorder.MIN_BACKUP_RECORDERS_COUNT;
import static org.github.java.monitor.core.MethodTagMaintainer.MAX_NUM;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/8
 */
@Slf4j
public abstract class AbstractRecorderMaintainer implements Scheduler {
    private static final int MAX_DELAY_TASKS = 1024;

    private volatile boolean initialState;

    protected List<Recorders> recordersList;

    private int curIndex;

    private volatile Recorders curRecorders;

    private ScheduledThreadPoolExecutor backgroundExecutor;

    private MethodMetricsExporter methodMetricsExporter;

    private boolean accurateMode;

    public boolean initial(MethodMetricsExporter methodMetricsExporter, boolean accurateMode, int backupRecordersCnt) {
        this.methodMetricsExporter = methodMetricsExporter;
        this.accurateMode = accurateMode;
        backupRecordersCnt = getFitBackupRecordersCnt(backupRecordersCnt);

        initRecorders(backupRecordersCnt);

        if (!initBackgroundTask()) {
            return false;
        }

        return initialState = initOther();
    }

    private int getFitBackupRecordersCnt(int backupRecordersCount) {
        if (backupRecordersCount < MIN_BACKUP_RECORDERS_COUNT) {
            return MIN_BACKUP_RECORDERS_COUNT;
        } else if (backupRecordersCount > MAX_BACKUP_RECORDERS_COUNT) {
            return MAX_BACKUP_RECORDERS_COUNT;
        }
        return backupRecordersCount;
    }

    private void initRecorders(int backupRecordersCount) {
        recordersList = IntStream.rangeClosed(0, backupRecordersCount)
            .mapToObj((a) -> new Recorders(new AtomicReferenceArray<>(MAX_NUM)))
            .collect(Collectors.toList());

        curRecorders = recordersList.get(curIndex);
    }

    private boolean initBackgroundTask() {
        try {
            backgroundExecutor = new ScheduledThreadPoolExecutor(1,
                ThreadUtil.newThreadFactory("java-monitor-background-executor_"),
                new ThreadPoolExecutor.DiscardOldestPolicy());

            ExecutorManager.addExecutorService(backgroundExecutor);
            return true;
        } catch (Exception e) {
            LogUtil.logMethodError("RecorderMaintainer.initBackgroundTask()", e);
        }
        return false;
    }

    public abstract boolean initOther();

    protected Recorder createRecorder(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        if (accurateMode) {
            return AccurateRecorder.getInstance(methodTagId, mostTimeThreshold, outThresholdCount);
        }
        return RoughRecorder.getInstance(methodTagId, mostTimeThreshold);
    }

    public abstract void addRecorder(int methodTagId, ProfilingParams params);

    public Recorder getRecorder(int methodTagId) {
        return curRecorders.getRecorder(methodTagId);
    }

    @Override
    public void run(long lastTimeSliceStartTime, long timeSliceInMill) {
        try {
            if (!initialState) {
                LogUtil.logMethodWarn("AbstractRecorderMaintainer.run",
                    "initialState is false", lastTimeSliceStartTime, timeSliceInMill);
                return;
            }

            final Recorders lastRecorders = turnAroundRecorders(lastTimeSliceStartTime, timeSliceInMill);
            backgroundExecutor.schedule(new ExportMetricsTask(methodMetricsExporter, lastRecorders), 10, MILLISECONDS);
        } catch (Exception e) {
            LogUtil.logMethodError("AbstractRecorderMaintainer.run", e, lastTimeSliceStartTime, timeSliceInMill);
        } finally {
            tryDiscardDelayTask();
        }
    }

    private Recorders turnAroundRecorders(long lastTimeSliceStartTime, long millTimeSlice) {
        final Recorders lastRecorders = this.curRecorders;
        lastRecorders.setStartTime(lastTimeSliceStartTime);
        lastRecorders.setStopTime(lastTimeSliceStartTime + millTimeSlice);
        lastRecorders.setWriting(false);
        //todo why init next recorders
        this.curRecorders = nextRecorders(lastTimeSliceStartTime, millTimeSlice);
        return lastRecorders;
    }

    private Recorders nextRecorders(long lastTimeSliceStartTime, long millTimeSlice) {
        curIndex = getNextIdx();
        log.debug("RecorderMaintainer.nextRecorders(long, long) curIndex=" + curIndex);

        final Recorders nextRecorders = recordersList.get(curIndex);
        nextRecorders.setStartTime(lastTimeSliceStartTime + millTimeSlice);
        nextRecorders.setStopTime(lastTimeSliceStartTime + 2 * millTimeSlice);
        nextRecorders.setWriting(true);
        nextRecorders.resetRecorder();
        return nextRecorders;
    }

    private int getNextIdx() {
        return (curIndex + 1) % recordersList.size();
    }

    private void tryDiscardDelayTask() {
        final BlockingQueue<Runnable> queue = backgroundExecutor.getQueue();
        if (queue.size() >= MAX_DELAY_TASKS) {
            queue.poll();
            log.warn("RecorderMaintainer.tryDiscardDelayTask the backgroundExecutor delay task count reach " +
                MAX_DELAY_TASKS + ", so discard oldest one!");
        }
    }

    @Override
    public String name() {
        return "RecorderMaintainer";
    }

    private static final class ExportMetricsTask implements Runnable {

        private final MethodTagMaintainer methodTagMaintainer;

        private final MethodMetricsExporter metricsExporter;

        private final Recorders lastRecorders;

        private ExportMetricsTask(MethodMetricsExporter metricsExporter, Recorders lastRecorders) {
            this.methodTagMaintainer = MethodTagMaintainer.getInstance();
            this.metricsExporter = metricsExporter;
            this.lastRecorders = lastRecorders;
        }

        @Override
        public void run() {
            if (lastRecorders.isWriting()) {
                log.warn("ExportMetricsTask.run() the lastRecorders is writing! " +
                    "Please increase `" + TIME_SLICE_METHOD + "` or `" + BACKUP_COUNT + "`!P1");
                return;
            }

            final long start = System.currentTimeMillis();
            final long epochStartMillis = lastRecorders.getStartTime();
            final long epochEndMillis = lastRecorders.getStopTime();
            try {
                metricsExporter.beforeProcess(epochStartMillis, epochStartMillis, epochEndMillis);
                final int actualSize = methodTagMaintainer.getMethodTagCount();
                for (int i = 0; i < actualSize; ++i) {
                    if (lastRecorders.isWriting()) {
                        log.warn("ExportMetricsTask.run() the lastRecorders is writing! " +
                            "Please increase `" + TIME_SLICE_METHOD + "` or `" + BACKUP_COUNT + "`!P2");
                        break;
                    }

                    final Recorder recorder = lastRecorders.getRecorder(i);
                    if (recorder == null) {
                        continue;
                    } else if (!recorder.hasRecord()) {
                        MethodMetricsHistogram.recordNoneMetrics(recorder.getMethodTagId());
                        continue;
                    }

                    final MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
                    final MethodMetrics metrics = MethodMetricsCalculator.calMetrics(recorder, methodTag, epochStartMillis, epochEndMillis);
                    MethodMetricsHistogram.recordMetrics(metrics);
                    metricsExporter.process(metrics, epochStartMillis, epochStartMillis, epochEndMillis);
                }
            } catch (Throwable e) {
                log.error("ExportMetricsTask.run() error", e);
            } finally {
                metricsExporter.afterProcess(epochStartMillis, epochStartMillis, epochEndMillis);
                final long cost = System.currentTimeMillis() - start;
                log.debug("ExportMetricsTask.run() finished!!! cost: " + cost + "ms");
            }
        }
    }
}
