package org.github.java.monitor.metrics.exporter;

import org.github.java.monitor.metrics.Metrics;

/**
 * 整体处理流程如下：
 * beforeProcess() -> process() -> process() -> *** -> process() -> afterProcess()
 * 即：每一轮统计均以beforeProcess()开始，以afterProcess()结束，中间会多次调用process()
 *
 * @author LiJun.Liu
 * @since 2022/12/8
 */
public interface MetricsExporter<T extends Metrics> {

    /**
     * 在每一轮统计指标开始处理前调用
     *
     * @param processId : 本轮统计的唯一ID
     */
    void beforeProcess(long processId, long startMillis, long stopMillis);

    /**
     * @param metrics     : 本轮统计的某一个指标
     * @param processId   : 本轮统计的唯一ID
     * @param startMillis : 本轮统计信息的起始时间，单位为ms
     * @param stopMillis  : 本轮统计信息的终止时间，单位为ms
     */
    void process(T metrics, long processId, long startMillis, long stopMillis);

    /**
     * 在每一轮统计指标开始处理前调用
     *
     * @param processId : 本轮统计的唯一ID
     */
    void afterProcess(long processId, long startMillis, long stopMillis);
}
