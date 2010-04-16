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

import static com.redhat.tools.nexus.audit.serial.SerialUtils.getGson;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.artifact.Gav;

import com.redhat.tools.nexus.audit.model.AuditInfo;

import javax.inject.Named;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Named( "json" )
public class JsonAuditStore
    extends AbstractAuditStore
    implements AuditStore
{

    private static final Logger logger = LoggerFactory.getLogger( JsonAuditStore.class );

    @Override
    public AuditInfo getAuditInformation( final String path, final String repoId )
        throws AuditStoreException
    {
        final File auditFile = getStoreFile( repoId, path );
        return getAuditInformation( auditFile );
    }

    @Override
    public AuditInfo getAuditInformation( final Gav gav, final String repoId )
        throws AuditStoreException
    {
        final File auditFile = getStoreFile( repoId, gav );
        return getAuditInformation( auditFile );
    }

    @Override
    public boolean saveAuditInformation( final AuditInfo auditInfo )
        throws AuditStoreException
    {
        final File auditFile = getStoreFile( auditInfo );

        if ( logger.isDebugEnabled() )
        {
            logger.debug( String.format( "Attempting to write audit info for: %s\nAudit File: %s",
                                         auditInfo.getReferencedPath(), auditFile ) );
        }

        final File dir = auditFile.getParentFile();
        if ( dir != null && !dir.exists() )
        {
            if ( !dir.mkdirs() )
            {
                throw new AuditStoreException( "Failed to create parent directory structure for audit file: %s",
                                               auditFile );
            }
        }

        FileWriter writer = null;
        try
        {
            writer = new FileWriter( auditFile );
            getGson().toJson( auditInfo, writer );

            return true;
        }
        catch ( final IOException e )
        {
            throw new AuditStoreException( "Failed to write audit file: %s\nReason: %s", e, auditFile, e.getMessage() );
        }
        finally
        {
            IOUtils.closeQuietly( writer );
        }
    }

    @Override
    protected String getAuditFileExtension()
    {
        return "json";
    }

    private AuditInfo getAuditInformation( final File auditFile )
        throws AuditStoreException
    {
        if ( auditFile.exists() && auditFile.isFile() && auditFile.canRead() )
        {
            FileReader reader = null;
            try
            {
                reader = new FileReader( auditFile );
                return getGson().fromJson( reader, AuditInfo.class );
            }
            catch ( final FileNotFoundException e )
            {
                throw new AuditStoreException( "Cannot open audit file: %s\nReason: %s", e, auditFile, e.getMessage() );
            }
            finally
            {
                IOUtils.closeQuietly( reader );
            }
        }

        return null;
    }

}
