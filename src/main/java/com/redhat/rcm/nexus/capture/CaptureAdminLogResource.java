package com.redhat.rcm.nexus.capture;

import static com.redhat.rcm.nexus.capture.request.RequestUtils.mediaTypeOf;
import static com.redhat.rcm.nexus.capture.request.RequestUtils.modeOf;
import static com.redhat.rcm.nexus.capture.request.RequestUtils.parseUrlDate;
import static com.redhat.rcm.nexus.capture.request.RequestUtils.query;
import static com.redhat.rcm.nexus.capture.serialize.SerializationUtils.getGson;
import static com.redhat.rcm.nexus.capture.serialize.SerializationUtils.getXStream;

import java.text.ParseException;
import java.util.Date;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.restlet.Context;
import org.restlet.data.Form;
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

import com.redhat.rcm.nexus.capture.model.CaptureSessionQuery;
import com.redhat.rcm.nexus.capture.request.RequestMode;
import com.redhat.rcm.nexus.capture.store.CaptureStore;
import com.redhat.rcm.nexus.capture.store.CaptureStoreException;

@Component( role = PlexusResource.class, hint = "CaptureAdminLogResource" )
public class CaptureAdminLogResource
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

    public CaptureAdminLogResource()
    {
        this.setModifiable( true );
    }

    @Override
    public Object getPayloadInstance()
    {
        // if you allow PUT or POST you would need to return your object.
        return null;
    }

    @Override
    public PathProtectionDescriptor getResourceProtection()
    {
        return new PathProtectionDescriptor( "/capture/admin/log",
                                             String.format( "authcBasic,perms[%s]",
                                                            CaptureResourceConstants.PRIV_LOG_ACCESS ) );
    }

    @Override
    public String getResourceUri()
    {
        return "/capture/admin/log";
    }

    @Override
    public Object get( final Context context, final Request request, final Response response, final Variant variant )
        throws ResourceException
    {
        final Form requestQuery = query( request );

        final String buildTag = requestQuery.getFirstValue( CaptureResourceConstants.PARAM_BUILD_TAG );
        final String captureSource = requestQuery.getFirstValue( CaptureResourceConstants.PARAM_CAPTURE_SOURCE );
        final String user = requestQuery.getFirstValue( CaptureResourceConstants.PARAM_USER );

        final CaptureSessionQuery query =
            new CaptureSessionQuery().setUser( user ).setBuildTag( buildTag ).setCaptureSource( captureSource );

        Object data = null;

        final RequestMode mode = modeOf( request );
        if ( mode != null )
        {
            if ( mode == RequestMode.TABLE_OF_CONTENTS )
            {
                try
                {

                    data = captureStore.getLogs( query, request.getRootRef().toString() );
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
            // TODO: Replace this with reading a list of all full sessions that match query.
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
    public void delete( final Context context, final Request request, final Response response )
        throws ResourceException
    {
        final Form requestQuery = query( request );

        final String buildTag = requestQuery.getFirstValue( CaptureResourceConstants.PARAM_BUILD_TAG );
        final String captureSource = requestQuery.getFirstValue( CaptureResourceConstants.PARAM_CAPTURE_SOURCE );
        final String user = requestQuery.getFirstValue( CaptureResourceConstants.PARAM_USER );

        Date before = null;
        try
        {
            final String value = query( request ).getFirstValue( CaptureResourceConstants.PARAM_BEFORE );
            before = parseUrlDate( value );
        }
        catch ( final ParseException e )
        {
            final String message =
                String.format( "Invalid date format in %s parameter. Error: %s\nMessage: %s",
                               CaptureResourceConstants.PARAM_BEFORE, e.getClass().getName(), e.getMessage() );

            logger.error( message, e );

            e.printStackTrace();

            throw new ResourceException( Status.CLIENT_ERROR_BAD_REQUEST, message );
        }

        final CaptureSessionQuery query =
            new CaptureSessionQuery().setUser( user ).setBuildTag( buildTag ).setCaptureSource( captureSource );

        if ( before != null )
        {
            query.setBefore( before );
        }

        try
        {
            captureStore.deleteLogs( query );
        }
        catch ( final CaptureStoreException e )
        {
            final String message =
                String.format( "Failed to expire capture log(s) for query:\n%s\nError: %s\nMessage: %s", query,
                               e.getClass().getName(), e.getMessage() );

            logger.error( message, e );

            e.printStackTrace();

            throw new ResourceException( Status.SERVER_ERROR_INTERNAL, message );
        }
    }
}