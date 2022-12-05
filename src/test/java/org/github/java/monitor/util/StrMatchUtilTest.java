package org.github.java.monitor.util;

import org.junit.jupiter.api.Test;

import static org.github.java.monitor.util.StrMatchUtil.isMatch;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
public class StrMatchUtilTest {

    @Test
    public void testIsMatch() {
        assertTrue(isMatch("org.github.java.monitor.config.abc", "org.github.java.monitor*abc"));
        assertTrue(isMatch("org.github.java.monitor.config.abc", "*.java.monitor*abc"));
        assertTrue(isMatch("org.github.java.monitor.config.abc", "*.java.monitor*a*c"));

        assertFalse(isMatch("org.github.java.monitor.config.abc", "*.java.monitor*ac"));
        assertFalse(isMatch("org.github.java.monitor.config.abc", "a*.java.monitor*ac"));
        assertFalse(isMatch("org.github.java.monitor.config.abc", "a*.java.monitor.a*"));
    }

}
