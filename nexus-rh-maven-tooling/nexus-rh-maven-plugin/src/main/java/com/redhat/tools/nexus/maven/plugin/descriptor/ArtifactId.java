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
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.SelectorUtils;

class ArtifactId
{
    private final String groupId;

    private final String artifactId;

    private final String type;

    private final String classifier;

    public ArtifactId( final Dependency dependency )
    {
        this( dependency.getGroupId(), dependency.getArtifactId(), dependency.getType(), dependency.getClassifier() );
    }

    public ArtifactId( final Artifact artifact )
    {
        this( artifact.getGroupId(), artifact.getArtifactId(), artifact.getType(), artifact.getClassifier() );
    }

    public ArtifactId( final String groupId, final String artifactId, final String type, final String classifier )
    {
        this.groupId = ( groupId != null ) ? groupId : "";
        this.artifactId = ( artifactId != null ) ? artifactId : "";
        this.type = ( type != null ) ? type : "";
        this.classifier = ( classifier != null ) ? classifier : "";
    }

    public ArtifactId( final String id )
    {
        String[] tokens = new String[0];
        if ( id != null && id.length() > 0 )
        {
            tokens = id.split( ":", -1 );
        }
        groupId = ( tokens.length > 0 ) ? tokens[0] : "";
        artifactId = ( tokens.length > 1 ) ? tokens[1] : "*";
        type = ( tokens.length > 3 ) ? tokens[2] : "*";
        classifier = ( tokens.length > 3 ) ? tokens[3] : ( ( tokens.length > 2 ) ? tokens[2] : "*" );
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getType()
    {
        return type;
    }

    public String getClassifier()
    {
        return classifier;
    }

    public boolean matches( final ArtifactId pattern )
    {
        if ( pattern == null )
        {
            return false;
        }
        if ( !match( getGroupId(), pattern.getGroupId() ) )
        {
            return false;
        }
        if ( !match( getArtifactId(), pattern.getArtifactId() ) )
        {
            return false;
        }
        if ( !match( getType(), pattern.getType() ) )
        {
            return false;
        }
        if ( !match( getClassifier(), pattern.getClassifier() ) )
        {
            return false;
        }

        return true;
    }

    private boolean match( final String str, final String pattern )
    {
        return SelectorUtils.match( pattern, str );
    }

    @Override
    public String toString()
    {
        return "ArtifactId [artifactId=" + artifactId + ", classifier=" + classifier + ", groupId=" + groupId
            + ", type=" + type + "]";
    }

}
