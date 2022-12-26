package org.github.java.monitor.config;

import lombok.Getter;
import lombok.Setter;
import org.github.java.monitor.bootstrap.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.github.java.monitor.constant.PropertyKeys.Recorder.BACKUP_COUNT;
import static org.github.java.monitor.constant.PropertyKeys.Recorder.MODE;
import static org.github.java.monitor.constant.PropertyKeys.Recorder.SIZE_TIMING_ARR;
import static org.github.java.monitor.constant.PropertyKeys.Recorder.SIZE_TIMING_MAP;
import static org.github.java.monitor.constant.PropertyValues.Recorder.MODE_ACCURATE;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/9
 */
@Getter
@Setter
public class RecorderConfig {

    private String mode;

    private int backupCount;

    private int timingArrSize;

    private int timingMapSize;

    private ProfilingParams commonProfilingParams;

    private final Map<String, ProfilingParams> profilingParamsMap = new HashMap<>(1024);

    public boolean accurateMode() {
        return MODE_ACCURATE.equalsIgnoreCase(mode);
    }
    public void commonProfilingParams(ProfilingParams commonProfilingParams) {
        this.commonProfilingParams = commonProfilingParams;
    }

    public void addProfilingParam(String methodName, int timeThreshold, int outThresholdCount) {
        profilingParamsMap.put(methodName, ProfilingParams.of(timeThreshold, outThresholdCount));
    }

    public ProfilingParams getProfilingParam(String methodName) {
        ProfilingParams params = profilingParamsMap.get(methodName);
        if (params != null) {
            return params;
        }
        return commonProfilingParams;
    }

    @Override
    public String toString() {
        return "RecorderConfig{" +
                "mode='" + mode + '\'' +
                ", backupCount=" + backupCount +
                ", timingArrSize=" + timingArrSize +
                ", timingMapSize=" + timingMapSize +
                ", commonProfilingParams=" + commonProfilingParams +
                '}';
    }

    public static RecorderConfig loadRecorderConfig() {
        MonitorProperties monitorProperties = ApplicationContext.getInstance(MonitorProperties.class);
        assert monitorProperties != null;

        final RecorderConfig config = new RecorderConfig();
        config.setMode(monitorProperties.getStr(MODE, MODE_ACCURATE));
        config.setBackupCount(monitorProperties.getInt(BACKUP_COUNT, 1));
        config.setTimingArrSize(monitorProperties.getInt(SIZE_TIMING_ARR, 1024));
        config.setTimingMapSize(monitorProperties.getInt(SIZE_TIMING_MAP, 32));
        config.commonProfilingParams(ProfilingParams.of(config.getTimingArrSize(), config.getTimingMapSize()));
        return config;
    }
}
