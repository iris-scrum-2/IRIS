package com.temenos.interaction.core.loader.impl;

import com.temenos.interaction.core.hypermedia.ResourceState;

/**
 * Created by kwieconkowski on 15/01/2016.
 */
public class ResourceStateResult {
    public final String beanName;
    public final ResourceState resourceState;

    public ResourceStateResult(String beanName, ResourceState resourceState) {
        this.beanName = beanName;
        this.resourceState = resourceState;
    }
}
