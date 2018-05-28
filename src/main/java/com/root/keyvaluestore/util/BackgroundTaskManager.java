package com.root.keyvaluestore.util;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import com.root.keyvaluestore.model.KeyValuePair;

public class BackgroundTaskManager implements ServletContextListener {

    private static volatile Set<String> subscribers;
    
    private static BlockingQueue<KeyValuePair> blockingQueue;
    
    private Client restClient;
    
    private ExecutorService executor;
    
    static {
        subscribers = new HashSet<>();
        blockingQueue = new LinkedBlockingQueue<>();
    }
    
    public BackgroundTaskManager() {
        
        this.executor = Executors.newFixedThreadPool(1);
        
        final ClientConfig cfg = new ClientConfig();
        cfg.property(ClientProperties.CONNECT_TIMEOUT, 5000);
        cfg.property(ClientProperties.READ_TIMEOUT, 5000);
        this.restClient = ClientBuilder.newClient(cfg);
    }
    
    public static void addTask(final KeyValuePair keyValuePair) {
        try {
            blockingQueue.put(keyValuePair);
        } catch (InterruptedException e) {
        }
    }
    
    public static void addSubscribers(final String subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        final KeyValuePair keyValuePair = blockingQueue.take();
                        for (String subscriber: subscribers) {
                            final Response response = restClient.target(subscriber).request().post(Entity.json(keyValuePair));
                        }
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
                
            }
        };
        executor.submit(runnable);
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        executor.shutdownNow();
    }
}
