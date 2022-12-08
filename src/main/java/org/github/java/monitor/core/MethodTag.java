package org.github.java.monitor.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.github.java.monitor.config.LevelMappingFilter;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/6
 */
@Getter
public class MethodTag {
    private final String fullDescription;

    private final String description;

    private final String type;

    private static final String TYPE_GENERAL = "General";

    private static final String TYPE_DYNAMIC_PROXY = "DynamicProxy";

    private final String simpleClassName;

    private final String methodName;

    private final String methodParamDesc;

    private final String level;

    private MethodTag(String fullClassName,
                      String simpleClassName,
                      String methodName,
                      String methodParamDesc,
                      String type,
                      String level) {
        this.simpleClassName = simpleClassName;
        this.methodName = methodName;
        this.methodParamDesc = methodParamDesc;
        this.fullDescription = fullClassName + '.' + methodName;
        this.description = simpleClassName + '.' + methodName + methodParamDesc;
        this.type = type;
        this.level = level;
    }

    @Override
    public String toString() {
        return "MethodTag{" +
            "simpleClassName='" + simpleClassName + '\'' +
            ", methodName='" + methodName + '\'' +
            ", methodParamDesc='" + methodParamDesc + '\'' +
            ", description='" + description + '\'' +
            ", type='" + type + '\'' +
            '}';
    }

    public static MethodTag newGeneralInstance(String fullClassName,
                                               String simpleClassName,
                                               String classLevel,
                                               String methodName,
                                               String methodParamDesc) {
        return new MethodTag(fullClassName, simpleClassName, methodName, methodParamDesc, TYPE_GENERAL, classLevel);
    }

    public static MethodTag newDynamicProxyInstance(String fullClassName,
                                                    String className,
                                                    String methodName,
                                                    String methodParamDesc) {
        String classLevel = LevelMappingFilter.getClassLevel(className);
        return new MethodTag(fullClassName, className, methodName, methodParamDesc, TYPE_DYNAMIC_PROXY, classLevel);
    }
}
