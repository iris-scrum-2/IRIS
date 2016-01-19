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
import com.temenos.interaction.core.loader.ResourceStateLoadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author kwieconkowski
 */
public class SpringResourceStateLoadingStrategy implements ResourceStateLoadingStrategy<String> {
    private final Logger logger = LoggerFactory.getLogger(SpringResourceStateLoadingStrategy.class);

    @Override
    public List<ResourceStateResult> load(String location) {
        checkLocationOrThrowException(location);
        ApplicationContext PrdAppCtx = loadSpringContex(location);
        if (PrdAppCtx == null) {
            logger.warn("File not found while loading spring configuration in location: " + location);
            return null;
        }
        List<ResourceStateResult> resourceStates = new ArrayList<ResourceStateResult>();
        for (Map.Entry<String, ResourceState> springBean : PrdAppCtx.getBeansOfType(ResourceState.class).entrySet()) {
            resourceStates.add(new ResourceStateResult(springBean.getKey(), springBean.getValue()));
        }
        logger.info("Resource state loaded from spring configuration xml: " + location);
        return resourceStates;
    }

    private ApplicationContext loadSpringContex(String location) {
        ApplicationContext PrdAppCtx = null;
        try {
            PrdAppCtx = new ClassPathXmlApplicationContext(location);
        } catch (Exception e) {
        }
        return PrdAppCtx;
    }

    private void checkLocationOrThrowException(String location) {
        if (location == null || location.isEmpty()) {
            final String msg = "Passed URI is NULL or empty";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        } else if (!Paths.get(location).getFileName().toString().equals(location)) {
            final String msg = "Spring PRD file location must contain only the filename (no path)";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}