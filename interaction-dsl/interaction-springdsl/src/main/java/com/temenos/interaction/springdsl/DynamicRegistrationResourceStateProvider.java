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

import com.temenos.interaction.core.hypermedia.ResourceStateProvider;
import java.util.Collection;


/**
 * Resource state providers that implement this interface will provide a
 * mechanism for dynamically registering resources i.e. at runtime after startup
 *
 * The responsibility of registering resources should be removed from the
 * provider in the future.
 * 
 * @author mlambert
 * @author andres
 */
public interface DynamicRegistrationResourceStateProvider extends ResourceStateProvider {

    void setStateRegisteration(StateRegisteration stateRegisteration);

    public void loadAndMapFiles(Collection<String> files);
    
    /**
     *
     * @param resourceStateName
     */
    public void unload(String resourceStateName);
}
