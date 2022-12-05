package org.github.java.monitor.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StrUtil {
    public static List<String> splitAsList(String str, String separator) {
        return List.of(StringUtils.split(str, separator));
    }
}
