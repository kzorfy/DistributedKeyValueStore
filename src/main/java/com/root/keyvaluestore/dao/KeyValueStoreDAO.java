package com.root.keyvaluestore.dao;

/*
 * DAO for interacting with key value store
 */
public interface KeyValueStoreDAO<K, V> {
    
    public V create(V object);
    
    public V read(K key);

}
