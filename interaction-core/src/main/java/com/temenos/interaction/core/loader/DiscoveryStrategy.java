/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.temenos.interaction.core.loader;

import java.util.List;

/**
 *
 * @author andres
 */
public interface DiscoveryStrategy<T, S> {
    public void setSource(S source);       
    public List<T> discoverAll();
}