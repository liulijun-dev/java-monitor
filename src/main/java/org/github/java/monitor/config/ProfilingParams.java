package org.github.java.monitor.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/8
 */
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProfilingParams {

    private int mostTimeThreshold; //UNIT: ms

    private int outThresholdCount;

    public static ProfilingParams of(int mostTimeThreshold, int outThresholdCount) {
        return new ProfilingParams(mostTimeThreshold, outThresholdCount);
    }
}
