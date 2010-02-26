package com.redhat.rcm.nexus.capture;

import static com.redhat.rcm.nexus.capture.request.RequestUtils.getDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.rcm.nexus.capture.model.CaptureSessionRef;
import com.redhat.rcm.nexus.capture.store.CaptureSessionQuery;
import com.redhat.rcm.nexus.capture.store.CaptureStore;
import com.redhat.rcm.nexus.capture.store.CaptureStoreException;
import com.redhat.rcm.nexus.protocol.CaptureSessionRefResource;

public final class CaptureLogUtils
{

    private static final Logger logger = LoggerFactory.getLogger( CaptureLogUtils.class );

    private CaptureLogUtils()
    {
    }

    static void deleteLogs( final CaptureStore captureStore, final String user, final String buildTag,
                            final Request request )
        throws ResourceException
    {
        final CaptureSessionQuery query = new CaptureSessionQuery().setUser( user ).setBuildTag( buildTag );

        setBeforeDate( query, request );
        setSinceDate( query, request );

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

    static void setBeforeDate( final CaptureSessionQuery query, final Request request )
        throws ResourceException
    {
        final Date d = getDate( CaptureResourceConstants.PARAM_BEFORE, request );
        if ( d != null )
        {
            query.setBefore( d );
        }
    }

    static void setSinceDate( final CaptureSessionQuery query, final Request request )
        throws ResourceException
    {
        final Date d = getDate( CaptureResourceConstants.PARAM_SINCE, request );
        if ( d != null )
        {
            query.setBefore( d );
        }
    }

    static List<CaptureSessionRefResource> queryLogs( final CaptureStore captureStore, final CaptureSessionQuery query,
                                                      final String appUrl )
        throws CaptureStoreException
    {
        final List<CaptureSessionRef> logs = captureStore.getLogs( query );
        if ( logs != null )
        {
            final List<CaptureSessionRefResource> resources = new ArrayList<CaptureSessionRefResource>( logs.size() );

            for ( final CaptureSessionRef ref : logs )
            {
                if ( ref != null )
                {
                    resources.add( ref.asResource( appUrl ) );
                }
            }

            return resources;
        }

        return null;
    }
}
