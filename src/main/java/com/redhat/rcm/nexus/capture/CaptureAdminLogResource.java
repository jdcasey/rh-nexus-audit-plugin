package com.redhat.rcm.nexus.capture;

import static com.redhat.rcm.nexus.capture.CaptureLogUtils.deleteLogs;
import static com.redhat.rcm.nexus.capture.CaptureLogUtils.queryLogs;
import static com.redhat.rcm.nexus.capture.CaptureLogUtils.setBeforeDate;
import static com.redhat.rcm.nexus.capture.CaptureLogUtils.setSinceDate;
import static com.redhat.rcm.nexus.capture.model.serialize.SerializationUtils.getGson;
import static com.redhat.rcm.nexus.capture.model.serialize.SerializationUtils.getXStreamForREST;
import static com.redhat.rcm.nexus.capture.request.RequestUtils.mediaTypeOf;
import static com.redhat.rcm.nexus.capture.request.RequestUtils.modeOf;
import static com.redhat.rcm.nexus.capture.request.RequestUtils.query;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

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

import com.redhat.rcm.nexus.capture.model.CaptureSessionRef;
import com.redhat.rcm.nexus.capture.model.render.CaptureSessionResource;
import com.redhat.rcm.nexus.capture.request.RequestMode;
import com.redhat.rcm.nexus.capture.store.CaptureSessionQuery;
import com.redhat.rcm.nexus.capture.store.CaptureStore;
import com.redhat.rcm.nexus.capture.store.CaptureStoreException;

@Named( "captureAdminLog" )
public class CaptureAdminLogResource
    extends AbstractNexusPlexusResource
    implements PlexusResource
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    @Named( "json" )
    private CaptureStore captureStore;

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
        return new PathProtectionDescriptor( "/capture/admin/logs",
                                             String.format( "authcBasic,perms[%s]",
                                                            CaptureResourceConstants.PRIV_LOG_ACCESS ) );
    }

    @Override
    public String getResourceUri()
    {
        return "/capture/admin/logs";
    }

    @Override
    public Object get( final Context context, final Request request, final Response response, final Variant variant )
        throws ResourceException
    {
        final Form requestQuery = query( request );

        final String buildTag = requestQuery.getFirstValue( CaptureResourceConstants.PARAM_BUILD_TAG );
        final String user = requestQuery.getFirstValue( CaptureResourceConstants.PARAM_USER );

        final CaptureSessionQuery query = new CaptureSessionQuery().setUser( user ).setBuildTag( buildTag );

        setBeforeDate( query, request );
        setSinceDate( query, request );

        Object data = null;

        final RequestMode mode = modeOf( request );
        if ( mode != null )
        {
            if ( mode == RequestMode.TABLE_OF_CONTENTS )
            {
                try
                {

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
                final List<CaptureSessionRef> logs = captureStore.getLogs( query );
                if ( logs != null )
                {
                    final String appUrl = request.getRootRef().toString();

                    final List<CaptureSessionResource> resources = new ArrayList<CaptureSessionResource>( logs.size() );
                    for ( final CaptureSessionRef ref : logs )
                    {
                        resources.add( new CaptureSessionResource( captureStore.readLog( ref ), appUrl ) );
                    }

                    data = resources;
                }
            }
            catch ( final CaptureStoreException e )
            {
                logger.error( "Failed to retrieve capture log(s). Error: {}\nMessage: {}", e.getClass().getName(),
                              e.getMessage() );
                e.printStackTrace();

                throw new ResourceException( Status.SERVER_ERROR_INTERNAL, e.getMessage() );
            }
        }

        String result = null;

        final MediaType mt = mediaTypeOf( request, variant );
        if ( mt == MediaType.APPLICATION_XML )
        {
            result = getXStreamForREST().toXML( data );
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
        final String user = requestQuery.getFirstValue( CaptureResourceConstants.PARAM_USER );

        deleteLogs( captureStore, user, buildTag, request );
    }
}