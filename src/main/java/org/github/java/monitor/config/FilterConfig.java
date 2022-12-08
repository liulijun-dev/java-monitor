package org.github.java.monitor.config;

import org.apache.commons.lang3.StringUtils;
import org.github.java.monitor.bootstrap.ApplicationContext;

import static org.github.java.monitor.constant.PropertyKeys.Filter.CLASS_LOADERS_EXCLUDE;
import static org.github.java.monitor.constant.PropertyKeys.Filter.METHODS_EXCLUDE;
import static org.github.java.monitor.constant.PropertyKeys.Filter.METHODS_EXCLUDE_PRIVATE;
import static org.github.java.monitor.constant.PropertyKeys.Filter.PACKAGES_EXCLUDE;
import static org.github.java.monitor.constant.PropertyKeys.Filter.PACKAGES_INCLUDE;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
public class FilterConfig {

    private String excludeClassLoaders;

    private String includePackages;

    private String excludePackages;

    private String excludeMethods;

    private boolean excludePrivateMethod;

    public static FilterConfig loadFilterConfig() {
        MonitorProperties monitorProperties = ApplicationContext.getInstance(MonitorProperties.class);
        assert monitorProperties != null;

        final String includePackages = monitorProperties.getStr(PACKAGES_INCLUDE);
        if (StringUtils.isBlank(includePackages)) {
            throw new IllegalArgumentException(String.format("%s or %s is required", PACKAGES_INCLUDE.key(), PACKAGES_INCLUDE.legacyKey()));
        }

        final FilterConfig config = new FilterConfig();
        config.includePackages(includePackages);
        config.excludeClassLoaders(monitorProperties.getStr(CLASS_LOADERS_EXCLUDE));
        config.excludePackages(monitorProperties.getStr(PACKAGES_EXCLUDE));
        config.excludeMethods(monitorProperties.getStr(METHODS_EXCLUDE));
        config.excludePrivateMethod(monitorProperties.getBoolean(METHODS_EXCLUDE_PRIVATE, true));
        return config;
    }

    public String excludeClassLoaders() {
        return excludeClassLoaders;
    }

    public void excludeClassLoaders(String excludeClassLoaders) {
        this.excludeClassLoaders = excludeClassLoaders;
    }

    public String includePackages() {
        return includePackages;
    }

    public void includePackages(String includePackages) {
        this.includePackages = includePackages;
    }

    public String excludePackages() {
        return excludePackages;
    }

    public void excludePackages(String excludePackages) {
        this.excludePackages = excludePackages;
    }

    public String excludeMethods() {
        return excludeMethods;
    }

    public void excludeMethods(String excludeMethods) {
        this.excludeMethods = excludeMethods;
    }

    public boolean excludePrivateMethod() {
        return excludePrivateMethod;
    }

    public void excludePrivateMethod(boolean excludePrivateMethod) {
        this.excludePrivateMethod = excludePrivateMethod;
    }

    @Override
    public String toString() {
        return "FilterConfig{" +
                "excludeClassLoaders='" + excludeClassLoaders + '\'' +
                ", includePackages='" + includePackages + '\'' +
                ", excludePackages='" + excludePackages + '\'' +
                ", excludeMethods='" + excludeMethods + '\'' +
                ", excludePrivateMethod=" + excludePrivateMethod +
                '}';
    }
}
