package org.github.java.monitor.core;

import java.lang.reflect.Method;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/6
 */
public abstract class AbstractMethodTagMaintainer {
    public abstract int addMethodTag(MethodTag methodTag);

    /**
     *
     * @param method 方法
     * @return -1 添加失败
     */
    public abstract int addMethodTag(Method method);

    public abstract MethodTag getMethodTag(int methodId);

    public abstract int getMethodTagCount();
}
