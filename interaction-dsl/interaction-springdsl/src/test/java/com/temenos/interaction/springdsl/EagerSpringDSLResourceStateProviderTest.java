package com.temenos.interaction.springdsl;

import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.loader.impl.SpringResourceStateLoadingStrategy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by kwieconkowski on 15/01/2016.
 */
public class EagerSpringDSLResourceStateProviderTest {
    private EagerSpringDSLResourceStateProvider springDSLResourceStateProvider;

    @Before
    public void setUp() throws Exception {
        springDSLResourceStateProvider = new EagerSpringDSLResourceStateProvider("classpath*:/**/IRIS-*-PRD.xml", new SpringResourceStateLoadingStrategy());
    }

    @Test
    public void testGetResourceState() throws Exception {
        ResourceState resourceState = springDSLResourceStateProvider.getResourceState("SimpleModel_Home_home");
        assertNotNull(resourceState);
        assertEquals("home", resourceState.getName());
    }

    @Test
    public void testUnload() throws Exception {
        ResourceState resourceState;
        final String beanName = "SimpleModel_Home_home";

        resourceState = springDSLResourceStateProvider.getResourceState(beanName);
        assertNotNull(resourceState);
        springDSLResourceStateProvider.unload(beanName);
        resourceState = springDSLResourceStateProvider.getResourceState(beanName);
        assertNull(resourceState);
    }

    @Test
    public void testIsLoaded() throws Exception {

    }

    @Test
    public void testLoadAllResourceStates() throws Exception {

    }

    @Test
    public void testDiscoverAllPrdFiles() throws Exception {

    }
}