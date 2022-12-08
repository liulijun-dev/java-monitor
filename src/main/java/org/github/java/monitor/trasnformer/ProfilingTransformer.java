package org.github.java.monitor.trasnformer;

import lombok.extern.slf4j.Slf4j;
import org.github.java.monitor.asm.ProfilingClassVisitor;
import org.github.java.monitor.util.ClassUtil;
import org.github.java.monitor.util.LogUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
@Slf4j
public class ProfilingTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        if (ProfilingFilter.isNotNeedInject(className)) {
            return classfileBuffer;
        }

        if (!ProfilingFilter.isNeedInject(className)) {
            return classfileBuffer;
        }

        if (loader != null && ProfilingFilter.isNotNeedInjectClassLoader(loader.getClass().getName())) {
            return classfileBuffer;
        }

        LogUtil.logMethodInfo(
            "ProfilingTransformer.transform",
            ClassUtil.getClassName(loader),
            className,
            ClassUtil.getClassName(classBeingRedefined),
            "protectionDomain",
            classfileBuffer.length);
        return getBytes(loader, className, classfileBuffer);
    }

    private byte[] getBytes(ClassLoader loader,
                            String className,
                            byte[] classFileBuffer) {
        ClassReader cr = new ClassReader(classFileBuffer);
        ClassWriter classWriter;
        ClassVisitor classVisitor;
        if (needComputeMaxs(loader)) {
            classWriter = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        } else {
            classWriter = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        }
        classVisitor = new ProfilingClassVisitor(classWriter, className);
        cr.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    private boolean needComputeMaxs(ClassLoader classLoader) {
        if (classLoader == null) {
            return false;
        }

        String loaderName = classLoader.getClass().getName();
        return loaderName.equals("org.apache.catalina.loader.WebappClassLoader")
            || loaderName.equals("org.apache.catalina.loader.ParallelWebappClassLoader")
            || loaderName.equals("org.springframework.boot.loader.LaunchedURLClassLoader")
            || loaderName.startsWith("org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders");
    }
}
