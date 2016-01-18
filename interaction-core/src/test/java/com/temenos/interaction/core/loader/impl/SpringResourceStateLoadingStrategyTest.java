package com.temenos.interaction.core.loader.impl;

/*
 * #%L
 * interaction-core
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

import com.temenos.interaction.core.loader.ResourceStateLoadingStrategy;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanDefinitionStoreException;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by kwieconkowski on 15/01/2016.
 */
public class SpringResourceStateLoadingStrategyTest {

    private static final String SPRING_PRD_FILE = "IRIS-testResources-PRD.xml";
    private static final String SPRING_EMPTY_PRD_FILE = "IRIS-empty-PRD.xml";
    private ResourceStateLoadingStrategy<String> loadingStrategy;

    @Before
    public void setUp() {
        loadingStrategy = new SpringResourceStateLoadingStrategy();
    }

    @Test
    public void load_shouldReturnFilledList() {
        List<ResourceStateResult> result = loadingStrategy.load(SPRING_PRD_FILE);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void load_emptyFile_shouldReturnEmptyList() {
        List<ResourceStateResult> result = loadingStrategy.load(SPRING_EMPTY_PRD_FILE);
        assertTrue(result.isEmpty());
    }

    @Test(expected = BeanDefinitionStoreException.class)
    public void load_fileNotExist_shouldThrowException() {
        List<ResourceStateResult> result = loadingStrategy.load("123.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void load_nullLocation_shouldThrowException() {
        try {
            List<ResourceStateResult> result = loadingStrategy.load(null);
        } catch (IllegalArgumentException e) {
            assertEquals("Passed URI is NULL or empty", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void load_locationWithPath_shouldThrowException() {
        try {
            List<ResourceStateResult> result = loadingStrategy.load(File.separator + SPRING_PRD_FILE);
        } catch (IllegalArgumentException e) {
            assertEquals("Spring PRD file location must contain only the filename (no path)", e.getMessage());
            throw e;
        }
    }
}