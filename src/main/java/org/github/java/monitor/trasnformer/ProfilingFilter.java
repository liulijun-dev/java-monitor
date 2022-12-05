package org.github.java.monitor.trasnformer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.github.java.monitor.util.StrMatchUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProfilingFilter {
    /**
     * 不需要注入的 Package前缀 集合
     */
    private static final Set<String> excludePackagePrefix = new HashSet<>();

    /**
     * 不需要注入的 Package表达式 集合
     */
    private static final Set<String> excludePackageExpression = new HashSet<>();

    /**
     * 需要注入的 Package前缀 集合
     */
    private static final Set<String> includePackagePrefix = new HashSet<>();

    /**
     * 需要注入的 Package表达式 集合
     */
    private static final Set<String> includePackageExpression = new HashSet<>();

    /**
     * 不需要注入的 method 集合
     */
    private static final Set<String> excludeMethods = new HashSet<>();

    /**
     * 不注入的 ClassLoader 集合
     */
    private static final Set<String> excludeClassLoader = new HashSet<>();

    static {
        // 默认不注入的 package
        excludePackagePrefix.add("java/");
        excludePackagePrefix.add("javax/");
        excludePackagePrefix.add("sun/");
        excludePackagePrefix.add("com/sun/");
        excludePackagePrefix.add("com/intellij/");

        // 不注入 java-monitor 本身
        excludePackagePrefix.add("org/github/java/monitor");

        // 默认注入的 package
        includePackagePrefix.add("net/paoding/rose/jade/context/JadeInvocationHandler"); //Jade
        includePackagePrefix.add("org/apache/ibatis/binding/MapperProxy"); //Mybatis
        includePackagePrefix.add("com/alibaba/dubbo/rpc/proxy/InvokerInvocationHandler"); //DUBBO
        includePackagePrefix.add("org/apache/dubbo/rpc/proxy/InvokerInvocationHandler"); //DUBBO
        includePackagePrefix.add("com/alipay/sofa/rpc/proxy/jdk/JDKInvocationHandler"); //SOFA jdk-proxy
        includePackagePrefix.add("com/weibo/api/motan/proxy/RefererInvocationHandler"); //Motan

        //默认不注入的method
        excludeMethods.add("main");
        excludeMethods.add("premain");
        excludeMethods.add("getClass"); //java.lang.Object
        excludeMethods.add("hashCode"); //java.lang.Object
        excludeMethods.add("equals"); //java.lang.Object
        excludeMethods.add("clone"); //java.lang.Object
        excludeMethods.add("toString"); //java.lang.Object
        excludeMethods.add("notify"); //java.lang.Object
        excludeMethods.add("notifyAll"); //java.lang.Object
        excludeMethods.add("wait"); //java.lang.Object
        excludeMethods.add("finalize"); //java.lang.Object
        excludeMethods.add("afterPropertiesSet"); //spring
    }

    /**
     * @param innerClassName : 形如: org/github/java/monitor/transform/ProfilingFilter
     * @return : true->不需要修改字节码  false->需要修改字节码
     */
    public static boolean isNotNeedInject(String innerClassName) {
        if (StringUtils.isBlank(innerClassName)) {
            return true;
        }

        //内部类
        if (innerClassName.indexOf('$') >= 0) {
            return true;
        }

        return isMatch(innerClassName, excludePackagePrefix, excludePackageExpression);
    }

    private static boolean isMatch(String innerClassName, Set<String> pkgPrefixSet, Set<String> pkgExpSet) {
        if (pkgPrefixSet.stream().anyMatch(innerClassName::startsWith)) {
            return true;
        }

        if (pkgExpSet.stream().anyMatch(express -> StrMatchUtils.isMatch(innerClassName, express))) {
            return true;
        }

        return false;
    }

    public static void addExcludePackage(String pkg) {
        if (StringUtils.isBlank(pkg)) {
            return;
        }

        addPackages(pkg, excludePackagePrefix, excludePackageExpression);
    }

    private static void addPackages(String packages, Set<String> pkgPrefixSet, Set<String> pkgExpSet) {
        Set<String> pkgSet = PkgExpUtils.parse(packages);
        for (String pkg : pkgSet) {
            pkg = preprocess(pkg);
            if (pkg.indexOf('*') > 0) {
                pkgExpSet.add(pkg);
            } else {
                pkgPrefixSet.add(pkg);
            }
        }
    }

    private static String preprocess(String pkg) {
        return pkg.replace('.', '/').trim();
    }

    /**
     * @param innerClassName : 形如: cn/myperf4j/core/ProfilingFilter
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    public static boolean isNeedInject(String innerClassName) {
        if (innerClassName == null) {
            return false;
        }

        return isMatch(innerClassName, includePackagePrefix, includePackageExpression);
    }

    public static void addIncludePackage(String pkg) {
        if (StrUtils.isEmpty(pkg)) {
            return;
        }

        addPackages(pkg, includePackagePrefix, includePackageExpression);
    }

    public static Set<String> getExcludePackagePrefix() {
        return new HashSet<>(excludePackagePrefix);
    }

    public static Set<String> getIncludePackagePrefix() {
        return new HashSet<>(includePackagePrefix);
    }

    /**
     * @param methodName
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    public static boolean isNotNeedInjectMethod(String methodName) {
        if (methodName == null) {
            return false;
        }

        if (isSpecialMethod(methodName)) {
            return true;
        }

        return excludeMethods.contains(methodName);
    }

    private static boolean isSpecialMethod(String methodName) {
        int symbolIndex = methodName.indexOf('$');
        if (symbolIndex < 0) {
            return false;
        }

        int leftParenIndex = methodName.indexOf('(');
        return leftParenIndex < 0 || symbolIndex < leftParenIndex;
    }

    public static void addExcludeMethods(String method) {
        if (method == null) {
            return;
        }

        excludeMethods.add(method.trim());
    }

    public static Set<String> getExcludeMethods() {
        return new HashSet<>(excludeMethods);
    }

    /**
     * @param classLoader
     */
    public static void addExcludeClassLoader(String classLoader) {
        excludeClassLoader.add(classLoader);
    }

    /**
     * 是否是不需要注入的类加载器
     *
     * @param classLoader
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    public static boolean isNotNeedInjectClassLoader(String classLoader) {
        return excludeClassLoader.contains(classLoader);
    }
}
