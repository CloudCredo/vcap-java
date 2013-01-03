package org.cloudfoundry.runtime.env;

import java.util.Map;


/**
 * Service info class for Cassandra.
 *
 * @author Chris Hedley
 */
public class CassandraServiceInfo extends BaseServiceInfo {

    private String database;
    private String userName;

    public CassandraServiceInfo(Map<String, Object> serviceInfo) {

        super(serviceInfo);

        Map<String, Object> credentials = (Map<String, Object>) serviceInfo.get("credentials");

        userName = (String) credentials.get("username");
        database = (String) credentials.get("db");
    }

    public String getUserName() {
        return userName;
    }

    public String getDatabase() {
        return database;
    }
}
