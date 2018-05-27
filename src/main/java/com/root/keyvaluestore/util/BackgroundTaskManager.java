package com.root.keyvaluestore.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    private ExecutorService executor;
    
    private static volatile List<KeyValuePair> keyValuePairs = new ArrayList<>();
    
    private static volatile Set<String> subscribers;
    
    private BlockingQueue<KeyValuePair> blockingQueue;
    
    private Client restClient;
    
    public BackgroundTaskManager() {
        this.subscribers = new HashSet<>();
        this.blockingQueue = new LinkedBlockingQueue<>();
        
        final ClientConfig cfg = new ClientConfig();
        cfg.property(ClientProperties.CONNECT_TIMEOUT, 5000);
        cfg.property(ClientProperties.READ_TIMEOUT, 5000);
        this.restClient = ClientBuilder.newClient(cfg);
    }
    
    public static void addTask(final KeyValuePair keyValuePair) {
        keyValuePairs.add(keyValuePair);
    }
    
    public static void addSubscribers(final String subscriber) {
        subscribers.add(subscriber);
        System.out.println(subscribers);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        executor = Executors.newFixedThreadPool(1);
        
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if(!keyValuePairs.isEmpty()) {
                        final KeyValuePair keyValuePair = keyValuePairs.get(0);
                        try {
                            blockingQueue.put(keyValuePair);
                        } catch (InterruptedException e) {
                            continue;
                        }
                        keyValuePairs.remove(0);
                    }
                    
                }
                
            }
        };
        new Thread(runnable).start();
        
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        final KeyValuePair keyValuePair = blockingQueue.take();
                        System.out.println("Data replication in progress");
                        final String payload = keyValuePair.getKey()+"#"+keyValuePair.getValue();
                        for (String subscriber: subscribers) {
                            System.out.println(subscriber);
                            System.out.println(Entity.json(keyValuePair));
                            final Response response = restClient.target(subscriber).request().post(Entity.json(keyValuePair));
                            System.out.println(response.getStatus());
                        }
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
                
            }
        };
        new Thread(runnable1).start();
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        executor.shutdownNow();
    }
}
