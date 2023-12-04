package com.menes.utils;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class StagingToDataWarehouse {
    private static final Configuration CONFIGURATION = Configuration.getInstance();


    public static void run() {
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", CONFIGURATION.getDBDataWarehouseHost(), CONFIGURATION.getDBDataWarehousePort(), CONFIGURATION.getDBDataWarehouseName());
        String username = CONFIGURATION.getDBDataWarehouseUsername();
        String password = CONFIGURATION.getDBDataWarehousePassword();
        Jdbi jdbi = Jdbi.create(jdbcUrl, username, password).installPlugin(new SqlObjectPlugin());

        // Create a Jdbi handle
        jdbi.useHandle(handle -> {
            String remoteHost = CONFIGURATION.getDBStagingHost();
            int remotePort = CONFIGURATION.getDBStagingPort();
            String remoteDBName = CONFIGURATION.getDBStagingName();
            String remoteUsername = CONFIGURATION.getDBStagingUsername();
            String remotePassword = CONFIGURATION.getDBStagingPassword();

            handle.createCall("CALL import_remote_schema(remoteHost,port,remoteDBName,remoteUsername,remotePassword);");
            handle.createCall("CALL load_data_to_fact();").invoke();

        });
        System.out.println("TRANSFORM SUCCESSFULLY!");
    }
}
