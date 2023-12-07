package com.menes.utils;

import org.apache.commons.logging.Log;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.Handle;
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
}
