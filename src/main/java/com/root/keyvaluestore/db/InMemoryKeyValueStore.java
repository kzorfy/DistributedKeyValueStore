package com.root.keyvaluestore.db;

import java.util.HashMap;
import java.util.Map;

/*
 * In memory key value store for managing string (key,value) pairs
 * 
 * NOTE: PUT operation works as both CREATE as well as UPDATE for the same key
 * 
 */
public class InMemoryKeyValueStore implements KeyValueStore<String, String> {
    
    private static final Map<String, String> keyValueStore = new HashMap<>();

    @Override
    public void put(String key, String value) {
        keyValueStore.put(key, value);
    }

    @Override
    public String get(String key) {
        return keyValueStore.get(key);
    }

}
