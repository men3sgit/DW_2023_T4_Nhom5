package com.menes.utils;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class DataWarehouseToMartLoader {
    private static final Configuration CONFIGURATION = Configuration.getInstance();


    public static void run() throws Exception{
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", CONFIGURATION.getProperty("db.mart.host"), CONFIGURATION.getProperty("db.mart.port"), CONFIGURATION.getProperty("db.mart.name"));
        String username = CONFIGURATION.getProperty("db.mart.username");
        String password = CONFIGURATION.getProperty("db.mart.password");
        Jdbi jdbi = Jdbi.create(jdbcUrl, username, password).installPlugin(new SqlObjectPlugin());

        // Create a Jdbi handle
        jdbi.useHandle(handle -> {
            String remoteHost = CONFIGURATION.getDBDataWarehouseHost();
            int remotePort = Integer.parseInt(CONFIGURATION.getDBDataWarehousePort());
            String remoteDBName = CONFIGURATION.getDBDataWarehouseName();
            String remoteUsername = CONFIGURATION.getDBDataWarehouseUsername();
            String remotePassword = CONFIGURATION.getDBDataWarehousePassword();

            handle.createCall("CALL import_remote_schema_to_data_warehouse(remoteHost,port,remoteDBName,remoteUsername,remotePassword);");
            handle.createCall("CALL insert_exchange_rates_today();").invoke();

        });
        System.out.println("DATA MART LOADED");
    }
}
