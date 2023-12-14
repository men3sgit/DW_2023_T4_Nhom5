package com.menes.extract;

import com.menes.utils.Configuration;
import com.menes.utils.ExceptionMailer;
import com.menes.utils.Logger;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class VietcombankScraping {
    private static final Configuration CONFIGURATION = Configuration.getInstance();
    static int count = 0;
    /**
     * run method to execute the Vietcombank scraping process.
     *
     * @throws InterruptedException If the thread is interrupted during sleep.
     * @throws ParseException       If there is an error parsing a date.
     * @throws IOException          If an I/O error occurs.
     */
    private static String csvPath;

    public static void run(String resultPath) throws IOException, ParseException, InterruptedException {
        Configuration configuration = Configuration.getInstance();
        csvPath = resultPath + "extract_data_" + DateTimeFormatter.ofPattern("dd-MM-yy_HH-mm-ss").format(LocalDateTime.now()) + ".csv";

        WebDriver driver = initializeDriver();

        try {
            performScraping(driver, configuration);
            System.err.println("Crawled " + count + " data rows");
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
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Open CSV file for writing
        char customDelimiter = '\t';
        try (CSVWriter csvWriter = (CSVWriter) new CSVWriterBuilder(new FileWriter(csvPath)).withSeparator(customDelimiter).withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER).build()) {
            // Write header to CSV file
            csvWriter.writeNext(new String[]{"Code", "Name", "Cash Buying", "Telegraphic Buying", "Selling", "Time", "Date", "Source", "Crawled Date", "Crawled Time"});
            Thread.sleep(3000);
            WebElement datePicker = driver.findElement(By.id("datePicker"));
            // Replace the hard-coded date with a variable
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            String currentDate = formatter.format(LocalDateTime.now());
            datePicker.clear();
            datePicker.sendKeys(currentDate);
            Thread.sleep(3000);

            WebElement loadMoreButton = driver.findElement(By.id("load-more-label"));
            loadMoreButton.sendKeys(Keys.ENTER);

            // Find and extract information from the website
            List<WebElement> rows = driver.findElements(By.cssSelector("table.table-responsive > tbody tr"));
            String[] dateTimeArray = driver.findElement(By.cssSelector(".annotate__content strong")).getText().split("\\s+");
            String time = dateTimeArray[1] + ":00";
            String date = dateTimeArray[3];
            String timeNow = timeFormatter.format(LocalDateTime.now());
            rows.forEach(row -> {
                List<String> columns = row.findElements(By.tagName("td")).stream().map(WebElement::getText).toList();

                // Extract values from columns
                String code = columns.get(0);
                String name = columns.get(1);
                String cashBuying = columns.get(2);
                String telegraphicBuying = columns.get(3);
                String selling = columns.get(4);
                String bank = "Vietcombank";
                // Print to console
                System.out.println(String.format("%s %s %s %s %s %s %s %s %s", code, name, cashBuying, telegraphicBuying, selling, time, bank, date, timeNow));

                // Write to CSV file
                csvWriter.writeNext(new String[]{code, name, cashBuying, telegraphicBuying, selling, time, date, bank, dateFormatter.format(LocalDateTime.now()), timeNow});

                count++;
            });

            // Move to the next day
//                currentDate = nextDay(currentDate);
        }
        // Close CSV writer
    }
//    }

    /**
     * Initializes the WebDriver for Chrome based on the provided configuration.
     *
     * @return An instance of WebDriver configured for Chrome.
     */
    private static WebDriver initializeDriver() {
        System.setProperty("webdriver.chrome.driver", CONFIGURATION.getChromeDriverPath());
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.vietcombank.com.vn/KHCN/Cong-cu-tien-ich/Ty-gia");
        driver.manage().window().maximize();
        return driver;
    }

}
