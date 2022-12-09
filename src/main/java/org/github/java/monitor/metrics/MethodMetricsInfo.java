package org.github.java.monitor.metrics;

import lombok.Getter;

/**
 * 方法调用统计信息
 *
 * @author LiJun.Liu
 * @since 2022/12/9
 */
@Getter
public class MethodMetricsInfo {
    private long tp95Sum;

    private long tp99Sum;

    private long tp999Sum;

    private long tp9999Sum;

    /**
     * 函数调用次数
     */
    private int count;

    public MethodMetricsInfo(int tp95, int tp99, int tp999, int tp9999) {
        this.tp95Sum = tp95;
        this.tp99Sum = tp99;
        this.tp999Sum = tp999;
        this.tp9999Sum = tp9999;
        this.count = isValid(tp95, tp99, tp999, tp9999) ? 1 : 0;
    }

    private boolean isValid(int tp95, int tp99, int tp999, int tp9999) {
        return tp95 >= 0 && tp99 >= 0 && tp999 >= 0 && tp9999 >= 0;
    }

    public void add(int tp95, int tp99, int tp999, int tp9999) {
        if (isValid(tp95, tp99, tp999, tp9999)) {
            tp95Sum += tp95;
            tp99Sum += tp99;
            tp999Sum += tp999;
            tp9999Sum += tp9999;
            count++;
        }
    }

    @Override
    public String toString() {
        return "MethodMetricsInfo{" +
            "tp95Sum=" + tp95Sum +
            ", tp99Sum=" + tp99Sum +
            ", tp999Sum=" + tp999Sum +
            ", tp9999Sum=" + tp9999Sum +
            ", count=" + count +
            '}';
    }
}
