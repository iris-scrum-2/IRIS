package com.temenos.interaction.translate.loader;

/*
 * #%L
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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.temenos.interaction.core.cache.Cache;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.loader.ResourceStateLoader;
import com.temenos.interaction.springdsl.DynamicRegistrationResourceStateProviderTemplate;
import com.temenos.interaction.springdsl.DynamicRegistrationResourceStateProvider;
import com.temenos.interaction.springdsl.StateRegisteration;
import com.temenos.interaction.core.resource.ResourceStateMapper;

/**
 *
 * Register resource states with the state machine and the web service provider
 * (such as Apache Wink). Registration with the provider is done when the
 * object is initialised, and when new resources are added.
 * 
 * In case the registration is done somewhere else, at initialisation time the
 * registration will be done twice. This implementation trades off design
 * clarity with efficiency.
 *
 * @author dgroves
 * @author hmanchala
 * @author kwieconkowski
 * @author andres
 */
public class RIMResourceStateProvider extends DynamicRegistrationResourceStateProviderTemplate implements DynamicRegistrationResourceStateProvider {

    private final Logger logger = LoggerFactory.getLogger(RIMResourceStateProvider.class);

    private Collection<String> sources;

    public RIMResourceStateProvider(String antStylePattern, Cache<String, ResourceState> cache) {
        this(antStylePattern, null, cache, null, null);
        this.sources = new HashSet<>();
        findRimFilenames();
        loadAllResourceStates();
    }

    public RIMResourceStateProvider(String antStylePattern, ResourceStateLoader<String> resourceStateLoader, Cache<String, ResourceState> cache, ResourceStateMapper mapper, StateRegisteration stateRegisteration) {
        super(antStylePattern, resourceStateLoader, cache, mapper, stateRegisteration);
    }

    @Override
    public ResourceState getResourceState(String stateName) {
        ResourceState resourceState = cache.get(stateName);
        if (resourceState != null) {
            logger.debug("Found resource state: [" + stateName + "]");
        } else {
            logger.error("Could not find resource state: [" + stateName + "]");
        }
        return resourceState;
    }

    private void findRimFilenames() {
        final ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        final Resource[] locations;
        try {
            String fileName;
            locations = patternResolver.getResources(antStylePattern);
            if (locations != null) {
                sources.clear();
                for (Resource location : locations) {
                    fileName = Paths.get(
                            location
                            .getURI()
                            .getPath()
                            .substring(1)
                    ).getFileName().toString();
                    sources.add(fileName);
                    logger.info("Found RIM file: " + fileName);
                }
            } else {
                logger.warn("No RIM files found for pattern: " + antStylePattern);
            }
        } catch (IOException e) {
            String msg = "IOException while loading RIM files";
            logger.error(msg, e);
            throw new IllegalStateException(msg, e);
        }
    }

    private synchronized void loadAllResourceStates() {
        cache.removeAll();
        super.loadAndMapFiles(sources);
    }
}
