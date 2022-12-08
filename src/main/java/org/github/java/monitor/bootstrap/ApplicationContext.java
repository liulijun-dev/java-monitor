package org.github.java.monitor.bootstrap;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
@Slf4j
public final class ApplicationContext {
    public static final Map<Class<?>, Object> INSTANCE_HOLDER = new HashMap<>();

    public static void addInstance(Class<?> key, Object value) {
        INSTANCE_HOLDER.put(key, value);
    }

    public static <T> T getInstance(Class<T> key) {
        T result =  (T) INSTANCE_HOLDER.get(key);
        if (Objects.isNull(result)) {
            log.error("cannot find instance for key: {}", key);
            return null;
        }

        return result;
    }
}
