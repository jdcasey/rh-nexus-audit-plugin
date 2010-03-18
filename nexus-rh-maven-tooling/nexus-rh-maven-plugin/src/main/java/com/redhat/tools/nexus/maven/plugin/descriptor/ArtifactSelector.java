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

package com.redhat.tools.nexus.maven.plugin.descriptor;

import org.apache.maven.artifact.Artifact;

import java.util.Collection;
import java.util.HashSet;

class ArtifactSelector
{
    private final Collection<ArtifactId> includes;

    private final Collection<ArtifactId> excludes;

    public ArtifactSelector( final Artifact projectArtifact, final ArtifactSet artifactSet )
    {
        String groupPrefix = null;
        if ( artifactSet != null )
        {
            includes = toIds( artifactSet.getIncludes() );
            excludes = toIds( artifactSet.getExcludes() );
            groupPrefix = artifactSet.getGroupPrefix();
        }
        else
        {
            includes = new HashSet<ArtifactId>();
            excludes = new HashSet<ArtifactId>();
        }

        if ( groupPrefix != null && groupPrefix.length() > 0 )
        {
            includes.add( new ArtifactId( groupPrefix + "*", "*", "*", "*" ) );
        }

        if ( projectArtifact != null && !includes.isEmpty() )
        {
            includes.add( new ArtifactId( projectArtifact ) );
        }
    }

    private static Collection<ArtifactId> toIds( final Collection<String> patterns )
    {
        final Collection<ArtifactId> result = new HashSet<ArtifactId>();

        if ( patterns != null )
        {
            for ( final String pattern : patterns )
            {
                result.add( new ArtifactId( pattern ) );
            }
        }

        return result;
    }

    public boolean isSelected( final Artifact artifact )
    {
        return ( artifact != null ) ? isSelected( new ArtifactId( artifact ) ) : false;
    }

    boolean isSelected( final ArtifactId id )
    {
        return ( includes == null && excludes == null )
            || ( ( includes.isEmpty() || matches( includes, id ) ) && !matches( excludes, id ) );
    }

    private boolean matches( final Collection<ArtifactId> ids, final ArtifactId id )
    {
        if ( ids != null && !ids.isEmpty() )
        {
            for ( final ArtifactId artifactId : ids )
            {
                if ( id.matches( artifactId ) )
                {
                    return true;
                }
            }
            return false;
        }
        else
        {
            return true;
        }
    }

}
