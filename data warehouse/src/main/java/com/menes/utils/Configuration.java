package com.menes.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private static final String CONFIG_PATH = "../config.properties";
    private static Configuration instance;
    public static final Properties PROPERTIES;

    static {
        PROPERTIES = new Properties();
        try {
            PROPERTIES.load(new FileInputStream(CONFIG_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Configuration() {
    }

    public static Configuration getInstance() {
        return instance != null ? instance : new Configuration();
    }

    // Get value from the config file
    public String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    // Example methods for getting specific properties

    // DB control properties
    public String getDBControlHost() {
        return getProperty("db.control.host");
    }

    public int getDBControlPort() {
        return Integer.parseInt(getProperty("db.control.port"));
    }

    public String getDBControlUsername() {
        return getProperty("db.control.username");
    }

    public String getDBControlPassword() {
        return getProperty("db.control.password");
    }

    public String getDBControlName() {
        return getProperty("db.control.name");
    }

    // DB staging properties
    public String getDBStagingHost() {
        return getProperty("db.staging.host");
    }

    public String getDBStagingUsername() {
        return getProperty("db.staging.username");
    }

    public String getDBStagingPassword() {
        return getProperty("db.staging.password");
    }

    public int getDBStagingPort() {
        return Integer.parseInt(getProperty("db.staging.port"));
    }

    public String getDBDataWarehouseName() {
        return getProperty("db.wh.name");
    }

    public String getDBDataWarehousePort() {
        return getProperty("db.wh.port");
    }

    public String getDBDataWarehouseHost() {
        return getProperty("db.wh.host");
    }

    public String getDBDataWarehouseUsername() {
        return getProperty("db.wh.username");
    }

    public String getDBDataWarehousePassword() {
        return getProperty("db.wh.password");
    }

    public String getDBStagingName() {
        return getProperty("db.staging.name");
    }


    // Environment properties
    public String getChromeDriverPath() {
        return getProperty("env.chromedriver.path");
    }


    public String getServerMailUsername() {
        return getProperty("server.mail.username");
    }

    public String getServerMailPassword() {
        return getProperty("server.mail.password");
    }
}
