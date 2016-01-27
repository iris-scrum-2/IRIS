package com.temenos.interaction.core.resource;

import com.temenos.interaction.core.resource.Mapper;
import java.util.HashMap;

/*
 * #%L
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


import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.loader.ResourceStateLoader.ResourceStateResult;

public class ResourceStateMapper implements Mapper<ResourceStateResult>{
	private Map<String, Set<String>> resourceStatesByPath;
	private Map<String, Set<String>> methodsByResourceStateName;
	private Map<String, String> resourceStatesByRequest;
	private Map<String, String> resourcePathsByState;
	
	private final Logger logger = LoggerFactory.getLogger(ResourceStateMapper.class);
	
	public ResourceStateMapper(){
		this.resourceStatesByPath = new HashMap<String, Set<String>>();
		this.methodsByResourceStateName = new HashMap<String, Set<String>>();
		this.resourceStatesByRequest = new HashMap<String, String>();
		this.resourcePathsByState = new HashMap<String, String>();
	}
	
	public ResourceStateMapper(Map<String, Set<String>> statesByPath, Map<String, Set<String>> methodsByStateName,
			Map<String, String> statesByRequest, Map<String, String> pathsByState){
		this.resourceStatesByPath = statesByPath;
		this.methodsByResourceStateName = methodsByStateName;
		this.resourceStatesByRequest = statesByRequest;
		this.resourcePathsByState = pathsByState;
	}
	
	@Override
	public void map(ResourceStateResult source) {
		mapResourcePathsToState(source);
		mapResourceMethodsToState(source);
		mapResourceStatesToRequests(source);
		mapResourceStatesToPaths(source);
	}

	private void mapResourcePathsToState(ResourceStateResult source){
		resourcePathsByState.put(source.getResourceStateId(), source.getPath());
	}
	
	private void mapResourceMethodsToState(ResourceStateResult source){
		Set<String> methods = methodsByResourceStateName.get(source.getResourceStateId());
		if(methods == null){
			logger.debug("No methods have been mapped to resource state: "+source.getResourceStateId());
			methods = new HashSet<String>();
		}
		for(String method : source.getMethods()){
			methods.add(method);
		}
		methodsByResourceStateName.put(source.getResourceStateId(), methods);
	}
	
	private void mapResourceStatesToRequests(ResourceStateResult source){
		for(String method : source.getMethods()){
			String request = method + " " + source.getPath();
			if(resourceStatesByRequest.containsKey(request)){
				logger.error("A mapping already exists for "+request+"; this will be overwritten.");
			}
			resourceStatesByRequest.put(request, source.getResourceStateId());
		}
	}
	
	private void mapResourceStatesToPaths(ResourceStateResult source){
		Set<String> stateNames = resourceStatesByPath.get(source.getPath());
		if(stateNames == null){
			stateNames = new HashSet<String>();
		}
		stateNames.add(source.getResourceStateId());
		resourceStatesByPath.put(source.getPath(), stateNames);
	}

    public Map<String, Set<String>> getResourceStatesByPath() {
        return resourceStatesByPath;
    }

    public Map<String, Set<String>> getResourceMethodsByState() {
    	return methodsByResourceStateName;
    }
    
    public Map<String, String> getResourceStatesByRequest() {
        return resourceStatesByRequest;
    }

    public Map<String, String> getResourcePathsByState() {
        return resourcePathsByState;
    }

}
