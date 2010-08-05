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

package com.redhat.tools.nexus.audit.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.proxy.NoSuchRepositoryException;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.repository.ProxyRepository;

import com.google.gson.annotations.SerializedName;
import com.redhat.tools.nexus.audit.protocol.AuditInfoResponse;
import com.redhat.tools.nexus.audit.serial.SerialConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Date;

@XStreamAlias( SerialConstants.AUDIT_INFO_ROOT )
public class AuditInfo
{

    private static final Logger logger = LoggerFactory.getLogger( AuditInfo.class );

    @SerializedName( SerialConstants.AUDIT_INFO_OWNER )
    @XStreamAlias( SerialConstants.AUDIT_INFO_OWNER )
    private final String owner;

    @SerializedName( SerialConstants.AUDIT_INFO_CAPTURE_DATE )
    @XStreamAlias( SerialConstants.AUDIT_INFO_CAPTURE_DATE )
    private final Date dateAdded;

    @SerializedName( SerialConstants.AUDIT_INFO_REFERENCED_PATH )
    @XStreamAlias( SerialConstants.AUDIT_INFO_REFERENCED_PATH )
    private final String referencedPath;

    @SerializedName( SerialConstants.AUDIT_INFO_REPO_ID )
    @XStreamAlias( SerialConstants.AUDIT_INFO_REPO_ID )
    private final String repositoryId;

    // For gson deserialize
    @SuppressWarnings( "unused" )
    private AuditInfo()
    {
        owner = null;
        dateAdded = null;
        referencedPath = null;
        repositoryId = null;
    }

    public AuditInfo( final String owner, final Date dateAdded, final String referencedPath, final String repositoryId )
    {
        this.owner = owner;
        this.dateAdded = dateAdded;
        this.referencedPath = referencedPath;
        this.repositoryId = repositoryId;
    }

    public String getOwner()
    {
        return owner;
    }

    public Date getDateAdded()
    {
        return dateAdded;
    }

    public String getReferencedPath()
    {
        return referencedPath;
    }

    public String getRepositoryId()
    {
        return repositoryId;
    }

    public AuditInfoResponse asResource( final String appUrl, final RepositoryRegistry repositoryRegistry )
    {
        String remoteBase = null;
        try
        {
            final ProxyRepository repository =
                repositoryRegistry.getRepositoryWithFacet( repositoryId, ProxyRepository.class );

            remoteBase = repository.getRemoteUrl();
        }
        catch ( final NoSuchRepositoryException e )
        {
            logger.warn( String.format(
                                        "Cannot find proxy repository for target. Target may be resolved from a hosted repository."
                                            + "\nRepository ID: %s\nTarget path: %s", repositoryId, referencedPath ) );
        }

        return new AuditInfoResponse( this, appUrl, remoteBase );
    }

}
