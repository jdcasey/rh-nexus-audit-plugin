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

package com.redhat.tools.nexus.audit;

import static com.redhat.tools.nexus.request.RequestUtils.mediaTypeOf;
import static com.redhat.tools.nexus.request.RequestUtils.query;
import static com.redhat.tools.nexus.request.RequestUtils.requestAttribute;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.artifact.IllegalArtifactCoordinateException;
import org.sonatype.nexus.rest.AbstractNexusPlexusResource;
import org.sonatype.plexus.rest.resource.PathProtectionDescriptor;
import org.sonatype.plexus.rest.resource.PlexusResource;

import com.redhat.tools.nexus.audit.model.AuditInfo;
import com.redhat.tools.nexus.audit.protocol.AuditInfoResponse;
import com.redhat.tools.nexus.audit.serial.store.AuditStore;
import com.redhat.tools.nexus.audit.serial.store.AuditStoreException;
import com.redhat.tools.nexus.response.WebResponseSerializer;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named( "auditInfoByGAVResource" )
public class AuditInfoByGAVResource
    extends AbstractNexusPlexusResource
    implements PlexusResource
{

    private static final Logger logger = LoggerFactory.getLogger( AuditInfoByGAVResource.class );

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
        return new PathProtectionDescriptor( "/audit/log/*/gav/*/*/*", String.format( "authcBasic,perms[%s]",
                                                                                      AuditConstants.PRIV_AUDIT_ACCESS ) );
    }

    @Override
    public String getResourceUri()
    {
        return "/audit/log/{" + AuditConstants.REPO_ID + "}/gav/{" + AuditConstants.GROUP_ID + "}/{"
            + AuditConstants.ARTIFACT_ID + "}/{" + AuditConstants.VERSION + "}";
    }

    @Override
    public Object get( final Context context, final Request request, final Response response, final Variant variant )
        throws ResourceException
    {
        final String repoId = requestAttribute( AuditConstants.REPO_ID, request );
        final String groupId = requestAttribute( AuditConstants.GROUP_ID, request );
        final String artifactId = requestAttribute( AuditConstants.ARTIFACT_ID, request );
        final String version = requestAttribute( AuditConstants.VERSION, request );

        final Form query = query( request );
        final String classifier = query.getFirstValue( AuditConstants.PARAM_CLASSIFIER );
        final String extension = query.getFirstValue( AuditConstants.PARAM_EXTENSION, "jar" );

        final String quiet = query.getFirstValue( AuditConstants.PARAM_QUIET, "false" );

        Object data = null;
        try
        {
            final Gav gav =
                new Gav( groupId, artifactId, version, classifier, extension, null, null, null, false, false, null,
                         false, null );

            final AuditInfo info = store.getAuditInformation( gav, repoId );
            if ( info != null )
            {
                data = info.asResource( request.getRootRef().toString(), getRepositoryRegistry() );
            }
        }
        catch ( final IllegalArtifactCoordinateException e )
        {
            final String message =
                String.format( "Invalid artifact coordinate. Error: %s\nGroup-Id: %s\nArtifact-Id: %s"
                    + "\nVersion: %s\nClassifier: %s\nExtension: %s", e.getMessage(), groupId, artifactId, version,
                               classifier, extension );

            logger.error( message, e );
            e.printStackTrace();

            throw new ResourceException( Status.CLIENT_ERROR_BAD_REQUEST, message );
        }
        catch ( final AuditStoreException e )
        {
            final String message =
                String.format( "Failed to retrieve audit log. Error: %s\nMessage: %s", e.getClass().getName(),
                               e.getMessage() );

            logger.error( message, e );
            e.printStackTrace();

            throw new ResourceException( Status.SERVER_ERROR_INTERNAL, "<pre>" + message + "</pre>" );
        }

        if ( data == null && Boolean.valueOf( quiet ) )
        {
            data = AuditInfoResponse.getUnknown( repoId );
        }

        final MediaType mt = mediaTypeOf( request, variant );
        return responseSerializer.serialize( data, mt, request, AuditConstants.AUDIT_TEMPLATE_BASEPATH );
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
}
