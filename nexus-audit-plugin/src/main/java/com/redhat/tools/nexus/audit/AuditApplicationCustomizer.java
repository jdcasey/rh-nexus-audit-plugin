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

package com.redhat.tools.nexus.audit;

import org.sonatype.nexus.configuration.application.ApplicationConfiguration;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.rest.NexusApplication;
import org.sonatype.nexus.rest.NexusApplicationCustomizer;
import org.sonatype.plexus.rest.RetargetableRestlet;

import com.redhat.tools.nexus.guice.NexusCommonsConfiguration;
import com.redhat.tools.nexus.guice.PluginPrivateInjection;

import javax.inject.Inject;
import javax.inject.Named;

public class AuditApplicationCustomizer
    implements NexusApplicationCustomizer
{

    @Inject
    @Named( "protected" )
    private RepositoryRegistry protectedRepositoryRegistry;

    @Inject
    private ApplicationConfiguration appConfig;

    @Override
    public void customize( final NexusApplication nexusApplication, final RetargetableRestlet root )
    {
        final NexusCommonsConfiguration config =
            new NexusCommonsConfiguration().withApplicationConfiguration( appConfig )
                                           .withRepositoryRegistry( protectedRepositoryRegistry );

        PluginPrivateInjection.initialize( config );
    }

}
