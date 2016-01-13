package com.temenos.interaction.core.loader;

import com.temenos.interaction.core.hypermedia.ResourceState;
import java.net.URI;

/**
 *
 * @author andres
 */
public interface LazyResourceStateLoadingStrategy extends LazyLoadingStrategy<ResourceState, URI> {
    public ResourceState doSomethingWithResourceThatWasObtainedLazily(String name);
}