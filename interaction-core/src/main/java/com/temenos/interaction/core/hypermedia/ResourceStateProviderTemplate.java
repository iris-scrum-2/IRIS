package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-springdsl
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
import com.temenos.interaction.core.cache.Cache;
import static com.temenos.interaction.core.loader.ResourceStateLoader.ResourceStateResult;
import com.temenos.interaction.core.loader.ResourceStateLoader;
import com.temenos.interaction.core.resource.ResourceStateMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 *
 * @author dgroves
 * @author hmanchala
 * @author kwieconkowski
 * @author andres
 */
public abstract class ResourceStateProviderTemplate implements ResourceStateProvider {

    private final Logger logger = LoggerFactory.getLogger(ResourceStateProviderTemplate.class);

    protected final Cache<String, ResourceState> cache;
    protected final String antStylePattern;
    protected ResourceStateLoader<String> resourceStateLoader;
    protected ResourceStateMapper mapper;

    public ResourceStateProviderTemplate(String antStylePattern, ResourceStateLoader<String> resourceStateLoader, Cache<String, ResourceState> cache, ResourceStateMapper mapper) {
        this.antStylePattern = antStylePattern;
        this.resourceStateLoader = resourceStateLoader;
        this.cache = cache;
        this.mapper = mapper;
    }

    public void setLoadingStrategy(ResourceStateLoader<String> resourceStateLoader) {
        this.resourceStateLoader = resourceStateLoader;
    }

    @Override
    public abstract ResourceState getResourceState(String resourceStateName);

    @Override
    public ResourceState determineState(Event event, String resourcePath) {
        ResourceState result = null;
        String request = event.getMethod() + " " + resourcePath,
                stateName = mapper.getResourceStatesByRequest().get(request);
        if (stateName != null) {
            logger.debug("Found resource state: [" + stateName + "] for request: [" + request + "]");
            result = getResourceState(stateName);
        } else {
            logger.warn("Could not find resource state: [" + stateName + "] for request: [" + request + "]");
        }
        return result;
    }

    @Override
    public boolean isLoaded(String resourceStateName) {
        return (cache.get(resourceStateName) != null);
    }

    @Override
    public Map<String, Set<String>> getResourceStatesByPath() {
        return mapper.getResourceStatesByPath();
    }

    @Override
    public Map<String, Set<String>> getResourceMethodsByState() {
        return mapper.getResourceMethodsByState();
    }

    @Override
    public Map<String, String> getResourcePathsByState() {
        return mapper.getResourcePathsByState();
    }

    protected void populateCacheAndMapResourceStates(List<ResourceStateResult> results) {
        Map<String, ResourceState> resourceStateNamesToResourceStates = new HashMap<>();
        for (ResourceStateResult result : results) {
            resourceStateNamesToResourceStates.put(result.getResourceStateId(), result.getResourceState());
            mapper.map(result);
        }
        this.cache.putAll(resourceStateNamesToResourceStates);
    }
}
