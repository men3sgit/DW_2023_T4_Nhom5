package com.menes.scripts;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;  // Fix the import statement
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JSoupVietcombank {
    public static void main(String[] args) {

        String xmlUrl = "https://portal.vietcombank.com.vn/Usercontrols/TVPortal.TyGia/pXML.aspx";

        try {
            String xmlData = fetchXmlData(xmlUrl);
            System.out.println(xmlData);
            Document doc = Jsoup.parse(xmlData);

            // Extract DateTime
            String dateTime = doc.select("DateTime").text();
            System.out.println("DateTime: " + dateTime);

            // Extract exchange rates
            Elements exrates = doc.select("Exrate");
            for (Element exrate : exrates) {
                String currencyCode = exrate.attr("CurrencyCode");
                String currencyName = exrate.attr("CurrencyName");
                String buy = exrate.attr("Buy");
                String transfer = exrate.attr("Transfer");
                String sell = exrate.attr("Sell");

                System.out.println("Currency: " + currencyCode);
                System.out.println("Name: " + currencyName);
                System.out.println("Buy: " + buy);
                System.out.println("Transfer: " + transfer);
                System.out.println("Sell: " + sell);
                System.out.println("-----------------------------");
            }

            // Extract source
            String source = doc.select("Source").text();
            System.out.println("Source: " + source);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String fetchXmlData(String xmlUrl) throws IOException, MalformedURLException {
        URL url = new URL(xmlUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request method
        connection.setRequestMethod("GET");

        // Get input stream from the connection
        try (InputStream inputStream = connection.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            StringBuilder response = new StringBuilder();
            String line;

            // Read the XML data
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();
        } finally {
            // Disconnect the connection
            connection.disconnect();
        }
    }
}
