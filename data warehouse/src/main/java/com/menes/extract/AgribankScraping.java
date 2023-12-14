package com.menes.extract;

import com.menes.utils.ExceptionMailer;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;

import javax.net.ssl.*;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AgribankScraping {

    public static void main(String[] args) {
        String url = "https://www.agribank.com.vn/vn/ty-gia"; // Replace with the actual URL

        try {
            // Create a custom TrustManager to trust all certificates
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Install the custom TrustManager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Disable hostname verification
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            // Send an HTTP request to the URL and get the response
            Connection.Response response = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .execute();

            // Parse the HTML content from the response
            Document document = response.parse();

            // Select the table element
            Element table = document.select("table").first();

            // Select all rows in the table body
            Elements rows = table.select("tbody tr");

            String csvFileName = "output.csv"; // Replace with your desired output CSV file name

            CSVWriter csvWriter = (CSVWriter) new CSVWriterBuilder(new FileWriter(csvFileName))
                    .withSeparator('\t')
                    .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                    .build();

            // Write header to CSV file
            csvWriter.writeNext(new String[]{"Code", "Name", "Cash Buying", "Telegraphic Buying", "Selling", "Time", "Date", "Source", "Crawled Date", "Crawled Time"});

            // Your existing code here...
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String time = "09:00:00";
            String today = dateFormatter.format(LocalDateTime.now());
            rows.forEach(row -> {
                Elements cells = row.select("td");
                // Extract values from columns
                String code = cells.get(0).text();
                String name = "-";
                String cashBuying = cells.get(1).text();
                String telegraphicBuying = cells.get(2).text();
                String selling = cells.get(3).text();
                String bank = "Agribank";

                // Extracted date and time


                // Write to CSV file
                csvWriter.writeNext(new String[]{code, name, cashBuying, telegraphicBuying, selling, time, today, bank, today, timeFormatter.format(LocalDateTime.now())});
            });

        } catch (IOException | KeyManagementException e) {
            ExceptionMailer.handleException(e);
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
}
