package org.github.java.monitor.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TypeDescUtil {
    /**
     * @param innerClassName : 形如：java/lang/String
     */
    public static String getSimpleClassName(String innerClassName) {
        int idx = innerClassName.lastIndexOf('/');
        return innerClassName.substring(idx + 1);
    }
}
