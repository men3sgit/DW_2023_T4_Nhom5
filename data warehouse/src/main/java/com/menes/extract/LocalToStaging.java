package com.menes.extract;

import com.menes.model.CurrencyExchange;
import com.menes.utils.Configuration;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * The LocalToStaging class reads data from a CSV file and inserts it into a PostgreSQL database using JDBI.
 */
public class LocalToStaging {

    /** The singleton instance of the Configuration class used for retrieving database connection settings. */
    private static final Configuration CONFIGURATION = Configuration.getInstance();

    /**
     * Gets the path to the latest CSV file in the specified directory.
     *
     * @param extractFilePath The path to the directory containing CSV files.
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
     *
     * @param extractFilePath The path to the directory containing CSV files.
     * @throws IOException If an I/O error occurs.
     */
    public static void run(String extractFilePath) throws IOException {
        // Step 1: Constructing the JDBC URL for the staging database connection
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s",
                CONFIGURATION.getDBStagingHost(),
                CONFIGURATION.getDBStagingPort(),
                CONFIGURATION.getDBStagingName());

        String username = CONFIGURATION.getDBStagingUsername();
        String password = CONFIGURATION.getDBStagingPassword();

        //2: Creating a Jdbi instance and installing the SqlObjectPlugin
        Jdbi jdbi = Jdbi.create(jdbcUrl, username, password).installPlugin(new SqlObjectPlugin());

        // 3: Reading the CSV file and inserting data into the PostgreSQL database
        BufferedReader reader = new BufferedReader(new FileReader(getExtractFile(extractFilePath)));
        // Skip the header row
        reader.readLine();

        // 4: Using a Jdbi handle to execute database operations
        jdbi.useHandle(handle -> {
            handle.createUpdate("TRUNCATE TABLE currency_exchange").execute();
            String line;
            while ((line = reader.readLine()) != null) {
                // 5: Parse the CSV line
                String[] data = line.split("\\t");

                // Remove double quotes from each element in the data array
                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].replaceAll("\"", "");
                }

                // 6: Map CSV data to a Java bean
                CurrencyExchange currencyExchange = new CurrencyExchange(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9]);
                System.out.println(currencyExchange);

                // 7: Insert the data into the PostgreSQL table using JDBI
                handle.attach(CurrencyExchangeDao.class).insert(currencyExchange);
            }
        });

        // 8: Indicate the success of the data loading process
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
        @SqlUpdate("INSERT INTO currency_exchange (code, name, cash_buying, telegraphic_buying, selling, time, date, bank_name, crawled_date, crawled_time) " +
                "VALUES (:code, :name, :cashBuying, :telegraphicBuying, :selling, :time, :date, :bankName, :crawledDate, :crawledTime)")
        void insert(@BindBean CurrencyExchange currencyExchange);
    }
}
