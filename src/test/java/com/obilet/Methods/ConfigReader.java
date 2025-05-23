package com.obilet.Methods;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader class is used to read application configuration settings.
 */
public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        String env = System.getenv("GAUGE_ENV");
        if (env == null || env.isEmpty()) {
            env = "default";
        }
        System.out.println("Environment: " + env);
        // Corrected path: Remove "/src/test/resources" and start from "/env/"
        String propertiesFilePath = "/env/" + env + "/config.properties";
        try (InputStream input = ConfigReader.class.getResourceAsStream(propertiesFilePath)) {
            if (input == null) {
                throw new RuntimeException("Properties file not found: " + propertiesFilePath);
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load properties file: " + propertiesFilePath, ex);
        }
    }

    /**
     * Returns the configuration value for the given key.
     *
     * @param key the key to look up
     * @return the configuration value or null if the key is not found
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Returns the configuration value for the given key, or the default value if the key is not found.
     *
     * @param key          the key to look up
     * @param defaultValue the default value to return if the key is not found
     * @return the configuration value or the default value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}