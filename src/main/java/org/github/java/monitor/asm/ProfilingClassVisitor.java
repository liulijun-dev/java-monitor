package org.github.java.monitor.asm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.github.java.monitor.asm.aop.ProfilingDynamicMethodAdvice;
import org.github.java.monitor.asm.aop.ProfilingMethodNormalAdvice;
import org.github.java.monitor.config.LevelMappingFilter;
import org.github.java.monitor.config.ProfilingConfig;
import org.github.java.monitor.trasnformer.ProfilingFilter;
import org.github.java.monitor.util.TypeDescUtil;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_BRIDGE;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_NATIVE;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ASM9;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
@Slf4j
public class ProfilingClassVisitor extends ClassVisitor {

    private final String innerClassName;

    private final String fullClassName;

    private final String simpleClassName;

    private final String classLevel;

    private boolean isInterface;

    private boolean isInvocationHandler;

    private final List<String> fieldNames = new ArrayList<>();

    public ProfilingClassVisitor(final ClassVisitor classVisitor, String innerClassName) {
        super(ASM9, classVisitor);
        this.innerClassName = innerClassName;
        this.fullClassName = innerClassName.replace('/', '.');
        this.simpleClassName = TypeDescUtil.getSimpleClassName(innerClassName);
        this.classLevel = LevelMappingFilter.getClassLevel(simpleClassName);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        log.debug(format("ProfilingClassVisitor.visit(%d, %d, %s, %s, %s, %s)",
            version, access, name, signature, superName, Arrays.toString(interfaces)));
        super.visit(version, access, name, signature, superName, interfaces);
        this.isInterface = (access & ACC_INTERFACE) != 0;
        this.isInvocationHandler = isInvocationHandler(interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        String upFieldName = name.substring(0, 1).toUpperCase() + name.substring(1);
        fieldNames.add("get" + upFieldName);
        fieldNames.add("set" + upFieldName);
        fieldNames.add("is" + upFieldName);

        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {
        if (isInterface || !isNeedVisit(access, name)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        String classMethodName = simpleClassName + "." + name;
        if (ProfilingFilter.isNotNeedInjectMethod(classMethodName)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        String desc4Human = TypeDescUtil.getMethodParamsDesc(desc);
        String classMethodNameWithParameter = classMethodName + desc4Human;
        if (ProfilingFilter.isNotNeedInjectMethod(classMethodNameWithParameter)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
        if (methodVisitor == null) {
            return null;
        }
        log.debug(format("ProfilingClassVisitor.visitMethod(%s, %s, %s, %s, %s), innerClassName=%s",
            access, name, desc, signature, Arrays.toString(exceptions), innerClassName));

        if (isInvocationHandler && isInvokeMethod(name, desc)) {
            return new ProfilingDynamicMethodAdvice(access, name, desc, methodVisitor);
        } else {
            return new ProfilingMethodNormalAdvice(access, name, desc, methodVisitor,
                innerClassName, fullClassName, simpleClassName, classLevel, desc4Human);
        }
    }

    private boolean isInvocationHandler(String[] interfaces) {
        if (ArrayUtils.isEmpty(interfaces)) {
            return false;
        }

        return Arrays.stream(interfaces).anyMatch("java/lang/reflect/InvocationHandler"::equalsIgnoreCase);
    }

    private boolean isNeedVisit(int access, String name) {
        //不对私有方法进行注入
        if ((access & ACC_PRIVATE) != 0 && ProfilingConfig.filterConfig().excludePrivateMethod()) {
            return false;
        }

        //不对抽象方法、native方法、桥接方法、合成方法进行注入
        if ((access & ACC_ABSTRACT) != 0
                || (access & ACC_NATIVE) != 0
                || (access & ACC_BRIDGE) != 0
                || (access & ACC_SYNTHETIC) != 0) {
            return false;
        }

        if ("<init>".equals(name) || "<clinit>".equals(name)) {
            return false;
        }

        if (fieldNames.contains(name) || ProfilingFilter.isNotNeedInjectMethod(name)) {
            return false;
        }

        return true;
    }

    private boolean isInvokeMethod(String methodName, String methodDesc) {
        return methodName.equals("invoke")
                && methodDesc.equals(
                "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;");
    }
}
