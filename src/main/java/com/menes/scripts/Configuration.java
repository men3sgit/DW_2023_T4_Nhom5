package com.menes.scripts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class Configuration {
    private static final String CONFIG_FILE = "config.properties";
    private final Properties properties;
    private static Configuration configuration;

    public static Configuration getInstance() throws IOException {
        if (configuration == null) {
            return configuration = new Configuration();
        }
        return configuration;
    }

    private Configuration() throws IOException {
        properties = new Properties();
        try (InputStream input = Configuration.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new FileNotFoundException("Unable to find " + CONFIG_FILE);
            }
            properties.load(input);
        }
    }

    public String getAuthorName() {
        return properties.getProperty("author.name");
    }

    public String getScrapeStartDate() {
        return properties.getProperty("scrape.start-date");
    }

    public String getErrorFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_hhmmss_a");
        String timestamp = dateFormat.format(new Date());
        return properties.getProperty("error.file.name").replace("{timestamp}", timestamp);
    }

    public String getScrapeEndDate() {
        return properties.getProperty("scrape.end-date");
    }

    public String getChromeDriverPath() {
        return properties.getProperty("webdriver.chrome.driver");
    }

    public String getWebsiteUrl() {
        return properties.getProperty("website.url");
    }

    public String getCsvFileNameWithTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_hhmmss_a");
        String timestamp = dateFormat.format(new Date());
        return properties.getProperty("csv.file.name").replace("{timestamp}", timestamp);
    }

    public static String nextDay(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(date));
        calendar.add(Calendar.DATE, 1);
        return format.format(calendar.getTime());
    }

    public String getDBHost() {

        return properties.getProperty("db.host");
    }

    public String getDBPassword() {

        return properties.getProperty("db.password");
    }

    public String getDBUsername() {

        return properties.getProperty("db.username");
    }

    public String getDBPort() {

        return properties.getProperty("db.port");
    }

    public String getDBName() {

        return properties.getProperty("db.name");
    }


}
