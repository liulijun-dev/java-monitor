package org.github.java.monitor.bootstrap;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.github.java.monitor.constant.PropertyKeys.Basic.PROPERTIES_FILE_DIR;
import static org.github.java.monitor.constant.PropertyKeys.PRO_FILE_NAME;
import static org.github.java.monitor.constant.PropertyValues.DEFAULT_PRO_FILE;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Bootstrap {
    private static final Bootstrap INSTANCE = new Bootstrap();

    public static Bootstrap getInstance() {
        return INSTANCE;
    }


    private boolean initProperties() {
        final String configFilePath = System.getProperty(PRO_FILE_NAME, DEFAULT_PRO_FILE);
        try (InputStream in = new FileInputStream(configFilePath)) {
            Properties properties = new Properties();
            properties.load(in);

            properties.put(PROPERTIES_FILE_DIR.key(), getConfigFileDir(configFilePath));
            return MyProperties.initial(properties);
        } catch (IOException e) {
            log.error("Bootstrap.initProperties() fail", e);
        }
        return false;
    }

    private String getConfigFileDir(String configFilePath) {
        if (System.getProperty("os.name").startsWith("windows")) {
            final int idx = configFilePath.lastIndexOf('\\');
            return configFilePath.substring(0, idx + 1);
        }

        final int idx = configFilePath.lastIndexOf('/');
        return configFilePath.substring(0, idx + 1);
    }
}
