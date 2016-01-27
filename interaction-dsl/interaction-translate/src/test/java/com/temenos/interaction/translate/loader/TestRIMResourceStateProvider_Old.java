package com.temenos.interaction.translate.loader;

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


import static org.junit.Assert.*;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Matchers.anyString;
import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.temenos.interaction.core.cache.Cache;
import com.temenos.interaction.core.hypermedia.Event;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.loader.ResourceStateLoader;
import com.temenos.interaction.core.loader.ResourceStateLoader.ResourceStateResult;
import com.temenos.interaction.springdsl.StateRegisteration;
import com.temenos.interaction.translate.mapper.ResourceStateMapper;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.eq;
import static org.hamcrest.CoreMatchers.nullValue;

public class TestRIMResourceStateProvider_Old {
	
	private RIMResourceStateProvider_Old resourceStateProvider;
	
	private @Mock ResourceStateLoader<String> loader;
	private @Mock ResourceStateResult alpha, beta, theta;
	private @Mock StateRegisteration stateRegistration;
	private @Mock Cache<String, ResourceStateResult> cache;
	private @Mock ResourceStateMapper mapper;
	
	private @Spy HashSet<String> sources;
	private String antPattern;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(loader.load(anyString())).thenReturn(resourceStateResultsStub());
		when(alpha.getMethods()).thenReturn(new String[]{"GET"});
		when(alpha.getPath()).thenReturn("/alpha");
		when(alpha.getResourceStateId()).thenReturn("Alpha");
		when(alpha.getResourceState()).thenReturn(mock(ResourceState.class));
		when(beta.getMethods()).thenReturn(new String[]{"GET", "POST"});
		when(beta.getPath()).thenReturn("/beta");
		when(beta.getResourceStateId()).thenReturn("Beta");
		when(beta.getResourceState()).thenReturn(mock(ResourceState.class));
		when(theta.getMethods()).thenReturn(new String[]{"POST", "PUT"});
		when(theta.getPath()).thenReturn("/theta");
		when(theta.getResourceStateId()).thenReturn("Theta");
		when(theta.getResourceState()).thenReturn(mock(ResourceState.class));
		when(cache.get(eq("Alpha"))).thenReturn(alpha);
		when(cache.get(eq("Beta"))).thenReturn(beta);
		when(cache.get(eq("Theta"))).thenReturn(theta);
		when(mapper.getResourceMethodsByState()).thenReturn(null);
		when(mapper.getResourcePathsByState()).thenReturn(null);
		when(mapper.getResourceStatesByPath()).thenReturn(null);
		when(mapper.getResourceStatesByRequest()).thenReturn(null);
		this.resourceStateProvider = new RIMResourceStateProvider_Old(antPattern, 
				cache, sources, mapper, loader, stateRegistration);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testLoadAndMapFiles(){
		//when
		this.resourceStateProvider.loadAndMapFiles(rimFilenamesStub(), false);
		//then
		verify(loader, times(1)).load(eq("numbers.rim"));
		verify(alpha).getResourceStateId();
		verify(beta).getResourceStateId();
		verify(theta).getResourceStateId();
		verify(mapper).map(alpha);
		verify(mapper).map(beta);
		verify(mapper).map(theta);
		verify(stateRegistration, times(0)).register(anyString(), anyString(), 
				anySetOf(String.class));
		verify(cache).putAll(anyMapOf(String.class, ResourceStateResult.class));
	}
	
	@Test
	public void testLoadAndMapFilesWithWinkRegistrationEnabled(){
		//when
		this.resourceStateProvider.loadAndMapFiles(rimFilenamesStub(), true);
		//then
		verify(loader, times(1)).load(eq("numbers.rim"));
		verify(alpha, atLeastOnce()).getResourceStateId();
		verify(beta, atLeastOnce()).getResourceStateId();
		verify(theta, atLeastOnce()).getResourceStateId();
		verify(mapper).map(alpha);
		verify(mapper).map(beta);
		verify(mapper).map(theta);
		verify(stateRegistration, atLeastOnce()).register(eq("Alpha"), eq("/alpha"), 
				eq(new HashSet<String>(Arrays.asList(new String[]{"GET"}))));
		verify(stateRegistration, atLeastOnce()).register(eq("Beta"), eq("/beta"), 
				eq(new HashSet<String>(Arrays.asList(new String[]{"GET", "POST"}))));
		verify(stateRegistration, atLeastOnce()).register(eq("Theta"), eq("/theta"), 
				eq(new HashSet<String>(Arrays.asList(new String[]{"POST", "PUT"}))));
		verify(cache).putAll(anyMapOf(String.class, ResourceStateResult.class));
	}

	@Test
	public void testGetResourceState(){
		//when
		assertThat(this.resourceStateProvider.getResourceState("Alpha"), 
				sameInstance(alpha.getResourceState()));
		//then
		verify(cache).get("Alpha");
	}
	
	@Test
	public void testGetResourecStateWithInvalidStateName(){
		//when
		assertThat(this.resourceStateProvider.getResourceState("Gamma"),
				nullValue());
		//then
		verify(cache).get("Gamma");
	}
	
	@Test
	public void testDetermineState(){
		//given
		when(mapper.getResourceStatesByRequest()).thenReturn(resourceStatesByRequestStub());
		//when
		assertThat(this.resourceStateProvider.determineState(new Event("Number", "GET"), 
				"/alpha"), sameInstance(alpha.getResourceState()));
		//then
		verify(this.mapper).getResourceStatesByRequest();
	}
	
	@Test
	public void testDetermineStateMapperWithEmptyMapping(){
		//given
		when(mapper.getResourceStatesByRequest()).thenReturn(new HashMap<String, String>());
		//when
		assertThat(this.resourceStateProvider.determineState(
			new Event("Number", "GET"), "/alpha"
		), nullValue());
		//then
		verify(this.mapper).getResourceStatesByRequest();
	}
	
	@Test
	public void testIsLoadedCacheIsPopulated(){
		//when
		assertTrue(resourceStateProvider.isLoaded("Alpha"));
		//then
		verify(this.cache).get(eq("Alpha"));
	}
	
	@Test
	public void testIsLoadedWithEmptyCache(){
		//given
		when(cache.get(anyString())).thenReturn(null);
		//when
		assertFalse(resourceStateProvider.isLoaded("Alpha"));
		//then
		verify(this.cache).get(eq("Alpha"));
	}
	
	private List<ResourceStateResult> resourceStateResultsStub(){
		return new ArrayList<ResourceStateResult>(Arrays.asList(new ResourceStateResult[]{
			alpha, beta, theta
		}));
	}
	
	private List<String> rimFilenamesStub(){
		return new ArrayList<String>(Arrays.asList(new String[]{
			"numbers.rim"
		}));
	}
	
	private Map<String, String> resourceStatesByRequestStub(){
		return new HashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
				this.put("GET /alpha", "Alpha");
				this.put("GET /beta", "Beta");
				this.put("POST /beta", "Beta");
				this.put("POST /theta", "Theta");
				this.put("PUT /theta", "Theta");
			}
		};
	}
}
