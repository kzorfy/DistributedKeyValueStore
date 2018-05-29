package com.root.keyvaluestore.db;

import java.util.HashMap;
import java.util.Map;

import com.root.keyvaluestore.exception.DataStoreException;

/*
 * In memory key value store for managing string (key,value) pairs
 * 
 * NOTE: PUT operation works as both CREATE as well as UPDATE for the same key
 * 
 */
public class InMemoryKeyValueStore implements KeyValueStore<String, String> {
    
    private final Map<String, String> keyValueStore;
    
    public InMemoryKeyValueStore() {
        this.keyValueStore = new HashMap<>();
    }
    
    protected InMemoryKeyValueStore(final Map<String, String> keyValueStore) {
        this.keyValueStore = keyValueStore;
    }

    @Override
    public void put(String key, String value) {
        try {
            keyValueStore.put(key, value);
        } catch (Exception e) {
            throw new DataStoreException(
                    String.format("Exception while storing data for key %s", key));
        }
    }

    @Override
    public String get(String key) {
        try {
            return keyValueStore.get(key);
        } catch (Exception e) {
            throw new DataStoreException(
                    String.format("Exception while retrieveing data for key %s", key));
        }
    }

}
