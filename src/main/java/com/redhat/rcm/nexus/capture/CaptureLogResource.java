package com.redhat.rcm.nexus.capture;

import static com.redhat.rcm.nexus.capture.request.RequestUtils.mediaTypeOf;
import static com.redhat.rcm.nexus.capture.request.RequestUtils.modeOf;
import static com.redhat.rcm.nexus.capture.serialize.CaptureSerializationUtils.getGson;
import static com.redhat.rcm.nexus.capture.serialize.CaptureSerializationUtils.getXStream;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.jsecurity.SecurityUtils;
import org.jsecurity.subject.Subject;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.rest.AbstractNexusPlexusResource;
import org.sonatype.plexus.rest.resource.PathProtectionDescriptor;
import org.sonatype.plexus.rest.resource.PlexusResource;

import com.redhat.rcm.nexus.capture.request.RequestMode;
import com.redhat.rcm.nexus.capture.serialize.CaptureStore;
import com.redhat.rcm.nexus.capture.serialize.CaptureStoreException;

@Component( role = PlexusResource.class, hint = "CaptureEmailLogResource" )
public class CaptureLogResource
    extends AbstractNexusPlexusResource
    implements PlexusResource
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Requirement( hint = "json" )
    private CaptureStore captureStore;

    // @Requirement
    // private SecuritySystem securitySystem;

    // @Requirement
    // private NexusEmailer emailer;

    @Override
    public Object getPayloadInstance()
    {
        // if you allow PUT or POST you would need to return your object.
        return null;
    }

    @Override
    public PathProtectionDescriptor getResourceProtection()
    {
        return new PathProtectionDescriptor( "/capture/*/*/log",
                                             String.format( "authcBasic,perms[%s]",
                                                            CaptureResourceConstants.CAPTURE_PERMISSION ) );
    }

    @Override
    public String getResourceUri()
    {
        return "/capture/{" + CaptureResourceConstants.BUILD_TAG_REPO_ID_KEY + "}/{"
                        + CaptureResourceConstants.CAPTURE_SOURCE_REPO_ID_KEY + "}/log";
    }

    @Override
    public Object get( final Context context, final Request request, final Response response, final Variant variant )
        throws ResourceException
    {
        final String buildTag =
            request.getAttributes().get( CaptureResourceConstants.BUILD_TAG_REPO_ID_KEY ).toString();

        final String captureSource =
            request.getAttributes().get( CaptureResourceConstants.CAPTURE_SOURCE_REPO_ID_KEY ).toString();

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
                    data = captureStore.getLogs( user, buildTag, captureSource );
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
                data = captureStore.readLatestLog( user, buildTag, captureSource );
            }
            catch ( final CaptureStoreException e )
            {
                logger.error( "Failed to retrieve capture log. Error: {}\nMessage: {}", e.getClass().getName(),
                              e.getMessage() );
                e.printStackTrace();

                throw new ResourceException( Status.SERVER_ERROR_INTERNAL, e.getMessage() );
            }
        }

        String result = null;

        final MediaType mt = mediaTypeOf( request, variant );
        if ( mt == MediaType.APPLICATION_XML )
        {
            result = getXStream().toXML( data );
        }
        else if ( mt == MediaType.APPLICATION_JSON )
        {
            result = getGson().toJson( data );
        }
        else
        {
            throw new ResourceException( Status.CLIENT_ERROR_NOT_ACCEPTABLE );
        }

        return new StringRepresentation( result, mt );
    }

    @Override
    public Object post( final Context context, final Request request, final Response response, final Object payload )
        throws ResourceException
    {
        throw new ResourceException( Status.CLIENT_ERROR_METHOD_NOT_ALLOWED );
    }

    @Override
    public Object put( final Context context, final Request request, final Response response, final Object payload )
        throws ResourceException
    {
        throw new ResourceException( Status.CLIENT_ERROR_METHOD_NOT_ALLOWED );
    }

    @Override
    public void delete( final Context context, final Request request, final Response response )
        throws ResourceException
    {
        throw new ResourceException( Status.CLIENT_ERROR_METHOD_NOT_ALLOWED );
    }
}