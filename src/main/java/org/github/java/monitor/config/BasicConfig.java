package org.github.java.monitor.config;

import org.apache.commons.lang3.StringUtils;
import org.github.java.monitor.bootstrap.ApplicationContext;

import static com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl.DEBUG;
import static org.github.java.monitor.constant.PropertyKeys.Basic.APP_NAME;
import static org.github.java.monitor.constant.PropertyKeys.Basic.PROPERTIES_FILE_DIR;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/9
 */
public class BasicConfig {

    private String appName;

    private String configFileDir;

    private boolean debug;

    public String appName() {
        return appName;
    }

    public void appName(String appName) {
        if (StringUtils.isBlank(appName)) {
            throw new IllegalArgumentException("AppName is required!!!");
        }
        this.appName = appName;
    }

    public String configFileDir() {
        return configFileDir;
    }

    public void configFileDir(String configFileDir) {
        this.configFileDir = configFileDir;
    }

    public boolean debug() {
        return debug;
    }

    public void debug(boolean debug) {
        this.debug = debug;
    }

    public String sysProfilingParamsFile() {
        return configFileDir + "." + appName + "_SysGenProfilingFile";
    }

    @Override
    public String toString() {
        return "BasicConfig{" +
                "appName='" + appName + '\'' +
                ", configFileDir='" + configFileDir + '\'' +
                ", debug=" + debug +
                '}';
    }

    public static BasicConfig loadBasicConfig() {
        MonitorProperties monitorProperties = ApplicationContext.getInstance(MonitorProperties.class);
        assert monitorProperties != null;

        String appName = monitorProperties.getStr(APP_NAME);
        if (StringUtils.isBlank(appName)) {
            throw new IllegalArgumentException(APP_NAME.key() + "|" + APP_NAME.legacyKey() + " is required!!!");
        }

        BasicConfig config = new BasicConfig();
        config.appName(appName);
        config.debug(monitorProperties.getBoolean(DEBUG, false));
        config.configFileDir(monitorProperties.getStr(PROPERTIES_FILE_DIR));
        return config;
    }
}
