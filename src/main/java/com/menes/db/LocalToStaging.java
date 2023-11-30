package com.menes.db;

import com.menes.utils.Configuration;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

/**
 * CsvLineReader class reads data from a CSV file and inserts it into a PostgreSQL database using JDBI.
 */
public class LocalToStaging {

    /**
     * Configuration instance.
     */
    private static final Configuration CONFIGURATION;

    static {
        try {
            Thread.sleep(4000);
            CONFIGURATION = Configuration.getInstance();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the path to the latest CSV file in the specified directory.
     *
     * @return The absolute path to the latest CSV file.
     */
    private static String getExtractFile(String extractFilePath) {
        File directory = new File(extractFilePath);
        File[] files = directory.listFiles();

        if (files != null && files.length > 0) {
            Arrays.sort(files, Comparator.comparing(File::lastModified).reversed());
            return files[0].getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * Runs the process of reading data from the CSV file and inserting it into the PostgreSQL database.
     */
    public static void run(String extractFilePath) throws IOException {
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", CONFIGURATION.getDBHost(), CONFIGURATION.getDBPort(), CONFIGURATION.getDBName());
        String username = CONFIGURATION.getDBUsername();
        String password = CONFIGURATION.getDBPassword();

        Jdbi jdbi = Jdbi.create(jdbcUrl, username, password).installPlugin(new SqlObjectPlugin());

        BufferedReader reader = new BufferedReader(new FileReader(getExtractFile(extractFilePath)));
        // Skip the header row
        reader.readLine();

        // Create a Jdbi handle
        jdbi.useHandle(handle -> {
            handle.createUpdate("TRUNCATE TABLE currency_exchange").execute();
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse the CSV line
                String[] data = line.split("\\t");

                // Remove double quotes from each element in the data array
                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].replaceAll("\"", "");
                }

                // Map CSV data to a Java bean (assuming CurrencyExchange is a class representing your table)
                CurrencyExchange currencyExchange = new CurrencyExchange(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9]);
                System.out.println(currencyExchange);

                // Insert the data into the PostgreSQL table using JDBI
                handle.attach(CurrencyExchangeDao.class).insert(currencyExchange);
            }
        });

        System.err.println("Data loaded successfully.");


    }

    /**
     * Interface for JDBI DAO to handle database operations for CurrencyExchange.
     */
    interface CurrencyExchangeDao {

        /**
         * Inserts CurrencyExchange data into the database.
         *
         * @param currencyExchange The CurrencyExchange object to insert.
         */
        @SqlUpdate("INSERT INTO currency_exchange (code, name, cash_buying, telegraphic_buying, selling, time, date, bank_name, crawled_date, crawled_time) " + "VALUES (:code, :name, :cashBuying, :telegraphicBuying, :selling, :time, :date, :bankName, :crawledDate, :crawledTime)")
        void insert(@BindBean CurrencyExchange currencyExchange);
    }
}
