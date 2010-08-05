/*
 *  Copyright (c) 2010 Red Hat, Inc.
 *  
 *  This program is licensed to you under Version 3 only of the GNU
 *  General Public License as published by the Free Software 
 *  Foundation. This program is distributed in the hope that it will be 
 *  useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 *  PURPOSE.
 *  
 *  See the GNU General Public License Version 3 for more details.
 *  You should have received a copy of the GNU General Public License 
 *  Version 3 along with this program. 
 *  
 *  If not, see http://www.gnu.org/licenses/.
 */

package com.redhat.tools.nexus.audit.statik;

import org.sonatype.nexus.plugins.rest.AbstractNexusResourceBundle;
import org.sonatype.nexus.plugins.rest.DefaultStaticResource;
import org.sonatype.nexus.plugins.rest.NexusResourceBundle;
import org.sonatype.nexus.plugins.rest.StaticResource;

import java.util.ArrayList;
import java.util.List;

public class AuditResourceBundle
    extends AbstractNexusResourceBundle
    implements NexusResourceBundle
{
    @Override
    public List<StaticResource> getContributedResouces()
    {
        final List<StaticResource> result = new ArrayList<StaticResource>();

        result.add( new DefaultStaticResource( getClass().getResource( "/static/js/nexus-audit-plugin-all.js" ),
                                               "/js/repoServer/nexus-audit-plugin-all.js", "application/x-javascript" ) );

        return result;
    }

}
