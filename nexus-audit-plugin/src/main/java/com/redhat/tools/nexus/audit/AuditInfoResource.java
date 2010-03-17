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

import static com.redhat.tools.nexus.request.RequestUtils.mediaTypeOf;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.configuration.application.ApplicationConfiguration;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.plexus.rest.resource.PathProtectionDescriptor;
import org.sonatype.plexus.rest.resource.PlexusResource;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.redhat.tools.nexus.audit.model.AuditInfo;
import com.redhat.tools.nexus.audit.serial.store.AuditStore;
import com.redhat.tools.nexus.audit.serial.store.AuditStoreException;
import com.redhat.tools.nexus.guice.PluginPrivateInjection;
import com.redhat.tools.nexus.nx.AbstractNonResolverResource;
import com.redhat.tools.nexus.response.WebResponseSerializer;

import java.util.ArrayList;
import java.util.List;

@javax.inject.Named( "auditInfoResource" )
public class AuditInfoResource
    extends AbstractNonResolverResource
    implements PlexusResource
{

    public static final String REPO_ID = "repositoryId";

    private static final String AUDIT_TEMPLATE_BASEPATH = "audit/log";

    private static final Logger logger = LoggerFactory.getLogger( AuditInfoResource.class );

    @javax.inject.Inject
    private ApplicationConfiguration applicationConfiguration;

    @javax.inject.Inject
    @javax.inject.Named( "protected" )
    private RepositoryRegistry repositoryRegistry;

    @Inject
    @Named( AuditConstants.PREFERRED_STORE )
    private AuditStore store;

    @Inject
    @Named( "audit" )
    private WebResponseSerializer responseSerializer;

    @Override
    public Object getPayloadInstance()
    {
        return null;
    }

    @Override
    public PathProtectionDescriptor getResourceProtection()
    {
        //        return new PathProtectionDescriptor( "/rh-audit/log/*/**", "authcBasic,perms[nexus:rh-audit-log]" );
        return new PathProtectionDescriptor( "/audit/log/*/**", "authcBasic" );
    }

    @Override
    public String getResourceUri()
    {
        return "/audit/log/{" + REPO_ID + "}";
    }

    @Override
    public Object get( final Context context, final Request request, final Response response, final Variant variant )
        throws ResourceException
    {
        init();

        final Object rid = request.getAttributes().get( REPO_ID );
        logger.info( String.format( "%s attribute value: %s", REPO_ID, rid ) );

        final String repoId = rid == null ? null : rid.toString();
        if ( repoId == null || repoId.trim().length() < 1 )
        {
            throw new ResourceException( Status.CLIENT_ERROR_BAD_REQUEST,
                                         "You must include a repository-id and target-path to retrieve its audit log." );
        }

        final String path = request.getResourceRef().getRemainingPart();

        if ( path == null || path.trim().length() < 1 )
        {
            throw new ResourceException( Status.CLIENT_ERROR_BAD_REQUEST,
                                         "You must include a target-path to retrieve its audit log." );
        }

        logger.info( String.format( "Looking up audit information:\nRepository: %s\nTarget Path: %s", repoId, path ) );

        Object data = null;
        try
        {
            final AuditInfo auditInfo = store.getAuditInformation( path, repoId );
            if ( auditInfo != null )
            {
                data = auditInfo.asResource( request.getRootRef().toString(), getRepoRegistry() );
            }
        }
        catch ( final AuditStoreException e )
        {
            final String message =
                String.format( "Failed to retrieve audit log. Error: %s\nMessage: %s", e.getClass().getName(),
                               e.getMessage() );

            logger.error( message, e );
            e.printStackTrace();

            throw new ResourceException( Status.SERVER_ERROR_INTERNAL, message );
        }

        logger.info( String.format( "Responding with: %s", data ) );
        final MediaType mt = mediaTypeOf( request, variant );
        return responseSerializer.serialize( data, mt, request, AUDIT_TEMPLATE_BASEPATH );
    }

    private void init()
    {
        if ( responseSerializer == null )
        {
            PluginPrivateInjection.getInjector().injectMembers( this );
        }
    }

    @Override
    public List<Variant> getVariants()
    {
        final List<Variant> variants = new ArrayList<Variant>();

        variants.add( new Variant( MediaType.APPLICATION_XML ) );
        variants.add( new Variant( MediaType.APPLICATION_JSON ) );
        variants.add( new Variant( MediaType.TEXT_PLAIN ) );

        return variants;
    }

    @Override
    public ApplicationConfiguration getApplicationConfiguration()
    {
        return applicationConfiguration;
    }

    @Override
    public RepositoryRegistry getRepoRegistry()
    {
        return repositoryRegistry;
    }
}
