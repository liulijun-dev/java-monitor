package org.github.java.monitor.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/8
 */
@Slf4j
public final class LogUtil {
    private static final String METHOD_FORMAT = "%s(%s)";

    private static final String METHOD_FORMAT_WITH_MESSAGE = "%s(%s) %s";

    public static void logMethodInfo(String methodName, Object... parameters) {
        log.info(String.format(METHOD_FORMAT, methodName, reduceParameters(parameters)));
    }

    public static void logMethodInfo(String methodName, String message, Object... parameters) {
        log.info(String.format(METHOD_FORMAT_WITH_MESSAGE, methodName, message, reduceParameters(parameters)));
    }

    public static void logMethodWarn(String methodName, String message, Object... parameters) {
        log.warn(String.format(METHOD_FORMAT_WITH_MESSAGE, methodName, message, reduceParameters(parameters)));
    }

    public static void logMethodError(String methodName, Throwable throwable, Object... parameters) {
        log.error(String.format(METHOD_FORMAT, methodName, reduceParameters(parameters)), throwable);
    }

    private static String reduceParameters(Object... parameters) {
        return Arrays.stream(parameters).map(Object::toString).reduce("", (a, b) -> a + ", " + b);
    }
}
