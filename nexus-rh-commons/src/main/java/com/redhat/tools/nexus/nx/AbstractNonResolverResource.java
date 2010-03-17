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

package com.redhat.tools.nexus.nx;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.rest.AbstractNexusPlexusResource;

import com.redhat.tools.nexus.guice.NexusCommonsConfiguration;
import com.redhat.tools.nexus.guice.PluginPrivateInjectable;
import com.redhat.tools.nexus.guice.PluginPrivateInjection;

public abstract class AbstractNonResolverResource
    extends AbstractNexusPlexusResource
    implements Startable, PluginPrivateInjectable
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Override
    public void start()
        throws StartingException
    {
        try
        {
            final NexusCommonsConfiguration nxConfig =
                new NexusCommonsConfiguration().withApplicationConfiguration( getApplicationConfiguration() )
                                               .withRepositoryRegistry( getRepoRegistry() );

            PluginPrivateInjection.initialize( nxConfig );
            PluginPrivateInjection.getInjector().injectMembers( this );
        }
        catch ( final Throwable t )
        {
            logger.error( "Failed to initialize! Reason: " + t.getMessage(), t );
        }
    }

    @Override
    public void stop()
        throws StoppingException
    {
    }

}
