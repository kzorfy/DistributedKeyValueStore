package com.root.keyvaluestore.resources;

import java.net.URI;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.root.keyvaluestore.dao.InMemoryKeyValueStoreDAO;
import com.root.keyvaluestore.dao.KeyValueStoreDAO;
import com.root.keyvaluestore.model.KeyValuePair;
import com.root.keyvaluestore.util.BackgroundTaskManager;

/*
 * Customer facing API for interacting with key value store
 */
@Path("keyvaluedatastore")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class KeyValueStoreResource {
    
    private final KeyValueStoreDAO<String, KeyValuePair> keyValueStoreDAO;
    
    public KeyValueStoreResource() {
        this.keyValueStoreDAO = new InMemoryKeyValueStoreDAO();
    }
    
    @GET
    @Path("/{key}")
    public KeyValuePair getKeyValuePair(final @PathParam("key") String key) {
        return keyValueStoreDAO.read(key);
    }
    
    @POST
    public Response putKeyValuePair(final KeyValuePair keyValuePair, final @Context UriInfo uriInfo) {
        
        final KeyValuePair newKeyValuePair = keyValueStoreDAO.create(keyValuePair);
        
        BackgroundTaskManager.addTask(newKeyValuePair);
        
        final String newKey = String.valueOf(newKeyValuePair.getKey());
        final URI uri = uriInfo.getAbsolutePathBuilder().path(newKey).build();
        
        return Response.created(uri).entity(newKeyValuePair).build();
    }
    
    @POST
    @Path("/registerreplicanode")
    public Response registerReplicaNodes(String replicaNode) {
        BackgroundTaskManager.addSubscribers(replicaNode);
        return Response.status(Status.CREATED).build();
    }
    
    @POST
    @Path("/replicatekeyvaluepair")
    public Response propagateReplicationData(final KeyValuePair keyValuePair) {
        keyValueStoreDAO.create(keyValuePair);
        return Response.status(Status.CREATED).build();
    }

}

