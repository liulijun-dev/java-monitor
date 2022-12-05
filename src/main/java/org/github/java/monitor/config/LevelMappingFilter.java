package org.github.java.monitor.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.github.java.monitor.util.StrMatchUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.github.java.monitor.constant.ClassLevels.API;
import static org.github.java.monitor.constant.ClassLevels.CACHE;
import static org.github.java.monitor.constant.ClassLevels.CONSUMER;
import static org.github.java.monitor.constant.ClassLevels.CONTROLLER;
import static org.github.java.monitor.constant.ClassLevels.DAO;
import static org.github.java.monitor.constant.ClassLevels.DISPATCHER;
import static org.github.java.monitor.constant.ClassLevels.FILTER;
import static org.github.java.monitor.constant.ClassLevels.HANDLER;
import static org.github.java.monitor.constant.ClassLevels.INTERCEPTOR;
import static org.github.java.monitor.constant.ClassLevels.LISTENER;
import static org.github.java.monitor.constant.ClassLevels.OTHERS;
import static org.github.java.monitor.constant.ClassLevels.PROCESSOR;
import static org.github.java.monitor.constant.ClassLevels.PRODUCER;
import static org.github.java.monitor.constant.ClassLevels.SERVICE;
import static org.github.java.monitor.constant.ClassLevels.UTILS;

/**
 * MethodLevelMapping=Controller:[*Controller];Api:[*Api*];
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LevelMappingFilter {

    private static final Map<String, List<String>> LEVEL_EXPS_MAP = new HashMap<>(20);

    static {
        //Initialize the default level mappings
        LEVEL_EXPS_MAP.put(CONTROLLER, Collections.singletonList("*Controller"));
        LEVEL_EXPS_MAP.put(INTERCEPTOR, Collections.singletonList("*Interceptor"));
        LEVEL_EXPS_MAP.put(PRODUCER, Collections.singletonList("*Producer"));
        LEVEL_EXPS_MAP.put(CONSUMER, Collections.singletonList("*Consumer"));
        LEVEL_EXPS_MAP.put(LISTENER, Collections.singletonList("*Listener"));
        LEVEL_EXPS_MAP.put(FILTER, Collections.singletonList("*Filter"));
        LEVEL_EXPS_MAP.put(HANDLER, Collections.singletonList("*Handler"));
        LEVEL_EXPS_MAP.put(PROCESSOR, Collections.singletonList("*Processor"));
        LEVEL_EXPS_MAP.put(DISPATCHER, Collections.singletonList("*Dispatcher"));
        LEVEL_EXPS_MAP.put(API, Arrays.asList("*Api", "*ApiImpl"));
        LEVEL_EXPS_MAP.put(SERVICE, Arrays.asList("*Service", "*ServiceImpl"));
        LEVEL_EXPS_MAP.put(CACHE, Arrays.asList("*Cache", "*CacheImpl"));
        LEVEL_EXPS_MAP.put(DAO, Collections.singletonList("*DAO"));
        LEVEL_EXPS_MAP.put(UTILS, Collections.singletonList("*Utils"));
    }

    /**
     * 根据 simpleClassName 返回 ClassLevel
     */
    public static String getClassLevel(String simpleClassName) {
        return LEVEL_EXPS_MAP.entrySet().stream()
            .filter(entry -> entry.getValue().stream()
                .anyMatch(expression -> StrMatchUtil.isMatch(simpleClassName, expression)))
            .findFirst()
            .map(Map.Entry::getKey)
            .orElse(OTHERS);
    }

    public static void putLevelMapping(String classLevel, List<String> expList) {
        LEVEL_EXPS_MAP.put(classLevel, expList);
    }
}
