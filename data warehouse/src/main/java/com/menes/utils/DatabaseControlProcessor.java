package com.menes.utils;

import com.menes.extract.VietcombankScraping;
import com.menes.extract.LocalToStaging;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * The {@code DatabaseControlProcessor} class is responsible for managing and processing
 * configurations stored in a database. It reads configuration properties from an external
 * file, establishes a database connection, and processes each configuration entry based on
 * specified conditions.
 *
 * <p>The class performs actions such as updating the status of configurations, executing
 * scraping and transformation processes, and handling errors during the processing.
 *
 * <p>This class uses the Jdbi library for database interaction and relies on external
 * components like {@code VietcombankScraping} and {@code LocalToStaging} for specific
 * processing tasks.
 *
 * <p><strong>Note:</strong> Ensure that the necessary dependencies and libraries are
 * available for the proper functioning of this class.
 *
 * @author Menes
 * @version 1.0
 * @Email menesforenglish102@gmail.com
 * @since 2023-11-28
 */
public class DatabaseControlProcessor {
    private static final String CONFIG_FILE_PATH = "../config.properties";
    private static String host;
    private static String port;
    private static String username;
    private static String password;
    private static String dbName;
    private static int source;

    public static void run(String fSource) {
        source = Integer.parseInt(fSource);
        // 1. Load configuration properties and establish a database connection.
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);
            host = properties.getProperty("db.host");
            port = properties.getProperty("db.port");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");
            dbName = properties.getProperty("db.name");

            String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);

            //2. Initialize Jdbi with SQL object plugin for database interaction.
            Jdbi jdbi = Jdbi.create(jdbcUrl, username, password).installPlugin(new SqlObjectPlugin());

            //3. Process configurations using a Jdbi handle.
            jdbi.useHandle(handle -> processConfigs(handle));

        } catch (IOException e) {
            // Propagate any IO exceptions as runtime exceptions.
            throw new RuntimeException(e);
        }
    }


    /**
     * Processes all configurations in the database.
     *
     * @param handle The Jdbi {@code Handle} for database interaction.
     */
    private static void processConfigs(Handle handle) {
        // Retrieve all configurations from the 'configs' table and process each one.
        handle.createQuery("SELECT * FROM configs")
                .mapToMap()
                .list()
                .forEach(config -> processConfig(handle, config));
    }

    /**
     * Processes an individual configuration entry.
     *
     * @param handle The Jdbi {@code Handle} for database interaction.
     * @param config The configuration entry to be processed.
     */
    private static void processConfig(Handle handle, Map<String, Object> config) {
        // Check if the configuration is in the 'OFF' state before processing.
        String isRunning = (String) config.get("is_running");

        if (Status.OFF.toString().equals(isRunning)) {
            int id = (int) config.get("id");
            try {
                // Update the configuration status to indicate crawling.
                updateConfigStatus(handle, id, Status.RUNNING, Status.CRAWLING);

                String resultPath = (String) config.get("result_path");
                // Execute VietcombankScraping process.
                if (source == Source.SACOMBANK) {
                    SacombankScraping.run(resultPath);
                } else {
                    VietcombankScraping.run(resultPath);
                }

                // Update the configuration status to indicate crawling completion.
                updateConfigStatus(handle, id, Status.OFF, Status.CRAWLED);
                // Update the configuration status to indicate transforming.
                updateConfigStatus(handle, id, Status.RUNNING, Status.TRANSFORMING);

                // Execute LocalToStaging process.

                LocalToStaging.run(resultPath);

                // Update the configuration status to indicate transformation completion.
                updateConfigStatus(handle, id, Status.OFF, Status.TRANSFORMED);
                updateConfigStatus(handle, id, Status.RUNNING, Status.LOADING);

                StagingToDataWarehouse.run();

            } catch (Exception e) {
                // In case of an exception during processing, update the configuration status to indicate an error.
                handle.createUpdate("UPDATE configs SET status = :status WHERE id = :id")
                        .bind("status", Status.ERROR.toString())
                        .bind("is_running", Status.OFF.toString())
                        .bind("id", id)
                        .execute();
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Updates the status of a configuration entry in the database.
     *
     * @param handle          The Jdbi {@code Handle} for database interaction.
     * @param id              The ID of the configuration entry to be updated.
     * @param isRunningStatus The new value for the 'is_running' field.
     * @param configStatus    The new value for the 'status' field.
     */
    private static void updateConfigStatus(Handle handle, int id, Status isRunningStatus, Status configStatus) {
        handle.createUpdate("UPDATE configs SET is_running = :isRunning, status = :status WHERE id = :id")
                .bind("isRunning", isRunningStatus.toString())
                .bind("status", configStatus.toString())
                .bind("id", id)
                .execute();
    }
}
