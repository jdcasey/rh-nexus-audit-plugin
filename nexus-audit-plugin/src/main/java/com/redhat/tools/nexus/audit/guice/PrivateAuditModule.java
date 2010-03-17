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

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.redhat.tools.nexus.audit.serial.AuditResponseSerializer;
import com.redhat.tools.nexus.audit.serial.store.AuditStore;
import com.redhat.tools.nexus.audit.serial.store.JsonAuditStore;
import com.redhat.tools.nexus.guice.PluginPrivateModule;
import com.redhat.tools.nexus.response.WebResponseSerializer;

public class PrivateAuditModule
    extends AbstractModule
    implements PluginPrivateModule
{

    @Override
    protected void configure()
    {
        bind( AuditStore.class ).annotatedWith( Names.named( "json" ) )
                                .to( JsonAuditStore.class )
                                .in( Scopes.SINGLETON );

        bind( WebResponseSerializer.class ).annotatedWith( Names.named( "audit" ) )
                                           .to( AuditResponseSerializer.class )
                                           .in( Scopes.SINGLETON );
    }

}
