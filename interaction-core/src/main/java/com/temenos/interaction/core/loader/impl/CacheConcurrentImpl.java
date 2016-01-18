package com.temenos.interaction.core.loader.impl;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
