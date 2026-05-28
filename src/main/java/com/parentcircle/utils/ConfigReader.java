package com.parentcircle.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPS = load();

    private ConfigReader() {}

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in != null) p.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
        return p;
    }

    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;
        return PROPS.getProperty(key);
    }

    // 1st - System property (-Dbase.url from Maven/Jenkins)
// 2nd - config.properties file
// 3rd - fallback default

    public static String get(String key, String defaultValue) {
        // First check system property (Jenkins -Dbase.url)
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isEmpty()) {
            return sysProp;
        }

        // Then check config.properties
        String configProp = PROPS.getProperty(key);
        if (configProp != null && !configProp.isEmpty()) {
            return configProp;
        }

        // Finally fallback
        return defaultValue;
    }

    public static boolean getBool(String key, boolean fallback) {
        String v = get(key);
        return v == null ? fallback : Boolean.parseBoolean(v);
    }
}
