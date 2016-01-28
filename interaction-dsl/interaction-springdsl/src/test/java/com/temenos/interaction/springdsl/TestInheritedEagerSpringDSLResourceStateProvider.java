package com.temenos.interaction.springdsl;

/*
 * #%L
 * interaction-springdsl
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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

import com.temenos.interaction.core.cache.Cache;
import com.temenos.interaction.core.cache.CacheConcurrentImpl;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateProvider;
import com.temenos.interaction.core.loader.SpringDSLResourceStateLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;
import com.temenos.interaction.core.loader.ResourceStateLoader;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mock;

/**
 * @author kwieconkowski
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestInheritedEagerSpringDSLResourceStateProvider extends TestSpringDSLResourceStateProvider {

    @Autowired
    private InheritedEagerSpringDSLResourceStateProvider springDSLResourceStateProvider;
    private boolean initialized;

    @Before
    public void setUp() throws Exception {
        /* We want to enforce that tests in the parent class TestSpringDSLResourceStateProvider
        are invoked with our children class InheritedEagerSpringDSLResourceStateProvider, to ensure back compatibility */
        if (!initialized) {
            initialized = true;
            resourceStateProvider = springDSLResourceStateProvider;
        }
    }

    @Test
    public void testGetResourceState() {
        ResourceState resourceState = springDSLResourceStateProvider.getResourceState("SimpleModel_Home_home");
        assertNotNull(resourceState);
        assertEquals("home", resourceState.getName());
    }

    @Test
    public void testGetResourceState_oldFormat() {
        InheritedEagerSpringDSLResourceStateProvider springDSLResourceStateProvider = getDefaultClass();
        springDSLResourceStateProvider.unload("SimpleModel_Home_home");
        ResourceState resourceState = springDSLResourceStateProvider.getResourceState("SimpleModel_Home_home");
        assertNotNull(resourceState);
        assertEquals("home", resourceState.getName());
    }

    @Test
    public void testUnload() {
        InheritedEagerSpringDSLResourceStateProvider springDSLResourceStateProvider = getDefaultClass();
        ResourceState resourceState;
        final String beanName = "SimpleModel_Home-home";

        resourceState = springDSLResourceStateProvider.getResourceState(beanName);
        assertNotNull(resourceState);
        springDSLResourceStateProvider.unload(beanName);
        resourceState = springDSLResourceStateProvider.getResourceState(beanName);
        assertNull(resourceState);
    }

    @Test
    public void testAddState() {
        InheritedEagerSpringDSLResourceStateProvider springDSLResourceStateProvider = getDefaultClass();
        springDSLResourceStateProvider.stateRegisteration = mock(StateRegisteration.class);

        final String resourceStateName = "SimpleModel_Home_home-ServiceDocument";
        Properties properties = new Properties();
        properties.setProperty(resourceStateName, "GET /");

        springDSLResourceStateProvider.addState(resourceStateName, properties);
    }

    @Test
    public void testSetLoadingStrategy_betterCodeCoverage() {
        springDSLResourceStateProvider.setLoadingStrategy(new SpringDSLResourceStateLoader());
    }

    @Test
    public void testIsLoaded() {
        assertTrue(springDSLResourceStateProvider.isLoaded("SimpleModel_Home_home"));
    }

    @Test
    public void testGetResourceStatesByPath() {
        Properties properties = new Properties();
        properties.put("SimpleModel_Home_home", "GET /test");
        ResourceStateProvider rsp = getDefaultClass(properties);
        Map<String, Set<String>> statesByPath = rsp.getResourceStatesByPath();
        assertEquals(1, statesByPath.size());
        assertEquals(1, statesByPath.get("/test").size());
        assertEquals("SimpleModel_Home_home", statesByPath.get("/test").toArray()[0]);
    }

    private InheritedEagerSpringDSLResourceStateProvider getDefaultClass() {
        return getDefaultClass(null);
    }

    private InheritedEagerSpringDSLResourceStateProvider getDefaultClass(Properties properties) {
        return getDefaultClass("classpath*:/**/IRIS-*-PRD.xml", new SpringDSLResourceStateLoader(), new CacheConcurrentImpl(), properties);
    }

    private InheritedEagerSpringDSLResourceStateProvider getDefaultClass(String antStylePattern, ResourceStateLoader<String> loadingStrategy, Cache<String, ResourceState> cache, Properties properties) {
        return new InheritedEagerSpringDSLResourceStateProvider(antStylePattern, loadingStrategy, cache, properties);
    }
}
