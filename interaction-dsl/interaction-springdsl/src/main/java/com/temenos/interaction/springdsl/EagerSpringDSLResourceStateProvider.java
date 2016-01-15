package com.temenos.interaction.springdsl;

import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.loader.Cache;
import com.temenos.interaction.core.loader.ResourceStateLoadingStrategy;
import com.temenos.interaction.core.loader.impl.CacheConcurrentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by kwieconkowski on 14/01/2016.
 */
public class EagerSpringDSLResourceStateProvider extends SpringDSLResourceStateProvider {
    private final Logger logger = LoggerFactory.getLogger(EagerSpringDSLResourceStateProvider.class);

    private Cache<String, ResourceState> cache = new CacheConcurrentImpl();
    private String[] PRDconfigurationFileSources;
    private ResourceStateLoadingStrategy<String> loadingStrategy;
    private final String antStylePattern;

    //"classpath*:/src-gen/**/IRIS-*-PRD.xml"
    public EagerSpringDSLResourceStateProvider(String antStylePattern) {
        this.antStylePattern = antStylePattern;
        discoverAllPrdFiles();
    }

    public EagerSpringDSLResourceStateProvider(String antStylePattern, Properties beanMap) {
        super(beanMap);
        this.antStylePattern = antStylePattern;
        discoverAllPrdFiles();
    }

    public void setLoadingStrategy(ResourceStateLoadingStrategy<String> loadingStrategy) {
        this.loadingStrategy = loadingStrategy;
    }

    @Override
    public ResourceState getResourceState(String resourceStateName) {
        ResourceState resourceState = cache.get(resourceStateName);
        if (resourceState == null) {
            loadAllResourceStates();
            resourceState = cache.get(resourceStateName);
        }
        if (resourceState == null) {
            logger.error("Could not find resource state with name: " + resourceStateName);
        }
        return resourceState;
    }

    private synchronized void loadAllResourceStates() {
        // we assume we do not want to keep ResourceStates that are not defined on PRD files
        cache.removeAll();
        List<ResourceState> resourceStates = null;
        Map<String, ResourceState> tmp = new HashMap<String, ResourceState>();

        for (String location : PRDconfigurationFileSources) {
            resourceStates = loadingStrategy.load(antStylePattern);
            if (resourceStates == null) {
                logger.warn("Could not find any resources with file pattern: " + location);
            }
            for (ResourceState resourceState : resourceStates) {
                tmp.put(resourceState.getName(), resourceState);
            }
        }
        cache.putAll(tmp);
    }

    public void discoverAllPrdFiles() {
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        Resource[] locationsPRD = null;
        try {
            locationsPRD = patternResolver.getResources(antStylePattern);
            if (locationsPRD != null) {
                PRDconfigurationFileSources = new String[locationsPRD.length];
                for (int i = 0; i < locationsPRD.length; i++) {
                    PRDconfigurationFileSources[i] = locationsPRD[i].getURI().getPath();
                }
            } else {
                logger.warn("Spring DSL eager loading strategy default, could not found any PRD spring configuration xml files");
            }
        } catch (IOException e) {
            logger.error("IOException loading Spring PRD files using eager strategy", e);
            throw new IllegalStateException("IOException loading Spring PRD files using eager strategy", e);
        }
    }

    @Override
    public void unload(String resourceStateName) {
        cache.remove(resourceStateName);
    }

    @Override
    public boolean isLoaded(String resourceStateName) {
        return (cache.get(resourceStateName) != null);
    }
}
