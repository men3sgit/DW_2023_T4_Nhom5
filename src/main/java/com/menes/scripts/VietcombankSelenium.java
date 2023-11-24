package com.menes.scripts;

import com.opencsv.CSVWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.text.ParseException;
import java.util.*;

import static com.menes.scripts.Configuration.previousDay;

public class VietcombankSelenium {

    /**
     * run method to execute the Vietcombank scraping process.
     *
     * @throws InterruptedException If the thread is interrupted during sleep.
     * @throws ParseException       If there is an error parsing a date.
     * @throws IOException          If an I/O error occurs.
     */
    public static void run() throws IOException, ParseException, InterruptedException {
        Configuration configuration = new Configuration();
        WebDriver driver = initializeDriver(configuration);

        try {
            performScraping(driver, configuration);
        } finally {
            // Close resources
            driver.quit();
        }
    }

    /**
     * Performs the scraping of exchange rate information from the Vietcombank website.
     *
     * @param driver        The WebDriver instance for interacting with the browser.
     * @param configuration The configuration settings for the scraper.
     * @throws InterruptedException If the thread is interrupted during sleep.
     * @throws ParseException       If there is an error parsing a date.
     * @throws IOException          If an I/O error occurs.
     */
    private static void performScraping(WebDriver driver, Configuration configuration)
            throws InterruptedException, ParseException, IOException {
        // Open CSV file for writing
        String csvFileName = configuration.getCsvFileNameWithTimestamp();
        File csvFile = new File(csvFileName);

        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile))) {
            // Write header to CSV file
            csvWriter.writeNext(new String[]{"Code", "Name", "Cash Buying", "Telegraphic Buying", "Selling", "Time", "Date", "Source"});
            Thread.sleep(3000);
            WebElement datePicker = driver.findElement(By.id("datePicker"));

            // Replace the hard-coded date with a variable
            String currentDate = configuration.getScrapeStartDate();

            while (!currentDate.equals(configuration.getScrapeEndDate())) {
                datePicker.clear();
                datePicker.sendKeys(currentDate);
                Thread.sleep(3000);

                WebElement loadMoreButton = driver.findElement(By.id("load-more-label"));
                loadMoreButton.sendKeys(Keys.ENTER);

                // Find and extract information from the website
                List<WebElement> rows = driver.findElements(By.cssSelector("table.table-responsive > tbody tr"));
                String[] dateTimeArray = driver.findElement(By.cssSelector(".annotate__content strong")).getText().split("\\s+");
                String time = dateTimeArray[1];
                String dateTime = dateTimeArray[3];

                for (WebElement row : rows) {
                    List<WebElement> columns = row.findElements(By.tagName("td"));
                    String code = columns.get(0).getText();
                    String name = columns.get(1).getText();
                    String cashBuying = columns.get(2).getText();
                    String telegraphicBuying = columns.get(3).getText();
                    String selling = columns.get(4).getText();

                    // Print to console
                    System.out.println(String.format("%s %s %s %s %s %s %s", code, name, cashBuying, telegraphicBuying, selling, dateTime, "Vietcombank"));

                    // Write to CSV file
                    csvWriter.writeNext(new String[]{code, name, cashBuying, telegraphicBuying, selling, time, dateTime, "Vietcombank"});
                }

                // Move to the previous day
                currentDate = previousDay(currentDate);
            }
        }
        // Close CSV writer
    }

    /**
     * Initializes the WebDriver for Chrome based on the provided configuration.
     *
     * @param configuration The configuration settings for the scraper.
     * @return An instance of WebDriver configured for Chrome.
     */
    private static WebDriver initializeDriver(Configuration configuration) {
        System.setProperty("webdriver.chrome.driver", configuration.getChromeDriverPath());
        WebDriver driver = new ChromeDriver();
        driver.get(configuration.getWebsiteUrl());
        driver.manage().window().maximize();
        return driver;
    }
}
