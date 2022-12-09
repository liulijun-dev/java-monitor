package org.github.java.monitor.core.recorder;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/8
 */
public interface Scheduler {

    /**
     * @param lastTimeSliceStartTime: 上一个时间片的起始时间
     * @param timeSliceInMill:          时间片大小
     */
    void run(long lastTimeSliceStartTime, long timeSliceInMill);

    String name();

}
