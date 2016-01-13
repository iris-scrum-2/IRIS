package com.temenos.interaction.core.loader;

import java.net.URI;

/**
 *
 * @param <T>
  */
public interface LoadingStrategy<T, S> {
    public void setSource(S source);       
    public T load(String name);
}

