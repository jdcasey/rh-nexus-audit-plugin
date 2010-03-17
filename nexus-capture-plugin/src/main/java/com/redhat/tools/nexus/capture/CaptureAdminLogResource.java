package com.redhat.tools.nexus.capture;

import static com.redhat.tools.nexus.capture.CaptureLogUtils.deleteLogs;
import static com.redhat.tools.nexus.capture.CaptureLogUtils.queryLogs;
import static com.redhat.tools.nexus.capture.CaptureLogUtils.setBeforeDate;
import static com.redhat.tools.nexus.capture.CaptureLogUtils.setSinceDate;
import static com.redhat.tools.nexus.request.RequestUtils.mediaTypeOf;
import static com.redhat.tools.nexus.request.RequestUtils.modeOf;
import static com.redhat.tools.nexus.request.RequestUtils.query;

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
import com.redhat.tools.nexus.guice.PluginPrivateInjection;
import com.redhat.tools.nexus.protocol.CaptureSessionResource;
import com.redhat.tools.nexus.protocol.ProtocolConstants;
import com.redhat.tools.nexus.request.RequestMode;
import com.redhat.tools.nexus.response.WebResponseSerializer;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named( "captureAdminLog" )
public class CaptureAdminLogResource
    extends AbstractNexusPlexusResource
    implements PlexusResource
{

    private static final String LOG_ADMIN_TEMPLATE_BASEPATH = "capture/admin/logs";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    @Named( "json" )
    private CaptureStore captureStore;

    @com.google.inject.Inject
    @com.google.inject.name.Named( "capture" )
    private WebResponseSerializer responseSerializer;

    public CaptureAdminLogResource()
    {
        setModifiable( true );
        PluginPrivateInjection.getInjector().injectMembers( this );
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
        return new PathProtectionDescriptor( ProtocolConstants.ADMIN_LOGS_RESOURCE_FRAGMENT,
                                             String.format( "authcBasic,perms[%s]",
                                                            CaptureResourceConstants.PRIV_LOG_ACCESS ) );
    }

    @Override
    public String getResourceUri()
    {
        return ProtocolConstants.ADMIN_LOGS_RESOURCE_FRAGMENT;
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
                        final CaptureSession session = captureStore.readLog( ref );
                        if ( session != null )
                        {
                            resources.add( session.asResource( appUrl, getRepositoryRegistry() ) );
                        }
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

        final MediaType mt = mediaTypeOf( request, variant );
        return responseSerializer.serialize( data, mt, request, LOG_ADMIN_TEMPLATE_BASEPATH );
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