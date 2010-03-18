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

package com.redhat.tools.nexus.audit.serial.store;

import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.proxy.NoSuchRepositoryException;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.StorageException;
import org.sonatype.nexus.proxy.maven.ArtifactStoreRequest;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.repository.Repository;

import com.redhat.tools.nexus.audit.model.AuditInfo;

import javax.inject.Inject;
import javax.inject.Named;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public abstract class AbstractAuditStore
    implements AuditStore
{

    @Inject
    @Named( "protected" )
    private RepositoryRegistry repoRegistry;

    protected RepositoryRegistry getRepositoryRegistry()
    {
        return repoRegistry;
    }

    protected File getStoreFile( final AuditInfo info )
        throws AuditStoreException
    {
        final MavenRepository repo = getRepository( info.getRepositoryId() );
        return getStoreFile( repo, new ResourceStoreRequest( info.getReferencedPath(), false ) );
    }

    protected File getStoreFile( final String repoId, final Gav gav )
        throws AuditStoreException
    {
        final MavenRepository repo = getRepository( repoId );
        return getStoreFile( repo, new ArtifactStoreRequest( repo, gav, false ) );
    }

    protected File getStoreFile( final String repoId, final String path )
        throws AuditStoreException
    {
        final MavenRepository repo = getRepository( repoId );
        return getStoreFile( repo, new ResourceStoreRequest( path, false ) );
    }

    protected MavenRepository getRepository( final String repoId )
        throws AuditStoreException
    {
        try
        {
            return repoRegistry.getRepositoryWithFacet( repoId, MavenRepository.class );
        }
        catch ( final NoSuchRepositoryException e )
        {
            throw new AuditStoreException( "Failed to retrieve maven repository: %s\nReason: %s", e, repoId,
                                           e.getMessage() );
        }
    }

    protected File getStoreFile( final Repository repository, final ResourceStoreRequest request )
        throws AuditStoreException
    {
        URL url;
        try
        {
            url = repository.getLocalStorage().getAbsoluteUrlFromBase( repository, request );
        }
        catch ( final StorageException e )
        {
            throw new AuditStoreException(
                                           "Failed to retrieve local-storage URL.\nPath: %s\nRepository-Id: %s\nReason: %s",
                                           e, request.getRequestPath(), repository.getId(), e.getMessage() );
        }

        File file;

        try
        {
            file = new File( url.toURI() );
        }
        catch ( final Throwable t )
        {
            file = new File( url.getPath() );
        }

        String artifactPath;
        try
        {
            artifactPath = file.getCanonicalPath();
        }
        catch ( final IOException e )
        {
            artifactPath = file.getAbsolutePath();
        }

        return new File( artifactPath + ".audit." + getAuditFileExtension() );
    }

    protected abstract String getAuditFileExtension();

}
