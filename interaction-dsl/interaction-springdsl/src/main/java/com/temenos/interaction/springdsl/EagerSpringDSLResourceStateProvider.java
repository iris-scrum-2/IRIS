package com.temenos.interaction.springdsl;

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

import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.loader.Cache;
import com.temenos.interaction.core.loader.ResourceStateLoadingStrategy;
import com.temenos.interaction.core.loader.impl.ResourceStateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author kwieconkowski
 */
public final class EagerSpringDSLResourceStateProvider extends SpringDSLResourceStateProvider {
    private final Logger logger = LoggerFactory.getLogger(EagerSpringDSLResourceStateProvider.class);

    private final Cache<String, ResourceState> cache;
    private final String antStylePattern;
    private Set<String> PRDconfigurationFileSources;
    private ResourceStateLoadingStrategy<String> loadingStrategy;

    public EagerSpringDSLResourceStateProvider(String antStylePattern, ResourceStateLoadingStrategy<String> loadingStrategy, Cache<String, ResourceState> cache) {
        this(antStylePattern, loadingStrategy, cache, null);
    }

    public EagerSpringDSLResourceStateProvider(String antStylePattern, ResourceStateLoadingStrategy<String> loadingStrategy, Cache<String, ResourceState> cache, Properties beanMap) {
        super(beanMap);
        this.antStylePattern = antStylePattern;
        this.loadingStrategy = loadingStrategy;
        this.cache = cache;
        PRDconfigurationFileSources = new LinkedHashSet();
        logger.error("Eager constructor");
        discoverAllPrdFiles();
        loadAllResourceStates();
    }

    public void setLoadingStrategy(ResourceStateLoadingStrategy<String> loadingStrategy) {
        this.loadingStrategy = loadingStrategy;
    }

    @Override
    public ResourceState getResourceState(String resourceStateName) {
        logger.error("Getting resource state: " + resourceStateName);
        ResourceState resourceState = getResourceStateByNameOrByOldFormatName(resourceStateName);
        if (resourceState == null) {
            logger.error("Could not find resource state with name: " + resourceStateName);
        }
        return resourceState;
    }

    private ResourceState getResourceStateByNameOrByOldFormatName(String resourceStateName) {
        ResourceState resourceState = cache.get(resourceStateName);
        return resourceState != null ? resourceState : getResourceStateByOldFormat(resourceStateName);
    }

    private ResourceState getResourceStateByOldFormat(String resourceStateName) {
        ResourceState resourceState = null;
        String newResourceStateName = resourceStateName;

        if (newResourceStateName.contains("-")) {
            newResourceStateName = newResourceStateName.substring(0, newResourceStateName.indexOf("-"));
            resourceState = cache.get(newResourceStateName);
        }

        if (resourceState == null) {
            int pos = newResourceStateName.lastIndexOf("-");
            if (pos < 0) {
                pos = newResourceStateName.lastIndexOf("_");
                if (pos > 0) {
                    newResourceStateName = String.format("%s-%s", newResourceStateName.substring(0, pos), newResourceStateName.substring(pos + 1));
                    resourceState = cache.get(newResourceStateName);
                }
            }
        }
        return resourceState;
    }

    @Override
    public void unload(String resourceStateName) {
        cache.remove(resourceStateName);
    }

    @Override
    public void addState(String stateName, Properties properties) {
        String[] methodAndPath = properties.getProperty(stateName).split(" ");
        String[] methods = methodAndPath[0].split(",");
        String path = methodAndPath[1];
        logger.info(String.format("Attempting to register state: %s, methods: %s, path: %s, using state registeration: %s",
                stateName, methods, path, stateRegisteration != null ? stateRegisteration : "NULL"));

        if (!loadResourceStatesFromPRD(discoverLocationOfPrdByResourceStateName(stateName, false))
                && !loadResourceStatesFromPRD(discoverLocationOfPrdByResourceStateName(stateName, true))) {
            logger.error("Any discovered path pattern is valid");
            return;
        }
        // populate maps in parent class from properties files
        storeState(stateName, properties.getProperty(stateName));
        stateRegisteration.register(stateName, path, new HashSet<String>(Arrays.asList(methods)));
    }

    @Override
    public boolean isLoaded(String resourceStateName) {
        return (cache.get(resourceStateName) != null);
    }

    /* Reload resource states from prd files (clear old ones from cache before) */
    private synchronized void loadAllResourceStates() {
        cache.removeAll();

        for (String locationOfPRD : PRDconfigurationFileSources) {
            loadResourceStatesFromPRD(locationOfPRD);
        }
    }

    private String discoverLocationOfPrdByResourceStateName(String resourceStateName, boolean oldFormat) {
        String pathToPRD = null;
        String newResourceStateName = resourceStateName;

        if (newResourceStateName.contains("-")) {
            newResourceStateName = newResourceStateName.substring(0, newResourceStateName.indexOf("-"));
        }

        if (!oldFormat) {
            pathToPRD = String.format("IRIS-%s-PRD.xml", newResourceStateName);
        }

        int position = newResourceStateName.lastIndexOf("_");
        if (position > 3) {
            pathToPRD = String.format("IRIS-%s-PRD.xml", newResourceStateName.substring(0, position));
        }

        return pathToPRD;
    }

    private boolean loadResourceStatesFromPRD(String prdLocation) {
        List<ResourceStateResult> resourceStates = null;
        Map<String, ResourceState> tmp = new HashMap<String, ResourceState>();

        if (prdLocation == null) {
            return false;
        }
        logger.error("Loading PRD file: " + prdLocation);
        resourceStates = loadingStrategy.load(prdLocation);
        if (resourceStates == null) {
            logger.warn("Could not find any resources with file pattern: " + prdLocation);
            return false;
        }
        for (ResourceStateResult resourceStateResult : resourceStates) {
            tmp.put(resourceStateResult.beanName, resourceStateResult.resourceState);
        }
        cache.putAll(tmp);
        return true;
    }

    private void discoverAllPrdFiles() {
        final ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        final Resource[] locationsPRD;
        try {
            String fileName;
            locationsPRD = patternResolver.getResources(antStylePattern);
            if (locationsPRD != null) {
                PRDconfigurationFileSources.clear();
                for (int i = 0; i < locationsPRD.length; i++) {
                    fileName = Paths.get(locationsPRD[i].getURI().getPath().substring(1)).getFileName().toString();
                    PRDconfigurationFileSources.add(fileName);
                    logger.error("Discovered path to PRD file: " + fileName);
                }
            } else {
                logger.warn("Spring DSL eager loading strategy default, could not found any PRD spring configuration xml files");
            }
        } catch (IOException e) {
            logger.error("IOException loading Spring PRD files using eager strategy", e);
            throw new IllegalStateException("IOException loading Spring PRD files using eager strategy", e);
        }
    }
}
