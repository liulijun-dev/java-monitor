package org.github.java.monitor.core.recorder;

import org.github.java.monitor.config.ProfilingParams;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/8
 */
public class ASMRecorderMaintainer extends AbstractRecorderMaintainer {

    private static final ASMRecorderMaintainer instance = new ASMRecorderMaintainer();

    public static ASMRecorderMaintainer getInstance() {
        return instance;
    }

    @Override
    public boolean initOther() {
        return true;
    }

    @Override
    public void addRecorder(int methodTagId, ProfilingParams params) {
        //todo why createRecorder for each recorders
        recordersList.forEach(recorders ->
            recorders.setRecorder(methodTagId, createRecorder(methodTagId, params.getMostTimeThresholdInMill(), params.getOutThresholdCount())));
    }
}
