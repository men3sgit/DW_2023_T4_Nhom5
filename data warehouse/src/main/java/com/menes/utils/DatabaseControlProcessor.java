package com.menes.utils;

import com.menes.extract.LocalToStaging;
import com.menes.extract.VietcombankScraping;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.util.Map;

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

/**
 * DatabaseConfiguration class provides access to the configuration settings
 * for connecting to a database using the DBControl module.
 */
public class DatabaseControlProcessor {
    /**
     * 1. The singleton instance of the Configuration class used for retrieving
     * database connection settings.
     */
    private static final Configuration CONFIGURATION = Configuration.getInstance();

    /**
     * The host address of the database server.
     */
    private static final String host = CONFIGURATION.getDBControlHost();

    /**
     * The port number for connecting to the database server.
     */
    private final static String port = String.valueOf(CONFIGURATION.getDBControlPort());

    /**
     * The username used for authenticating the database connection.
     */
    private final static String username = CONFIGURATION.getDBControlUsername();

    /**
     * The password used for authenticating the database connection.
     */
    private final static String password = CONFIGURATION.getDBControlPassword();

    /**
     * The name of the database to connect to.
     */
    private final static String dbName = CONFIGURATION.getDBControlName();
    ;

    private static int source;

    private static final Jdbi jdbi;

    static {
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);
        jdbi = Jdbi.create(jdbcUrl, username, password).installPlugin(new SqlObjectPlugin());
    }

    public static void run(String fSource) {
        source = Integer.parseInt(fSource);
        try {
            Logger.log(source, Logger.Level.INFO, Thread.currentThread().threadId(), DatabaseControlProcessor.class.getName(), "run", "Processing started");
            // 1. Load configuration properties and establish a database connection.
            Logger.log(source, Logger.Level.INFO, Thread.currentThread().threadId(), DatabaseControlProcessor.class.getName(), "run", "Establishing a database connection");

            jdbi.useHandle(DatabaseControlProcessor::processConfigs);

        } catch (Exception e) {
            Logger.log(source, Logger.Level.ERROR, Thread.currentThread().threadId(), DatabaseControlProcessor.class.getName(), "run", e.getMessage());
            ExceptionMailer.handleException(e);
            throw new RuntimeException(e);
        } finally {
            Logger.log(source, Logger.Level.INFO, Thread.currentThread().threadId(), DatabaseControlProcessor.class.getName(), "run", "Processing completed");
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
                .stream().peek(System.out::println)
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
        String flag = (String) config.get("is_running");

        if (Flag.OFF.toString().equals(flag)) {
            int configId = (int) config.get("id");
            try {
                // Update the configuration status to indicate crawling.
                updateConfigStatus(handle, configId, Flag.RUNNING, Status.CRAWLING);

                String resultPath = (String) config.get("result_path");
                String errorPath = (String) config.get("error_path");
                String email = (String) config.get("email");

                ExceptionMailer.ERROR_PATH = errorPath;
                ExceptionMailer.TO_EMAIL = email;

                if (source == Source.SACOMBANK) {
                    Logger.log(configId, Logger.Level.INFO, Thread.currentThread().threadId(), DatabaseControlProcessor.class.getName(), "processConfig", "Processing SACOMBANK configuration");
                    SacombankScraping.run(resultPath);
                } else {
                    // Execute VietcombankScraping process.
                    Logger.log(configId, Logger.Level.INFO, Thread.currentThread().threadId(), DatabaseControlProcessor.class.getName(), "processConfig", "Processing Vietcombank configuration");
                    VietcombankScraping.run(resultPath);
                }

                // Update the configuration status to indicate crawling completion.
                updateConfigStatus(handle, configId, Flag.OFF, Status.CRAWLED);
                Logger.log(configId, Logger.Level.INFO, Thread.currentThread().threadId(), DatabaseControlProcessor.class.getName(), "processConfig", "Executing LocalToStaging process");

                // Update the configuration status to indicate transforming.
                updateConfigStatus(handle, configId, Flag.RUNNING, Status.TRANSFORMING);

                // Execute LocalToStaging process.
                Logger.log(configId, Logger.Level.INFO, Thread.currentThread().threadId(), DatabaseControlProcessor.class.getName(), "processConfig", "Executing LocalToStaging process");
                LocalToStaging.run(resultPath);

                // Update the configuration status to indicate transformation completion.
                updateConfigStatus(handle, configId, Flag.OFF, Status.TRANSFORMED);
                updateConfigStatus(handle, configId, Flag.RUNNING, Status.LOADING);

                // Execute StagingToDataWarehouse process.
                Logger.log(configId, Logger.Level.INFO, Thread.currentThread().threadId(), DatabaseControlProcessor.class.getName(), "processConfig", "Executing StagingToDataWarehouse process");
                StagingToDataWarehouse.run();
                updateConfigStatus(handle, configId, Flag.OFF, Status.LOADED);
                updateConfigStatus(handle, configId, Flag.RUNNING, Status.LOADING);

                // Execute DataWarehouseToMartLoader process.
                Logger.log(configId, Logger.Level.INFO, Thread.currentThread().threadId(), DatabaseControlProcessor.class.getName(), "processConfig", "Executing DataWarehouseToMartLoader process");
                DataWarehouseToMartLoader.run();

                // Update the configuration status to indicate the process is finished.
                updateConfigStatus(handle, configId, Flag.OFF, Status.FINISHED);
                Logger.log(configId, Logger.Level.INFO, Thread.currentThread().threadId(), DatabaseControlProcessor.class.getName(), "processConfig", "Finish Extract");

            } catch (Exception e) {
                // In case of an exception during processing, update the configuration status to indicate an error.
                handle.createUpdate("UPDATE configs SET status = :status WHERE id = :id").bind("status", Status.ERROR.toString()).bind("is_running", Flag.OFF.toString()).bind("id", configId).execute();
                Logger.log(configId, Logger.Level.ERROR, Thread.currentThread().threadId(), DatabaseControlProcessor.class.getName(), "processConfig", e.getMessage());
                ExceptionMailer.handleException(e);
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
    private static void updateConfigStatus(Handle handle, int id, Flag isRunningStatus, Status configStatus) {
        handle.createUpdate("UPDATE configs SET is_running = :isRunning, status = :status, updated_at = CURRENT_TIMESTAMP WHERE id = :id").bind("isRunning", isRunningStatus.toString()).bind("status", configStatus.toString()).bind("id", id).execute();
    }
}