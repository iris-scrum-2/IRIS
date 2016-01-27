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
import com.temenos.interaction.core.hypermedia.ResourceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

import com.temenos.interaction.core.loader.ResourceStateLoader;
import com.temenos.interaction.translate.mapper.ResourceStateMapper;

/**
 *
 * @author dgroves
 * @author hmanchala
 * @author kwieconkowski
 * @author andres
 */
public class LazySpringDSLResourceStateProvider extends SpringDSLResourceStateProviderTemplate implements DynamicRegistrationResourceStateProvider {

    private final Logger logger = LoggerFactory.getLogger(LazySpringDSLResourceStateProvider.class);

    public LazySpringDSLResourceStateProvider(String antStylePattern, ResourceStateLoader<String> resourceStateLoader, Cache<String, ResourceState> cache, StateRegisteration stateRegisteration, ResourceStateMapper mapper, Properties beanMap) {
        super(antStylePattern, resourceStateLoader, cache, stateRegisteration, mapper, beanMap);
    }

    @Override
    public ResourceState getResourceState(String resourceStateName) {
        logger.info("Getting resource state name: " + resourceStateName);
        if (!loadResourceStatesFromPRD(discoverNameOfPrdByUsingResourceStateName(resourceStateName, false))
                && !loadResourceStatesFromPRD(discoverNameOfPrdByUsingResourceStateName(resourceStateName, true))) {
            logger.error("PRD configuration file for resource state name " + resourceStateName + "not found.");
            return null;
        }
        ResourceState resourceState = getResourceStateByNameOrByOldFormatName(resourceStateName);
        if (resourceState == null) {
            logger.error("Could not find resource state name: " + resourceStateName);
        }
        return resourceState;
    }
}
