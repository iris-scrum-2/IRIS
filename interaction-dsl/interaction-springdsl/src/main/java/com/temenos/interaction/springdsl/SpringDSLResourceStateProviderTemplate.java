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
import com.temenos.interaction.core.cache.Cache;
import com.temenos.interaction.core.resource.provider.DynamicRegistrationResourceStateProviderTemplate;
import com.temenos.interaction.core.hypermedia.ResourceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static com.temenos.interaction.core.loader.ResourceStateLoader.ResourceStateResult;
import com.temenos.interaction.core.loader.ResourceStateLoader;
import com.temenos.interaction.translate.mapper.ResourceStateMapper;

/**
 *
 * @author dgroves
 * @author hmanchala
 * @author kwieconkowski
 * @author andres
 */
public abstract class SpringDSLResourceStateProviderTemplate extends DynamicRegistrationResourceStateProviderTemplate implements DynamicRegistrationResourceStateProvider {

    private final Logger logger = LoggerFactory.getLogger(SpringDSLResourceStateProviderTemplate.class);

    protected Set<String> PRDconfigurationFileSources;
    // Map of ResourceState bean names, to paths.
    protected Properties beanMap;

    public SpringDSLResourceStateProviderTemplate(String antStylePattern, ResourceStateLoader<String> resourceStateLoader, Cache<String, ResourceState> cache, StateRegisteration stateRegisteration, ResourceStateMapper mapper, Properties beanMap) {
        super(antStylePattern, resourceStateLoader, cache, mapper, stateRegisteration);
        this.beanMap = beanMap;
        PRDconfigurationFileSources = new LinkedHashSet();
        discoverAllPrdFilesNames();
    }

    public void setResourceMap(Properties beanMap) {
        this.beanMap = beanMap;
    }

    protected ResourceState getResourceStateByNameOrByOldFormatName(String resourceStateName) {
        ResourceState resourceState = cache.get(resourceStateName);
        return resourceState != null ? resourceState : getResourceStateByOldFormat(resourceStateName);
    }

    protected ResourceState getResourceStateByOldFormat(String resourceStateName) {
        String oldResourceStateName = resourceStateName;

        oldResourceStateName = substringToFirstLineSymbol(oldResourceStateName);
        ResourceState resourceState = cache.get(oldResourceStateName);

        if (resourceState == null) {
            oldResourceStateName = replaceLastUnderscoreWithLine(oldResourceStateName);
            resourceState = cache.get(oldResourceStateName);
        }
        return resourceState;
    }

    protected String replaceLastUnderscoreWithLine(String resourceStateName) {
        if (!isThereLineSymbol(resourceStateName)) {
            int pos = resourceStateName.lastIndexOf("_");
            if (pos > 0) {
                resourceStateName = String.format("%s-%s", resourceStateName.substring(0, pos), resourceStateName.substring(pos + 1));
            }
        }
        return resourceStateName;
    }

    protected boolean isThereLineSymbol(String resourceStateName) {
        return resourceStateName.lastIndexOf("-") >= 0;
    }

    protected String substringToFirstLineSymbol(String newResourceStateName) {
        if (newResourceStateName.contains("-")) {
            newResourceStateName = newResourceStateName.substring(0, newResourceStateName.indexOf("-"));
        }
        return newResourceStateName;
    }

    protected String discoverNameOfPrdByUsingResourceStateName(String resourceStateName, boolean oldFormat) {
        String newResourceStateName = resourceStateName;

        newResourceStateName = substringToFirstLineSymbol(newResourceStateName);
        if (!oldFormat) {
            return String.format("IRIS-%s-PRD.xml", newResourceStateName);
        } else {
            return substringToFirstUnderscoreSymbol(newResourceStateName);
        }
    }

    protected String substringToFirstUnderscoreSymbol(String newResourceStateName) {
        int position = newResourceStateName.lastIndexOf("_");
        if (position > 3) {
            return String.format("IRIS-%s-PRD.xml", newResourceStateName.substring(0, position));
        }
        return null;
    }

    protected boolean loadResourceStatesFromPRD(String prdName) {
        if (prdName == null) {
            return false;
        }
        logger.info("Loading PRD file: " + prdName);
        List<ResourceStateResult> resourceStates = resourceStateLoader.load(prdName);
        if (resourceStates == null) {
            logger.warn("Could not find any resources with name: " + prdName);
            return false;
        }
        Map<String, ResourceState> tmp = new HashMap<>();
        for (ResourceStateResult resourceStateResult : resourceStates) {
            tmp.put(resourceStateResult.resourceStateId, resourceStateResult.resourceState);
        }
        cache.putAll(tmp);
        return true;
    }

    protected final void discoverAllPrdFilesNames() {
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
                    logger.info("Discovered path to PRD file: " + fileName);
                }
            } else {
                logger.warn("There was not found any PRD configuration xml files using given antStylePattern");
            }
        } catch (IOException e) {
            String msg = "IOException while loading PRD configuration xml files";
            logger.error(msg, e);
            throw new IllegalStateException(msg, e);
        }
    }
}
