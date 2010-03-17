/*
 *  Copyright (C) 2010 John Casey.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.redhat.tools.nexus.guice;

import com.google.inject.Module;

/**
 * Marker interface to provide a plugin-private target for {@link java.util.ServiceLoader} to grab
 * components in a fashion not supported by Nexus' own component wiring. For instance, component
 * interfaces that are in plugin dependency jars.
 * 
 * To use, add the fully-qualified classname of your PluginPrivateModule implementation on its own
 * line to a file in your jar called META-INF/services/com.redhat.tools.nexus.guice.PluginPrivateModule
 */
public interface PluginPrivateModule
    extends Module
{
}
