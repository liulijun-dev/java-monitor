package org.github.java.monitor.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
public class PkgExpressionUtilTest {
    @Test
    public void shouldSuccess() {
        Set<String> result = PkgExpressionUtil.parse("org.apache.util.[Logger,DateUtils]");
        assertTrue(result.containsAll(List.of("org.apache.util.Logger", "org.apache.util.DateUtils")));

        result = PkgExpressionUtil.parse("org.apache.metric.[formatter,processor]");
        assertTrue(result.containsAll(List.of("org.apache.metric.formatter", "org.apache.metric.processor")));
    }
}
