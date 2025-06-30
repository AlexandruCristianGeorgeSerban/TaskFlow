package com.taskflow.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// Gestionează configurația aplicației încărcată dintr-un fișier properties.
public class PropertiesManager {
    private static final String CONFIG_FILE = "/config.properties"; // Calea în resurse
    private static Properties props = new Properties(); // Proprietățile încărcate

    static {
        try (InputStream in = PropertiesManager.class.getResourceAsStream(CONFIG_FILE)) {
            if (in != null) {
                props.load(in); // Încarcă proprietățile
            } else {
                throw new RuntimeException("Configuration file 'config.properties' not found in resources.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration: " + e.getMessage(), e);
        }
    }

    // Returnează valoarea proprietății pentru cheia dată (sau null dacă nu există)
    public static String get(String key) {
        return props.getProperty(key);
    }

    // Returnează valoarea proprietății cu valoare implicită dacă nu există
    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}
