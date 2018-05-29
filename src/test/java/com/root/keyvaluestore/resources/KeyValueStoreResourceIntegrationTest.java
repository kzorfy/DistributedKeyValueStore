package com.root.keyvaluestore.resources;

import static org.testng.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.root.keyvaluestore.model.KeyValuePair;

/**
 * Integration test for {@link KeyValueStoreResource}
 * 
 * NOTE: Keep both the master and the replica node up and running before kickstarting the integration test suite
 * 
 */
public class KeyValueStoreResourceIntegrationTest {
    
    private static final String RESPONSE_TEMPLATE = "{\"key\":\"%s\",\"value\":\"%s\"}";
    
    private static final int CREATED_HTTP_STATUS_CODE = 201;
    
    private static final int SUCCESS_HTTP_STATUS_CODE = 200;
    
    private Client restClient;
    
    @BeforeMethod(groups={"integration"}, alwaysRun=true)
    public void initialize() {
        
        final ClientConfig cfg = new ClientConfig();
        cfg.property(ClientProperties.CONNECT_TIMEOUT, 5000);
        cfg.property(ClientProperties.READ_TIMEOUT, 5000);
        this.restClient = ClientBuilder.newClient(cfg);
    }
    
    @Test(groups={"integration"}, dataProvider="happyTestCaseForKeyValueStore")
    public void testKeyValuePairStoreGETAndPUTOperations(final String masterNodeResourceEndpoint,
            final String masterNodeReplicaResgistrationEndPoint, final String replicationEndPoint,
            final String replicaEndPoint,final String key, final String value) throws InterruptedException {
        
        //Register the replica node
        Response response = restClient.target(masterNodeReplicaResgistrationEndPoint)
                .request().post(Entity.json(replicationEndPoint));
        assertEquals(response.getStatus(), CREATED_HTTP_STATUS_CODE);
        
        //Create a key value pair in the master node
        response = restClient.target(masterNodeResourceEndpoint).request()
                .post(Entity.json(new KeyValuePair(key, value)));
        assertEquals(response.getStatus(), CREATED_HTTP_STATUS_CODE);
        
        final String expectedResponse = String.format(RESPONSE_TEMPLATE, key, value);
        
        //Verify the master node is updated with the above submitted request
        final Response masterNodeResponse = restClient.target(masterNodeResourceEndpoint+ "/" + key).request().accept(
                MediaType.APPLICATION_JSON).get();
        assertEquals(masterNodeResponse.getStatus(), SUCCESS_HTTP_STATUS_CODE);
        String actualResponse = masterNodeResponse.readEntity(String.class);
        assertEquals(actualResponse, expectedResponse);
        
        /*
         * Wait for replication to take place
         */
        Thread.sleep(1000);
        
        //Verify the master node is updated with the above submitted request
        final Response replicaNodeResponse = restClient.target(replicaEndPoint+ "/" + key).request().accept(
                MediaType.APPLICATION_JSON).get();
        assertEquals(replicaNodeResponse.getStatus(), SUCCESS_HTTP_STATUS_CODE);
        actualResponse = replicaNodeResponse.readEntity(String.class);
        assertEquals(actualResponse, expectedResponse);
        
    }
    
    @DataProvider
    public Object[][] happyTestCaseForKeyValueStore() {
        
        return new Object[][] {
            {"http://localhost:8181/keyvaluestore/webapi/keyvaluedatastore",
                "http://localhost:8181/keyvaluestore/webapi/keyvaluedatastore/registerreplicanode",
                "http://localhost:8182/keyvaluestore/webapi/keyvaluedatastore/replicatekeyvaluepair",
                "http://localhost:8182/keyvaluestore/webapi/keyvaluedatastore", "key", "value"}
        };
        
    }

}
