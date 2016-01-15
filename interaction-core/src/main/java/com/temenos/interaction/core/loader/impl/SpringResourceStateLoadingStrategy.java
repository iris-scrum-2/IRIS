package com.temenos.interaction.core.loader.impl;

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
 * Created by kwieconkowski on 13/01/2016.
 */
public class SpringResourceStateLoadingStrategy implements ResourceStateLoadingStrategy<String> {
    private final Logger logger = LoggerFactory.getLogger(SpringResourceStateLoadingStrategy.class);

    @Override
    public List<ResourceStateResult> load(String location) {
        checkLocationOrThrowException(location);
        ApplicationContext PrdAppCtx = new ClassPathXmlApplicationContext(location);
        List<ResourceStateResult> resourceStates = new ArrayList<ResourceStateResult>();
        for (Map.Entry<String, ResourceState> springBean : PrdAppCtx.getBeansOfType(ResourceState.class).entrySet()) {
            resourceStates.add(new ResourceStateResult(springBean.getKey(), springBean.getValue()));
        }
        logger.info("Resource state loaded from spring configuration xml: " + location);
        return resourceStates;
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