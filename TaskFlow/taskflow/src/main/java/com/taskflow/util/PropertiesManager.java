package com.taskflow.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {
    private static final String CONFIG_FILE = "/config.properties";
    private static Properties props = new Properties();

    static {
        try (InputStream in = PropertiesManager.class.getResourceAsStream(CONFIG_FILE)) {
            if (in != null) {
                props.load(in);
            } else {
                throw new RuntimeException("Configuration file 'config.properties' not found in resources.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration: " + e.getMessage(), e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}