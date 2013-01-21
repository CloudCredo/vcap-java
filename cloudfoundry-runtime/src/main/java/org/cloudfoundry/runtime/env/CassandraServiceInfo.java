package org.cloudfoundry.runtime.env;

import java.util.Map;


/**
 * Service info class for Cassandra.
 *
 * @author Chris Hedley
 */
public class CassandraServiceInfo extends BaseServiceInfo {

    /**
     * The username of the provisioned service instance
     */
    private String userName;

    /**
     * The name of the cluster on provisioned Cassandra instances
     */
    private String clusterName;

    public CassandraServiceInfo(Map<String, Object> serviceInfo) {

        super(serviceInfo);

        Map<String, Object> credentials = (Map<String, Object>) serviceInfo.get("credentials");

        userName = (String) credentials.get("username");
        clusterName = (String) credentials.get("name");
    }

    /**
     * The username of the provisioned service instance
     */
    public String getUserName() {
        return userName;
    }

    /**
     * The name of the cluster for the provisioned Cassandra instances
     */
    public String getClusterName() {
        return clusterName;
    }
}
