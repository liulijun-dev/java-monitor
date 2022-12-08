package org.github.java.monitor.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/8
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassUtil {
    public static String getClassName(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return StringUtils.EMPTY;
        }

        return clazz.getName();
    }

    public static String getClassName(ClassLoader classLoader) {
        if (Objects.isNull(classLoader)) {
            return StringUtils.EMPTY;
        }

        return classLoader.getClass().getName();
    }
}
