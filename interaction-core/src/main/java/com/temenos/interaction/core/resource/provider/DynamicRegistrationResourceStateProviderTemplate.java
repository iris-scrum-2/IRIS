package com.temenos.interaction.core.resource.provider;

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
import com.temenos.interaction.springdsl.DynamicRegistrationResourceStateProvider;
import com.temenos.interaction.core.cache.Cache;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.springdsl.StateRegisteration;
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
public abstract class DynamicRegistrationResourceStateProviderTemplate extends ResourceStateProviderTemplate implements DynamicRegistrationResourceStateProvider {

    private final Logger logger = LoggerFactory.getLogger(DynamicRegistrationResourceStateProviderTemplate.class);

    protected StateRegisteration stateRegisteration;

    public DynamicRegistrationResourceStateProviderTemplate(String antStylePattern, ResourceStateLoader<String> resourceStateLoader, Cache<String, ResourceState> cache, ResourceStateMapper mapper, StateRegisteration stateRegisteration) {
        super(antStylePattern, resourceStateLoader, cache, mapper);
        this.stateRegisteration = stateRegisteration;
    }

    @Override
    public void setStateRegisteration(StateRegisteration registerState) {
        this.stateRegisteration = registerState;
    }

    @Override
    public void loadAndMapFiles(Collection<String> rimFilenames) {
        List<ResourceStateResult> results = new ArrayList<>();
        for (String filename : rimFilenames) {
            results.addAll(resourceStateLoader.load(filename));
        }
        populateCacheAndMapResourceStates(results);
    }

    @Override
    public void unload(String resourceStateName) {
        cache.remove(resourceStateName);
    }

    protected void register(ResourceStateResult resource) {
        stateRegisteration.register(resource.getResourceStateId(), resource.getPath(),
                new HashSet<String>(Arrays.asList(resource.getMethods())));
    }

    @Override
    protected void populateCacheAndMapResourceStates(List<ResourceStateResult> results) {
        Map<String, ResourceState> resourceStateNamesToResourceStates = new HashMap<>();
        for (ResourceStateResult result : results) {
            resourceStateNamesToResourceStates.put(result.getResourceStateId(), result.getResourceState());
            mapper.map(result);
            register(result);
        }
        this.cache.putAll(resourceStateNamesToResourceStates);
    }
}
