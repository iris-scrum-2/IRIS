package com.temenos.interaction.springdsl;

import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.loader.Action;
import com.temenos.interaction.core.loader.Cache;
import com.temenos.interaction.core.loader.ResourceStateLoadingStrategy;
import com.temenos.interaction.core.loader.impl.CacheConcurrentImpl;
import com.temenos.interaction.core.loader.impl.ResourceStateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by kwieconkowski on 14/01/2016.
 */
public class EagerSpringDSLResourceStateProvider extends SpringDSLResourceStateProvider implements Action<Properties> {
    private final Logger logger = LoggerFactory.getLogger(EagerSpringDSLResourceStateProvider.class);

    private final Cache<String, ResourceState> cache = new CacheConcurrentImpl();
    private final String antStylePattern;
    private String[] PRDconfigurationFileSources;
    private ResourceStateLoadingStrategy<String> loadingStrategy;

    //"classpath*:/src-gen/**/IRIS-*-PRD.xml"
    public EagerSpringDSLResourceStateProvider(String antStylePattern, ResourceStateLoadingStrategy<String> loadingStrategy) {
        this.antStylePattern = antStylePattern;
        this.loadingStrategy = loadingStrategy;
        discoverAllPrdFiles();
        loadAllResourceStates();
    }

    //"classpath*:/src-gen/**/IRIS-*-PRD.xml"
    public EagerSpringDSLResourceStateProvider(String antStylePattern, ResourceStateLoadingStrategy<String> loadingStrategy, Properties beanMap) {
        super(beanMap);
        this.antStylePattern = antStylePattern;
        this.loadingStrategy = loadingStrategy;
        discoverAllPrdFiles();
        loadAllResourceStates();
    }

    public void setLoadingStrategy(ResourceStateLoadingStrategy<String> loadingStrategy) {
        this.loadingStrategy = loadingStrategy;
    }

    @Override
    public ResourceState getResourceState(String resourceStateName) {
        ResourceState resourceState = cache.get(resourceStateName);
        if (resourceState == null) {
            logger.error("Could not find resource state with name: " + resourceStateName);
        }
        return resourceState;
    }

    @Override
    public void unload(String resourceStateName) {
        cache.remove(resourceStateName);
    }

    @Override
    public boolean isLoaded(String resourceStateName) {
        return (cache.get(resourceStateName) != null);
    }

    protected synchronized void loadAllResourceStates() {
        // we assume we do not want to keep ResourceStates that are not defined on PRD files
        cache.removeAll();

        for (String locationOfPRD : PRDconfigurationFileSources) {
            loadResourceStatesFromPRD(locationOfPRD);
        }
    }

    private void loadResourceStatesFromPRD(String prdLocation) {
        List<ResourceStateResult> resourceStates = null;
        Map<String, ResourceState> tmp = new HashMap<String, ResourceState>();

        resourceStates = loadingStrategy.load(prdLocation);
        if (resourceStates == null) {
            logger.warn("Could not find any resources with file pattern: " + prdLocation);
        }
        for (ResourceStateResult resourceStateResult : resourceStates) {
            tmp.put(resourceStateResult.beanName, resourceStateResult.resourceState);
        }
        cache.putAll(tmp);
    }

    public void discoverAllPrdFiles() {
        final ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        final Resource[] locationsPRD;
        try {
            locationsPRD = patternResolver.getResources(antStylePattern);
            if (locationsPRD != null) {
                PRDconfigurationFileSources = new String[locationsPRD.length];
                for (int i = 0; i < locationsPRD.length; i++) {
                    PRDconfigurationFileSources[i] = Paths.get(locationsPRD[i].getURI().getPath().substring(1)).getFileName().toString();
                }
            } else {
                logger.warn("Spring DSL eager loading strategy default, could not found any PRD spring configuration xml files");
            }
        } catch (IOException e) {
            logger.error("IOException loading Spring PRD files using eager strategy", e);
            throw new IllegalStateException("IOException loading Spring PRD files using eager strategy", e);
        }
    }

    /* TODO implment this method */
    public String discoverPrdFileFromResourceState(String resourceStateName) {

        String beanXml = String.format("IRIS-{0}-PRD.xml", resourceStateName.contains("-")
                ? resourceStateName.substring(0, resourceStateName.indexOf("-")) : resourceStateName);

    /*
        // Attempt to create Spring context based on current resource filename pattern
        ApplicationContext context = createApplicationContext(beanXml);

        if (context == null) {
            // Failed to create Spring context using current resource filename pattern so use old pattern
            int pos = tmpResourceName.lastIndexOf("_");

            if (pos > 3){
                tmpResourceName = tmpResourceName.substring(0, pos);
                beanXml = "IRIS-" + tmpResourceName + "-PRD.xml";

                context = createApplicationContext(beanXml);

                if (context != null) {
                    // Successfully created Spring context using old resource filename pattern

                    // Convert resource state name to old resource name format
                    pos = tmpResourceStateName.lastIndexOf("-");

                    if (pos < 0){
                        pos = tmpResourceStateName.lastIndexOf("_");

                        if (pos > 0){
                            tmpResourceStateName = tmpResourceStateName.substring(0, pos) + "-" + tmpResourceStateName.substring(pos+1);
                        }
                    }
                }
            }
        }

        if(context != null) {
            result = loadAllResourceStatesFromFile(context, tmpResourceStateName);
        }
        */
        return null;
    }

    @Override
    /* TODO: correct next day */
    public void execute(Properties properties) {
        String prdLocation = (String) properties.getProperty("Mode");

        for (Object key : properties.keySet()) {
            unload(key.toString());
        }
        // reloardPRDfile(map.get(key.toString()));
        // loadResourceStatesFromPRD(prdLocation);
    }
}