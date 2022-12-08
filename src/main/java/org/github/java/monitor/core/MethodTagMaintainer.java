package org.github.java.monitor.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.github.java.monitor.config.MetricsConfig;
import org.github.java.monitor.config.ProfilingConfig;
import org.github.java.monitor.util.TypeDescUtil;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static java.lang.String.format;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/6
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodTagMaintainer extends AbstractMethodTagMaintainer {
    public static final int MAX_NUM = 1024 * 32;

    private static final MethodTagMaintainer INSTANCE = new MethodTagMaintainer();

    private static final MetricsConfig metricsConfig = ProfilingConfig.metricsConfig();

    private final AtomicInteger index = new AtomicInteger(0);

    private final AtomicReferenceArray<MethodTag> methodTagArr = new AtomicReferenceArray<>(MAX_NUM);

    private final ConcurrentHashMap<Method, Integer> methodMap = new ConcurrentHashMap<>(4096);

    public static MethodTagMaintainer getInstance() {
        return INSTANCE;
    }

    @Override
    public int addMethodTag(MethodTag methodTag) {
        int methodId = index.getAndIncrement();
        if (methodId > MAX_NUM) {
            log.warn(format("MethodTagMaintainer.addMethodTag(%s): methodId > MAX_NUM: %s, ignored!", methodTag, true));
            return -1;
        }

        methodTagArr.set(methodId, methodTag);
        return methodId;
    }

    @Override
    public int addMethodTag(Method method) {
        Integer tagId = methodMap.get(method);
        if (tagId != null) {
            return tagId;
        }

        synchronized (this) {
            tagId = methodMap.get(method);
            if (tagId != null) {
                return tagId;
            }

            tagId = addMethodTag(createMethodTag(method));
        }

        if (tagId < 0) {
            return tagId;
        }

        methodMap.putIfAbsent(method, tagId);
        return tagId;
    }

    private static MethodTag createMethodTag(Method method) {
        String methodParamDesc = metricsConfig.showMethodParams() ? TypeDescUtil.getMethodParamsDesc(method) : "";
        Class<?> declaringClass = method.getDeclaringClass();
        return MethodTag.newDynamicProxyInstance(declaringClass.getName(),
            declaringClass.getSimpleName(),
            method.getName(),
            methodParamDesc);
    }

    @Override
    public MethodTag getMethodTag(int methodId) {
        if (methodId >= 0 && methodId < MAX_NUM) {
            return methodTagArr.get(methodId);
        }
        return null;
    }

    @Override
    public int getMethodTagCount() {
        return index.get();
    }

}
