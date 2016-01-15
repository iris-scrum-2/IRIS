package com.temenos.interaction.core.loader;

import java.util.Map;

/**
 * Created by kwieconkowski on 14/01/2016.
 */
public interface Cache<K, V> {
    void put(K key, V value);

    void put(K key, V value, int ageInSeconds);

    void putAll(Map<K, V> keyValueMap);

    V get(K key);

    void remove(K key);

    void removeAll();
}
