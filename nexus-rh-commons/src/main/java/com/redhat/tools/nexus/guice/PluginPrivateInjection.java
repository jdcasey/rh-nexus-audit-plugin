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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.configuration.application.ApplicationConfiguration;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.ServiceLoader;

// ICK; it's a classic singleton! Unfortunately, since a Nexus plugin doesn't have a single point of 
// entry that works (NexusPlugin is vestigial), we have to do it this way. Classloader separation
// between plugins SHOULD mean that each plugin gets a separate RHCommons.injector instance, but that
// a single instance is shared by all components within that plugin.
//
// NOTE: I'm not sure what will happen when one plugin uses another...there may be classloading
// issues there.
public final class PluginPrivateInjection
    extends AbstractModule
{

    private static final Logger logger = LoggerFactory.getLogger( PluginPrivateInjection.class );

    private static Injector injector;

    private final NexusCommonsConfiguration nexusConfig;

    private PluginPrivateInjection( final NexusCommonsConfiguration nexusConfig )
    {
        this.nexusConfig = nexusConfig;
    }

    public static void initialize( final NexusCommonsConfiguration nexusConfig )
    {
        if ( injector == null )
        {
            injector = Guice.createInjector( new PluginPrivateInjection( nexusConfig ) );
        }
    }

    public static Injector getInjector()
    {
        if ( injector == null )
        {
            final StringBuilder message = new StringBuilder();
            message.append( "This plugin's private Guice injector has not been initialized!" );
            message.append( "\n\nHint: Make sure you have a component in your plugin that implements org.sonatype.nexus.rest.NexusApplicationCustomizer." );
            message.append( "\nThen, make sure that component calls com.redhat.tools.nexus.guice.PluginPrivateInjection.initialize(..)!" );

            throw new IllegalStateException( message.toString() );
        }

        return injector;
    }

    @Override
    protected void configure()
    {
        logger.info( "Loaded by: " + getClass().getClassLoader() );
        logger.info( "PluginPrivateModule loaded by: " + PluginPrivateModule.class.getClassLoader() );

        final ServiceLoader<PluginPrivateModule> pluginPrivateModules =
            ServiceLoader.load( PluginPrivateModule.class, PluginPrivateModule.class.getClassLoader() );

        if ( pluginPrivateModules == null )
        {
            throw new IllegalStateException(
                                             "No plugin-private Guice modules located! Cannot proceed with creating a private Guice instance." );
        }

        for ( final PluginPrivateModule module : pluginPrivateModules )
        {
            logger.info( "Installing plugin-private Guice module: " + module.getClass().getName() );
            install( module );
        }

        if ( nexusConfig.getApplicationConfiguration() != null )
        {
            logger.info( "Binding Nexus application configuration: " + nexusConfig.getApplicationConfiguration() );
            bind( ApplicationConfiguration.class ).toInstance( nexusConfig.getApplicationConfiguration() );
        }

        if ( nexusConfig.getRepositoryRegistry() != null )
        {
            logger.info( "Binding Nexus repository registry: " + nexusConfig.getRepositoryRegistry() );
            bind( RepositoryRegistry.class ).toInstance( nexusConfig.getRepositoryRegistry() );
        }
    }

}
