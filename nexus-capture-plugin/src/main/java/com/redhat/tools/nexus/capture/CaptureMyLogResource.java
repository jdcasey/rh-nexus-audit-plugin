package com.redhat.tools.nexus.capture;

import static com.redhat.tools.nexus.capture.CaptureLogUtils.deleteLogs;
import static com.redhat.tools.nexus.capture.CaptureLogUtils.queryLogs;
import static com.redhat.tools.nexus.request.RequestUtils.mediaTypeOf;
import static com.redhat.tools.nexus.request.RequestUtils.modeOf;
import static com.redhat.tools.nexus.request.RequestUtils.query;
import static org.codehaus.plexus.util.StringUtils.isEmpty;

import org.jsecurity.SecurityUtils;
import org.jsecurity.subject.Subject;
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
import org.sonatype.nexus.rest.AbstractNexusPlexusResource;
import org.sonatype.plexus.rest.resource.PathProtectionDescriptor;
import org.sonatype.plexus.rest.resource.PlexusResource;

import com.redhat.tools.nexus.capture.model.CaptureSession;
import com.redhat.tools.nexus.capture.model.CaptureSessionRef;
import com.redhat.tools.nexus.capture.store.CaptureSessionQuery;
import com.redhat.tools.nexus.capture.store.CaptureStore;
import com.redhat.tools.nexus.capture.store.CaptureStoreException;
import com.redhat.tools.nexus.protocol.CaptureSessionRefResource;
import com.redhat.tools.nexus.protocol.CaptureSessionResource;
import com.redhat.tools.nexus.protocol.ProtocolConstants;
import com.redhat.tools.nexus.request.RequestMode;
import com.redhat.tools.nexus.response.WebResponseSerializer;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named( "captureMyLog" )
public class CaptureMyLogResource
    extends AbstractNexusPlexusResource
    implements PlexusResource
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    @Named( "json" )
    private CaptureStore captureStore;

    @Inject
    @Named( "capture" )
    private WebResponseSerializer responseSerializer;

    public CaptureMyLogResource()
    {
        setModifiable( true );
    }

    @Override
    public Object getPayloadInstance()
    {
        return CaptureSessionRefResource.payloadPrototype();
    }

    @Override
    public PathProtectionDescriptor getResourceProtection()
    {
        return new PathProtectionDescriptor( ProtocolConstants.MY_LOGS_RESOURCE_FRAGMENT + "/*",
                                             String.format( "authcBasic,perms[%s]",
                                                            CaptureResourceConstants.PRIV_LOG_ACCESS ) );
    }

    @Override
    public String getResourceUri()
    {
        return ProtocolConstants.MY_LOGS_RESOURCE_FRAGMENT + "/{" + CaptureResourceConstants.ATTR_BUILD_TAG_REPO_ID
            + "}";
    }

    @Override
    public Object get( final Context context, final Request request, final Response response, final Variant variant )
        throws ResourceException
    {
        final String buildTag =
            request.getAttributes().get( CaptureResourceConstants.ATTR_BUILD_TAG_REPO_ID ).toString();

        final Subject subject = SecurityUtils.getSubject();
        final String user = subject.getPrincipal().toString();

        Object data = null;

        final RequestMode mode = modeOf( request );
        if ( mode != null )
        {
            if ( mode == RequestMode.TABLE_OF_CONTENTS )
            {
                try
                {
                    final CaptureSessionQuery query = new CaptureSessionQuery().setUser( user ).setBuildTag( buildTag );

                    data = queryLogs( captureStore, query, request.getRootRef().toString() );
                }
                catch ( final CaptureStoreException e )
                {
                    logger.error( "Failed to retrieve capture-log listing. Error: {}\nMessage: {}", e.getClass()
                                                                                                     .getName(),
                                  e.getMessage() );
                    e.printStackTrace();

                    throw new ResourceException( Status.SERVER_ERROR_INTERNAL, e.getMessage() );
                }
            }
        }

        if ( data == null )
        {
            try
            {
                final CaptureSession session = captureStore.readLatestLog( user, buildTag );
                if ( session != null )
                {
                    data = session.asResource( request.getRootRef().toString(), getRepositoryRegistry() );
                }
            }
            catch ( final CaptureStoreException e )
            {
                logger.error( "Failed to retrieve capture log. Error: {}\nMessage: {}", e.getClass().getName(),
                              e.getMessage() );
                e.printStackTrace();

                throw new ResourceException( Status.SERVER_ERROR_INTERNAL, e.getMessage() );
            }
        }

        final MediaType mt = mediaTypeOf( request, variant );
        return responseSerializer.serialize( data, mt, request, CaptureLogResource.LOG_TEMPLATE_BASEPATH );
    }

    @Override
    public Object post( final Context context, final Request request, final Response response, final Object payload )
        throws ResourceException
    {
        final String buildTag =
            request.getAttributes().get( CaptureResourceConstants.ATTR_BUILD_TAG_REPO_ID ).toString();

        final Subject subject = SecurityUtils.getSubject();
        final String user = subject.getPrincipal().toString();

        CaptureSessionResource resource = null;
        try
        {
            final CaptureSessionRef ref = captureStore.closeCurrentLog( user, buildTag );
            CaptureSession session = null;
            if ( ref != null )
            {
                session = captureStore.readLog( ref );
            }

            if ( session == null )
            {
                final Form query = query( request );
                final String strict = query.getFirstValue( CaptureResourceConstants.PARAM_STRICT );
                if ( isEmpty( strict ) || Boolean.FALSE.toString().equals( strict.toLowerCase() ) )
                {
                    session = captureStore.readLatestLog( user, buildTag );
                }
            }

            resource =
                session == null ? null : session.asResource( request.getRootRef().toString(), getRepositoryRegistry() );
        }
        catch ( final CaptureStoreException e )
        {
            logger.error( "Failed to close current capture log. Error: {}\nMessage: {}", e.getClass().getName(),
                          e.getMessage() );

            e.printStackTrace();

            throw new ResourceException( Status.SERVER_ERROR_INTERNAL, e.getMessage() );
        }

        final MediaType mt = mediaTypeOf( request );
        return responseSerializer.serialize( resource, mt, request, CaptureLogResource.LOG_TEMPLATE_BASEPATH );
    }

    @Override
    public void delete( final Context context, final Request request, final Response response )
        throws ResourceException
    {
        final String buildTag =
            request.getAttributes().get( CaptureResourceConstants.ATTR_BUILD_TAG_REPO_ID ).toString();

        final Subject subject = SecurityUtils.getSubject();
        final String user = subject.getPrincipal().toString();

        deleteLogs( captureStore, user, buildTag, request );
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