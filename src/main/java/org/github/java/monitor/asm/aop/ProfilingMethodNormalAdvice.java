package org.github.java.monitor.asm.aop;

import org.github.java.monitor.config.MetricsConfig;
import org.github.java.monitor.config.ProfilingConfig;
import org.github.java.monitor.core.MethodTag;
import org.github.java.monitor.core.MethodTagMaintainer;
import org.github.java.monitor.core.recorder.AbstractRecorderMaintainer;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/6
 */
public class ProfilingMethodNormalAdvice extends AdviceAdapter {

    private static final String PROFILING_ASPECT_INNER_NAME = Type.getInternalName(ProfilingAspect.class);

    private static final MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

    private final AbstractRecorderMaintainer maintainer = ASMRecorderMaintainer.getInstance();

    private final MetricsConfig metricsConf = ProfilingConfig.metricsConfig();

    private final RecorderConfig recorderConf = ProfilingConfig.recorderConfig();

    private final String innerClassName;

    private final String methodName;

    private final int methodTagId;

    private int startTimeIdentifier;

    public ProfilingMethodNormalAdvice(int access,
                                       String methodName,
                                       String desc,
                                       MethodVisitor mv,
                                       String innerClassName,
                                       String fullClassName,
                                       String simpleClassName,
                                       String classLevel,
                                       String humanMethodDesc) {
        super(ASM9, mv, access, methodName, desc);
        this.methodName = methodName;
        this.methodTagId = methodTagMaintainer.addMethodTag(
                createMethodTag(fullClassName, simpleClassName, classLevel, methodName, humanMethodDesc));
        this.innerClassName = innerClassName;
    }

    @Override
    protected void onMethodEnter() {
        if (profiling()) {
            maintainer.addRecorder(methodTagId, recorderConf.getProfilingParam(innerClassName + "/" + methodName));

            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            startTimeIdentifier = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, startTimeIdentifier);
        }
    }

    @Override
    protected void onMethodExit(int opcode) {
        if (profiling() && ((IRETURN <= opcode && opcode <= RETURN) || opcode == ATHROW)) {
            mv.visitVarInsn(LLOAD, startTimeIdentifier);
            mv.visitLdcInsn(methodTagId);
            mv.visitMethodInsn(INVOKESTATIC, PROFILING_ASPECT_INNER_NAME, "profiling", "(JI)V", false);
        }
    }

    private boolean profiling() {
        return methodTagId >= 0;
    }

    private MethodTag createMethodTag(String fullClassName,
                                      String simpleClassName,
                                      String classLevel,
                                      String methodName,
                                      String humanMethodDesc) {
        String methodParamDesc = metricsConf.showMethodParams() ? humanMethodDesc : "";
        return MethodTag.newGeneralInstance(fullClassName, simpleClassName, classLevel, methodName, methodParamDesc);
    }
}
