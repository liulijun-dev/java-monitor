package org.github.java.monitor.config;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/5
 */
public final class ConfigKey {

    private final String key;

    private final String legacyKey;

    private ConfigKey(String key, String legacyKey) {
        this.key = key;
        this.legacyKey = legacyKey;
    }

    public String key() {
        return key;
    }

    public String legacyKey() {
        return legacyKey;
    }

    public static ConfigKey of(String key, String legacyKey) {
        return new ConfigKey(key, legacyKey);
    }
}
