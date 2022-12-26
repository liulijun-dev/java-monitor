package org.github.java.monitor.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.github.java.monitor.bootstrap.ApplicationContext;

import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;
import static org.github.java.monitor.constant.PropertyKeys.HttpServer.ACCEPT_COUNT;
import static org.github.java.monitor.constant.PropertyKeys.HttpServer.MAX_WORKERS;
import static org.github.java.monitor.constant.PropertyKeys.HttpServer.MIN_WORKERS;
import static org.github.java.monitor.constant.PropertyKeys.HttpServer.PORT;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/9
 */
@Getter
@Setter
@Slf4j
public class HttpServerConfig {

    private int preferencePort;

    private int minPort;

    private int maxPort;

    private int minWorkers;

    private int maxWorkers;

    private int acceptCount;

    @Override
    public String toString() {
        return "HttpServerConfig{" +
                "preferencePort=" + preferencePort +
                ", minPort=" + minPort +
                ", maxPort=" + maxPort +
                ", minWorkers=" + minWorkers +
                ", maxWorkers=" + maxWorkers +
                ", acceptCount=" + acceptCount +
                '}';
    }

    public static HttpServerConfig loadHttpServerConfig() {
        MonitorProperties monitorProperties = ApplicationContext.getInstance(MonitorProperties.class);
        assert  monitorProperties != null;

        String portStr = monitorProperties.getStr(PORT);
        if (StringUtils.isBlank(portStr)) {
            portStr = "2048,2000,2040";
            log.info(PORT.key() + " is not configured, so use '" + portStr + "' as default.");
        }

        final HttpServerConfig config = new HttpServerConfig();
        completePorts(config, portStr);
        config.setMinWorkers(monitorProperties.getInt(MIN_WORKERS, 1));
        config.setMaxWorkers(monitorProperties.getInt(MAX_WORKERS, 2));
        config.setAcceptCount(monitorProperties.getInt(ACCEPT_COUNT, 1024));
        return config;
    }

    private static void completePorts(final HttpServerConfig config, final String portStr) {
        final List<String> ports = Arrays.asList(StringUtils.split(portStr, ','));
        if (ports.size() != 3) {
            config.setPreferencePort(parseInt(ports.get(0), 2048));
            config.setMinPort(2000);
            config.setMaxPort(2040);
            return;
        }

        config.setPreferencePort(parseInt(ports.get(0), 2048));
        config.setMinPort(parseInt(ports.get(1), 2000));
        config.setMaxPort(parseInt(ports.get(2), 2040));
    }
}
