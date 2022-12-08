package org.github.java.monitor.asm.aop;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.github.java.monitor.config.ProfilingParams;
import org.github.java.monitor.core.MethodTagMaintainer;
import org.github.java.monitor.core.recorder.ASMRecorderMaintainer;
import org.github.java.monitor.core.recorder.Recorder;
import org.github.java.monitor.util.LogUtil;

import java.lang.reflect.Method;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProfilingAspect {

    private static final MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

    private static ASMRecorderMaintainer recorderMaintainer;

    private static boolean running;

    public static void profiling(final long startNanos, final int methodTagId) {
        try {
            if (!running) {
                LogUtil.logMethodWarn("ProfilingAspect.profiling()", "ignore for not runnint",
                    methodTagId, MethodTagMaintainer.getInstance().getMethodTag(methodTagId), startNanos);
                return;
            }

            Recorder recorder = recorderMaintainer.getRecorder(methodTagId);
            if (recorder == null) {
                LogUtil.logMethodWarn("ProfilingAspect.profiling()", "ignore for null recorder",
                    methodTagId, MethodTagMaintainer.getInstance().getMethodTag(methodTagId), startNanos);
                return;
            }

            recorder.recordTime(startNanos, System.nanoTime());
        } catch (Exception e) {
            LogUtil.logMethodError("ProfilingAspect.profiling",
                e, startNanos, methodTagId,MethodTagMaintainer.getInstance().getMethodTag(methodTagId));
        }
    }

    //InvocationHandler.invoke(Object proxy, Method method, Object[] args)
    public static void profiling(final long startNanos, final Method method) {
        try {
            if (!running) {
                LogUtil.logMethodWarn("ProfilingAspect.profiling()", "ignore for not running", startNanos, method);
                return;
            }

            int methodTagId = methodTagMaintainer.addMethodTag(method);
            if (methodTagId < 0) {
                LogUtil.logMethodWarn("ProfilingAspect.profiling", "add method tag fail", startNanos, method);
                return;
            }

            Recorder recorder = recorderMaintainer.getRecorder(methodTagId);
            if (recorder == null) {
                synchronized (ProfilingAspect.class) {
                    recorder = recorderMaintainer.getRecorder(methodTagId);
                    if (recorder == null) {
                        recorderMaintainer.addRecorder(methodTagId, ProfilingParams.of(3000, 10));
                        recorder = recorderMaintainer.getRecorder(methodTagId);
                    }
                }
            }

            recorder.recordTime(startNanos, System.nanoTime());
        } catch (Exception e) {
            LogUtil.logMethodError("ProfilingAspect.profiling", e, startNanos, method);
        }
    }

    public static void setRecorderMaintainer(ASMRecorderMaintainer maintainer) {
        synchronized (ProfilingAspect.class) { //强制把值刷新到主存
            recorderMaintainer = maintainer;
        }
    }

    public static void setRunning(boolean run) {
        synchronized (ProfilingAspect.class) { //强制把值刷新到主存
            running = run;
        }
    }
}
