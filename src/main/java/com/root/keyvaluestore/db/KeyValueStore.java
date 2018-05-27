package com.root.keyvaluestore.db;

/*
 * Interface for key value store
 */
public interface KeyValueStore<K, V> {
    
    public void put(K key, V value);
    
    public V get(K key);

}
