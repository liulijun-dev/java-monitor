package org.github.java.monitor.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/6
 */
@Slf4j
@RequiredArgsConstructor
public final class MonitorProperties {
    private final Properties properties;

    public String getStr(ConfigKey confKey) {
        final String str = doGetStr(confKey.key());
        if (str != null) {
            return trimToEmpty(str);
        }
        return trimToEmpty(doGetStr(confKey.legacyKey()));
    }

    public String getStr(String key) {
        return trimToEmpty(doGetStr(key));
    }

    private String doGetStr(String key) {
        checkState();

        final String value = System.getProperty(key);
        if (value != null) {
            return value;
        }
        return properties.getProperty(key);
    }

    public void setStr(String key, String value) {
        checkState();

        System.setProperty(key, value);
        properties.setProperty(key, value);
    }

    public String getStr(ConfigKey confKey, String defaultValue) {
        final String result = doGetStr(confKey.key());
        if (result != null) {
            return trimToEmpty(result);
        }
        return defaultValue;
    }

    public String getStr(String key, String defaultValue) {
        final String result = doGetStr(key);
        if (result != null) {
            return trimToEmpty(result);
        }
        return defaultValue;
    }

    public int getInt(ConfigKey confKey, int defaultValue) {
        final Integer result = getInt(confKey.key());
        if (result != null) {
            return result;
        }
        return getInt(confKey.legacyKey(), defaultValue);
    }

    public Integer getInt(ConfigKey confKey) {
        final Integer result = getInt(confKey.key());
        if (result != null) {
            return result;
        }
        return getInt(confKey.legacyKey());
    }

    public Integer getInt(String key) {
        final String result = doGetStr(key);
        if (result == null) {
            return null;
        }

        try {
            return Integer.valueOf(result);
        } catch (Exception e) {
            log.error(format("getInt(%s)", key), e);
        }
        return null;
    }

    public int getInt(String key, int defaultValue) {
        final String result = doGetStr(key);
        if (result == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(result);
        } catch (Exception e) {
            log.error(format("getInt(%s, %d)",key, defaultValue), e);
        }
        return defaultValue;
    }

    public long getLong(ConfigKey confKey, long defaultValue) {
        final Long l = getLong(confKey.key());
        if (l != null) {
            return l;
        }
        return getLong(confKey.legacyKey(), defaultValue);
    }

    public Long getLong(String key) {
        final String result = doGetStr(key);
        if (result == null) {
            return null;
        }

        try {
            return Long.valueOf(result);
        } catch (Exception e) {
            log.error(format("getLong(%s)", key), e);
        }
        return null;
    }

    public long getLong(String key, long defaultValue) {
        final String result = doGetStr(key);
        if (result == null) {
            return defaultValue;
        }

        try {
            return Long.parseLong(result);
        } catch (Exception e) {
            log.error(format("getLong(%s, %d)", key, defaultValue), e);
        }
        return defaultValue;
    }

    public long getLong(String key, long defaultValue, long minValue) {
        final long result = getLong(key, defaultValue);
        if (result <= minValue) {
            return minValue;
        }
        return result;
    }

    public boolean isSame(String key, String expectValue) {
        if (expectValue == null) {
            throw new IllegalArgumentException("isSame(" + key + ", null): expectValue must not null!!!");
        }
        return expectValue.equals(doGetStr(key));
    }

    public boolean getBoolean(ConfigKey confKey, boolean defaultValue) {
        final Boolean result = getBoolean(confKey.key());
        if (result != null) {
            return result;
        }
        return getBoolean(confKey.legacyKey(), defaultValue);
    }

    public Boolean getBoolean(String key) {
        final String result = doGetStr(key);
        if (result != null) {
            return result.equalsIgnoreCase("true");
        }
        return null;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        final String result = doGetStr(key);
        if (result != null) {
            return result.equalsIgnoreCase("true");
        }
        return defaultValue;
    }

    private void checkState() {
        if (properties == null) {
            throw new IllegalStateException("MonitorProperties is not initial yet!");
        }
    }
}
