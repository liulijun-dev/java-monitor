package org.github.java.monitor.bootstrap;

import java.util.HashMap;
import java.util.Map;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
public final class ApplicationContext {
    public static final Map<Class<?>, Object> INSTANCE_HOLDER = new HashMap<>();

    public static void addInstance(Class<?> key, Object value) {
        INSTANCE_HOLDER.put(key, value);
    }

    public static
}
