package com.menes.utils;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.util.List;

public class Logger {
    private static final Configuration CONFIGURATION = Configuration.getInstance();
    private static final String host = CONFIGURATION.getDBControlHost();
    private final static String port = String.valueOf(CONFIGURATION.getDBControlPort());
    private final static String username = CONFIGURATION.getDBControlUsername();
    private final static String password = CONFIGURATION.getDBControlPassword();
    private final static String dbName = CONFIGURATION.getDBControlName();
    private static final Jdbi jdbi;

    static {
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);
        jdbi = Jdbi.create(jdbcUrl, username, password).installPlugin(new SqlObjectPlugin());
    }

    public static void log(int configId, String logLevel, long threadId, String className, String methodName, String logMessage) {

        String sql = "INSERT INTO logs (config_id, log_level, thread_id, class_name, method_name, log_message) " +
                "VALUES (:configId, :logLevel, :threadId, :className, :methodName, :logMessage)";
        jdbi.useHandle(handle ->
                handle.createUpdate(sql)
                        .bind("configId", configId)
                        .bind("logLevel", logLevel)
                        .bind("threadId", threadId)
                        .bind("className", className)
                        .bind("methodName", methodName)
                        .bind("logMessage", logMessage)
                        .execute()
        );

    }


    public static class Level {
        public static final String ERROR = "ERROR";
        public static final String WARNING = "WARNING";
        public static final String INFO = "INFO";

    }
    /**
     * Retrieves all logs from the 'logs' table.
     *
     * @return List of LogEntry objects representing the logs.
     * @throws RuntimeException if an error occurs during the database operation.
     */
    public static List<LogEntry> getAllLogs() {
        String sql = "SELECT * FROM logs";
        try {
            return jdbi.withHandle(handle ->
                    handle.createQuery(sql)
                            .mapToBean(LogEntry.class)
                            .list()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving logs from the database", e);
        }
    }


    /**
     * A simple class to represent a log entry.
     */
    public static class LogEntry {
        private int configId;
        private String logLevel;
        private long threadId;
        private String className;
        private String methodName;
        private String logMessage;

        // getters and setters...

        // You can generate getters and setters using your IDE or write them manually.
    }
}
