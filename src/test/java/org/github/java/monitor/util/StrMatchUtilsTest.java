package org.github.java.monitor.util;

import org.junit.jupiter.api.Test;

import static org.github.java.monitor.util.StrMatchUtils.isMatch;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by LinShunkang on 2020/07/26
 */
public class StrMatchUtilsTest {

    @Test
    public void testIsMatch() {
        assertTrue(isMatch("cn.myperf4j.config.abc", "cn.myperf4j*abc"));
        assertTrue(isMatch("cn.myperf4j.config.abc", "*.myperf4j*abc"));
        assertTrue(isMatch("cn.myperf4j.config.abc", "*.myperf4j*a*c"));

        assertFalse(isMatch("cn.myperf4j.config.abc", "*.myperf4j*ac"));
        assertFalse(isMatch("cn.myperf4j.config.abc", "a*.myperf4j*ac"));
        assertFalse(isMatch("cn.myperf4j.config.abc", "a*.myperf4j.a*"));
    }

}
