package com.temenos.interaction.core.loader.impl;

import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.loader.ResourceStateLoadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kwieconkowski on 13/01/2016.
 */
public class SpringResourceStateLoadingStrategy implements ResourceStateLoadingStrategy<String> {
    private final Logger logger = LoggerFactory.getLogger(SpringResourceStateLoadingStrategy.class);

    @Override
    public List<ResourceState> load(String location) {
        checkLocationOrThrowException(location);
        ApplicationContext PrdAppCtx = new ClassPathXmlApplicationContext(location);
        checkAppCtxOrThrowException(location, PrdAppCtx);
        List<ResourceState> resourceStates = new ArrayList<ResourceState>();
        for (Map.Entry<String, ResourceState> springBean : PrdAppCtx.getBeansOfType(ResourceState.class).entrySet()) {
            resourceStates.add(springBean.getValue());
        }
        logger.info("Resource state loaded from spring configuration xml: " + location);
        return resourceStates;
    }

    private void checkAppCtxOrThrowException(String location, ApplicationContext prdAppCtx) {
        if (prdAppCtx == null) {
            logger.error("There is no file in given location or not a spring configuration xml: " + location);
            throw new IllegalArgumentException("There is no file in given location or not a spring configuration xml: " + location);
        }
    }

    private void checkLocationOrThrowException(String location) {
        if (location == null) {
            logger.error("Passed URI is NULL");
            throw new IllegalArgumentException("Passed URI is NULL");
        }
    }
}