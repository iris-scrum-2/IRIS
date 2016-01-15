package com.temenos.interaction.core.loader.impl;

import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.loader.Cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by kwieconkowski on 14/01/2016.
 */
public class CacheConcurrentImpl implements Cache<String, ResourceState> {
    private ConcurrentMap<String, ResourceState> cache = new ConcurrentHashMap<String, ResourceState>();

    @Override
    public void put(String key, ResourceState value) {
        cache.put(key, value);
    }

    @Override
    public void put(String key, ResourceState value, int ageInSeconds) {
        throw new UnsupportedOperationException("Not supported operation for this implementation");
    }

    @Override
    public void putAll(Map<String, ResourceState> keyValueMap) {
        cache.putAll(keyValueMap);
    }

    @Override
    public ResourceState get(String key) {
        return cache.get(key);
    }

    @Override
    public void remove(String key) {
        cache.remove(key);
    }

    @Override
    public void removeAll() {
        cache.clear();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }
}
