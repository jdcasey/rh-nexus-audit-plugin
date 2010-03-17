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

package com.redhat.tools.nexus.audit.guice;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.sonatype.nexus.configuration.application.ApplicationConfiguration;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.redhat.tools.nexus.guice.NexusCommonsConfiguration;
import com.redhat.tools.nexus.guice.PluginPrivateInjection;
import com.redhat.tools.nexus.response.WebResponseSerializer;

public class PrivateAuditModuleTest
{

    @Inject
    @Named( "audit" )
    private WebResponseSerializer responseSerializer;

    @Test
    public void loadFromInjection()
    {
        final ApplicationConfiguration config = createMock( ApplicationConfiguration.class );
        expect( config.getConfigurationDirectory() ).andReturn( null ).anyTimes();

        final RepositoryRegistry reg = createMock( RepositoryRegistry.class );

        final NexusCommonsConfiguration nxConfig =
            new NexusCommonsConfiguration().withApplicationConfiguration( config ).withRepositoryRegistry( reg );

        replay( config );
        replay( reg );

        PluginPrivateInjection.initialize( nxConfig );
        PluginPrivateInjection.getInjector().injectMembers( this );

        assertNotNull( responseSerializer );

        verify( config );
        verify( reg );
    }
}
