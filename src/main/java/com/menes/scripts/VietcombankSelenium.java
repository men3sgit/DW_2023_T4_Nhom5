package com.menes.scripts;

import com.menes.scripts.db.CsvLineReader;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.menes.scripts.Configuration.nextDay;

public class VietcombankSelenium {

    /**
     * run method to execute the Vietcombank scraping process.
     *
     * @throws InterruptedException If the thread is interrupted during sleep.
     * @throws ParseException       If there is an error parsing a date.
     * @throws IOException          If an I/O error occurs.
     */
    public static void run() throws IOException, ParseException, InterruptedException {
        Configuration configuration = Configuration.getInstance();
        WebDriver driver = initializeDriver(configuration);

        try {
            performScraping(driver, configuration);

        } catch (Exception e) {
            ExceptionMailer.handleException(e);
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
    private static void performScraping(WebDriver driver, Configuration configuration) throws InterruptedException, ParseException, IOException {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");

        // Open CSV file for writing
//        String csvFileName = configuration.getCsvFileNameWithTimestamp();
        String csvFileName = Configuration.getInstance().getCsvFileNameWithTimestamp();
// Specify your custom delimiter (e.g., a tab character '\t')
        char customDelimiter = '\t';
        try (CSVWriter csvWriter = (CSVWriter) new CSVWriterBuilder(new FileWriter(csvFileName))
                .withSeparator(customDelimiter)
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .build()) {
            // Write header to CSV file
            csvWriter.writeNext(new String[]{"Code", "Name", "Cash Buying", "Telegraphic Buying", "Selling", "Time", "Date", "Source", "Crawled Date", "Crawled Time"});
            Thread.sleep(3000);
            WebElement datePicker = driver.findElement(By.id("datePicker"));

            // Replace the hard-coded date with a variable
            String currentDate = configuration.getScrapeStartDate();

            while (Integer.parseInt(currentDate) <= Integer.parseInt(configuration.getScrapeEndDate())) {
                datePicker.clear();
                datePicker.sendKeys(currentDate);
                Thread.sleep(3000);

                WebElement loadMoreButton = driver.findElement(By.id("load-more-label"));
                loadMoreButton.sendKeys(Keys.ENTER);

                // Find and extract information from the website
                List<WebElement> rows = driver.findElements(By.cssSelector("table.table-responsive > tbody tr"));
                String[] dateTimeArray = driver.findElement(By.cssSelector(".annotate__content strong")).getText().split("\\s+");
                String time = dateTimeArray[1];
                String date = dateTimeArray[3];

                rows.forEach(row -> {
                    List<String> columns = row.findElements(By.tagName("td")).stream().map(WebElement::getText).toList();

                    // Extract values from columns
                    String code = columns.get(0);
                    String name = columns.get(1);
                    String cashBuying = columns.get(2);
                    String telegraphicBuying = columns.get(3);
                    String selling = columns.get(4);

                    // Print to console
                    System.out.println(String.format("%s %s %s %s %s %s %s %s %s", code, name, cashBuying, telegraphicBuying, selling, time, "Vietcombank", date, timeFormatter.format(LocalDateTime.now())));

                    // Write to CSV file
                    csvWriter.writeNext(new String[]{code, name, cashBuying, telegraphicBuying, selling, time, date, "Vietcombank", dateFormatter.format(LocalDateTime.now()), timeFormatter.format(LocalDateTime.now())});
                });

                // Move to the next day
                currentDate = nextDay(currentDate);
            }
            // Close CSV writer
        }
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
