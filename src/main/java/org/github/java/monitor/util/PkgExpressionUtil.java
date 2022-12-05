package org.github.java.monitor.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 该类用于解析 java-monitor 自定义的包路径表达式解析
 * 规则：
 * 1、可以用 [] 代表集合的概念，集合 [e1,e2,e3] 中包含多个元素，每个元素由英文逗号分隔，元素可以是 package 和 class。但 [] 不可嵌套出现。
 * 例如：
 * a、org.apache.util.[Logger,DateUtils] -> org.apache.util.Logger;cn.apache.util.DateUtils
 * b、org.apache.metric.[formatter,processor] -> org.apache.metric.formatter;org.apache.metric.processor
 * c、org.apache.metric.[formatter,MethodMetrics] -> org.apache.metric.formatter;org.apache.metric.MethodMetrics
 * <p>
 * 2、可以用 * 代表贪心匹配，可以匹配多个字符。
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PkgExpressionUtil {
    public static Set<String> parse(String expStr) {
        int leftIdx = expStr.indexOf('[');
        if (leftIdx < 0) {
            return Set.of(expStr);
        }

        int rightIdx = expStr.indexOf(']', leftIdx);
        if (rightIdx < 0) {
            throw new IllegalArgumentException("PkgExpUtil.parse(\"" + expStr + "\"): '[' always paired with ']'");
        }

        String prefixStr = expStr.substring(0, leftIdx);
        String suffixStr = rightIdx + 1 < expStr.length() ? expStr.substring(rightIdx + 1) : "";

        String elementsStr = expStr.substring(leftIdx + 1, rightIdx);
        List<String> elements = StrUtil.splitAsList(elementsStr, ",");

        return elements.stream()
            .map(element -> prefixStr.concat(element).concat(suffixStr))
            .collect(Collectors.toSet());
    }
}
