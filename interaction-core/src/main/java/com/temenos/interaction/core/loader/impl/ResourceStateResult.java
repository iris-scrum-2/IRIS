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

import com.temenos.interaction.core.hypermedia.ResourceState;

/**
 * Class grouping a ResourceState and a its id. Needed for keeping the bean id
 * when loading from PRD files.
 * 
 * @author kwieconkowski
 * @author andres
 * @author dgroves
 */
public class ResourceStateResult {
    public final String resourceStateId;
    public final ResourceState resourceState;

    public ResourceStateResult(String resourceStateId, ResourceState resourceState) {
        this.resourceStateId = resourceStateId;
        this.resourceState = resourceState;
    }
}
