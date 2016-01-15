package com.temenos.interaction.core.loader;

import java.util.List;

public interface LoadingStrategy<T, S> {
    public List<T> load(S source);
}