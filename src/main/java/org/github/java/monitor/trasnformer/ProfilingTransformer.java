package org.github.java.monitor.trasnformer;

import lombok.extern.slf4j.Slf4j;
import org.github.java.monitor.asm.ProfilingClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
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
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        if (ProfilingFilter.isNotNeedInject(className)) {
            return classfileBuffer;
        }

        if (!ProfilingFilter.isNeedInject(className)) {
            return classfileBuffer;
        }

        if (loader != null && ProfilingFilter.isNotNeedInjectClassLoader(loader.getClass().getName())) {
            return classfileBuffer;
        }

        log.info("ProfilingTransformer.transform(" + getClassLoaderName(loader) + ", " + className
            + ", classBeingRedefined, protectionDomain, " + classfileBuffer.length + ")...");
        return getBytes(loader, className, classfileBuffer);
    }

    private byte[] getBytes(ClassLoader loader,
                            String className,
                            byte[] classFileBuffer) {
        if (needComputeMaxs(loader)) {
            ClassReader cr = new ClassReader(classFileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = new ProfilingClassVisitor(cw, className);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        } else {
            ClassReader cr = new ClassReader(classFileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
            ClassVisitor cv = new ProfilingClassVisitor(cw, className);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        }
    }

    private boolean needComputeMaxs(ClassLoader classLoader) {
        if (classLoader == null) {
            return false;
        }

        String loaderName = getClassLoaderName(classLoader);
        return loaderName.equals("org.apache.catalina.loader.WebappClassLoader")
            || loaderName.equals("org.apache.catalina.loader.ParallelWebappClassLoader")
            || loaderName.equals("org.springframework.boot.loader.LaunchedURLClassLoader")
            || loaderName.startsWith("org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders");
    }

    private String getClassLoaderName(ClassLoader classLoader) {
        if (classLoader == null) {
            return "null";
        }

        return classLoader.getClass().getName();
    }
}
