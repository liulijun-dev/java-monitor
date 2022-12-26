package org.github.java.monitor.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.github.java.monitor.bootstrap.ApplicationContext;

import static org.github.java.monitor.constant.PropertyKeys.HttpServer.PORT;
import static org.github.java.monitor.constant.PropertyKeys.InfluxDB.CONN_TIMEOUT;
import static org.github.java.monitor.constant.PropertyKeys.InfluxDB.DATABASE;
import static org.github.java.monitor.constant.PropertyKeys.InfluxDB.HOST;
import static org.github.java.monitor.constant.PropertyKeys.InfluxDB.ORG_NAME;
import static org.github.java.monitor.constant.PropertyKeys.InfluxDB.PASSWORD;
import static org.github.java.monitor.constant.PropertyKeys.InfluxDB.READ_TIMEOUT;
import static org.github.java.monitor.constant.PropertyKeys.InfluxDB.USERNAME;
import static org.github.java.monitor.constant.PropertyKeys.InfluxDB.VERSION;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/26
 */
@Slf4j
@Getter
@Builder
@AllArgsConstructor
public class InfluxDbConfig {

    private String version;

    private String host;

    private int port;

    private String orgName;

    private String database;

    private int connectTimeout;

    private int readTimeout;

    private String username;

    private String password;


    @Override
    public String toString() {
        return "InfluxDbConfig{" +
            "version='" + version + '\'' +
            ", host='" + host + '\'' +
            ", port=" + port +
            ", orgName='" + orgName + '\'' +
            ", database='" + database + '\'' +
            ", connectTimeout=" + connectTimeout +
            ", readTimeout=" + readTimeout +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            '}';
    }

    public static InfluxDbConfig loadInfluxDbConfig() {
        MonitorProperties monitorProperties = ApplicationContext.getInstance(MonitorProperties.class);
        assert monitorProperties != null;

        String version = monitorProperties.getStr(VERSION);
        if (StringUtils.isBlank(version)) {
            version = "1.x";
            log.info(VERSION.key() + " is not configured, so use '1.x' as default version.");
        }

        String host = monitorProperties.getStr(HOST);
        if (StringUtils.isBlank(host)) {
            host = "127.0.0.1";
            log.info(HOST.key() + " is not configured, so use '127.0.0.1' as default host.");
        }

        Integer port = monitorProperties.getInt(PORT);
        if (port == null) {
            port = 8086;
            log.info(PORT.key() + " is not configured, so use '8086' as default port.");
        }

        return InfluxDbConfig.builder()
            .version(version)
            .host(host)
            .port(port)
            .orgName(monitorProperties.getStr(ORG_NAME))
            .database(monitorProperties.getStr(DATABASE))
            .username(monitorProperties.getStr(USERNAME))
            .password(monitorProperties.getStr(PASSWORD))
            .connectTimeout(monitorProperties.getInt(CONN_TIMEOUT, 3000))
            .readTimeout(monitorProperties.getInt(READ_TIMEOUT, 5000))
            .build();
    }
}
