package com.root.keyvaluestore.dao;

/*
 * DAO for interacting with in-memory key value store
 */
import com.root.keyvaluestore.db.InMemoryKeyValueStore;
import com.root.keyvaluestore.db.KeyValueStore;
import com.root.keyvaluestore.exception.DataNotFoundException;
import com.root.keyvaluestore.model.KeyValuePair;

public class InMemoryKeyValueStoreDAO implements KeyValueStoreDAO<String, KeyValuePair> {
    
    private final KeyValueStore<String, String> keyValueStore;
    
    public InMemoryKeyValueStoreDAO() {
        this.keyValueStore = new InMemoryKeyValueStore();
    }
    
    @Override
    public KeyValuePair create(final KeyValuePair keyValuePair) {
        keyValueStore.put(keyValuePair.getKey(), keyValuePair.getValue());
        return keyValuePair;
        
    }

    @Override
    public KeyValuePair read(final String key) {
        final String value = keyValueStore.get(key);
        if (null == value) {
            throw new DataNotFoundException(
                    String.format("No data available for key %s", key));
        }
        return new KeyValuePair(key, value);
    }
    
    

}
