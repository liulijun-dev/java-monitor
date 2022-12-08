package org.github.java.monitor.core.recorder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/8
 */
public class Recorders {
    private final AtomicReferenceArray<Recorder> recorders;

    @Setter
    @Getter
    private volatile boolean writing;

    @Setter
    @Getter
    private volatile long startTime;

    @Setter
    @Getter
    private volatile long stopTime;

    public Recorders(AtomicReferenceArray<Recorder> recorders) {
        this.recorders = recorders;
    }

    public Recorder getRecorder(int index) {
        return recorders.get(index);
    }

    public void setRecorder(int index, Recorder recorder) {
        recorders.set(index, recorder);
    }

    public int size() {
        return recorders.length();
    }

    public void resetRecorder() {
        int length = recorders.length();
        for (int i = 0; i < length; ++i) {
            Recorder recorder = recorders.get(i);
            if (recorder != null) {
                recorder.resetRecord();
            }
        }
    }

    @Override
    public String toString() {
        return "Recorders{" +
            "recorderArr=" + recorders +
            ", writing=" + writing +
            ", startTime=" + startTime +
            ", stopTime=" + stopTime +
            '}';
    }
}
