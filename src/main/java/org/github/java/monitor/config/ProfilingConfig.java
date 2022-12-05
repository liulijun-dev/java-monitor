package org.github.java.monitor.config;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
public final class ProfilingConfig {

    private static BasicConfig BASIC_CONFIG;

    private static HttpServerConfig HTTP_SERVER_CONFIG;

    private static MetricsConfig METRICS_CONFIG;

    private static FilterConfig FILTER_CONFIG;

    private static InfluxDbConfig INFLUX_DB_CONFIG;

    private static RecorderConfig RECORDER_CONFIG;

    private ProfilingConfig() {
        //empty
    }

    public static BasicConfig basicConfig() {
        return BASIC_CONFIG;
    }

    public static void basicConfig(BasicConfig basicConfig) {
        BASIC_CONFIG = basicConfig;
    }

    public static HttpServerConfig httpServerConfig() {
        return HTTP_SERVER_CONFIG;
    }

    public static void httpServerConfig(HttpServerConfig httpServerConfig) {
        HTTP_SERVER_CONFIG = httpServerConfig;
    }

    public static InfluxDbConfig influxDBConfig() {
        return INFLUX_DB_CONFIG;
    }

    public static void influxDBConfig(InfluxDbConfig influxDBConfig) {
        ProfilingConfig.INFLUX_DB_CONFIG = influxDBConfig;
    }

    public static MetricsConfig metricsConfig() {
        return METRICS_CONFIG;
    }

    public static void metricsConfig(MetricsConfig metricsConfig) {
        ProfilingConfig.METRICS_CONFIG = metricsConfig;
    }

    public static FilterConfig filterConfig() {
        return FILTER_CONFIG;
    }

    public static void filterConfig(FilterConfig filterConfig) {
        ProfilingConfig.FILTER_CONFIG = filterConfig;
    }

    public static RecorderConfig recorderConfig() {
        return RECORDER_CONFIG;
    }

    public static void recorderConfig(RecorderConfig recorderConfig) {
        RECORDER_CONFIG = recorderConfig;
    }
}
