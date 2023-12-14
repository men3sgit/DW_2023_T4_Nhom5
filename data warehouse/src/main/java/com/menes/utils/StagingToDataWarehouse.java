package com.menes.utils;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

/**
 * The StagingToDataWarehouse class handles the process of transferring data
 * from a staging database to a data warehouse using Jdbi.
 */
public class StagingToDataWarehouse {

    /**
     * 1. The singleton instance of the Configuration class used for retrieving database connection settings.
     */
    private static final Configuration CONFIGURATION = Configuration.getInstance();


    public static void run() {

        // 2: Constructing the JDBC URL for the data warehouse connection
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", CONFIGURATION.getDBDataWarehouseHost(), CONFIGURATION.getDBDataWarehousePort(), CONFIGURATION.getDBDataWarehouseName());
        String username = CONFIGURATION.getDBDataWarehouseUsername();
        String password = CONFIGURATION.getDBDataWarehousePassword();

        // 3: Creating a Jdbi instance and installing the SqlObjectPlugin
        Jdbi jdbi = Jdbi.create(jdbcUrl, username, password).installPlugin(new SqlObjectPlugin());


        // 4: Using a Jdbi handle to execute database operations
        jdbi.useHandle(handle -> {

            // 5: Retrieving information for the staging database connection
            String remoteHost = CONFIGURATION.getDBStagingHost();
            int remotePort = CONFIGURATION.getDBStagingPort();
            String remoteDBName = CONFIGURATION.getDBStagingName();
            String remoteUsername = CONFIGURATION.getDBStagingUsername();
            String remotePassword = CONFIGURATION.getDBStagingPassword();

            // 6: Calling stored procedures to import remote schema
            handle.createCall("CALL import_remote_schema(remoteHost,remotePort,remoteDBName,remoteUsername,remotePassword);");

            //7. Calling stored procedures load data into the data warehouse
            handle.createCall("CALL load_data_to_fact();").invoke();

        });
        //// 8: Indicating the success of the data transformation process
        System.out.println("TRANSFORM SUCCESSFULLY!");
    }
}
