package com.redhat.rcm.nexus.capture.request;

import static com.redhat.rcm.nexus.capture.serialize.SerializationUtils.getGson;
import static com.redhat.rcm.nexus.capture.serialize.SerializationUtils.getXStream;
import static org.codehaus.plexus.util.StringUtils.isNotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.resource.Variant;

import com.redhat.rcm.nexus.capture.CaptureResourceConstants;

public final class RequestUtils
{

    private static final String FULL_DATE_FORMAT = "yyyy-MM-dd+HH-mm-ssZ";

    private static final String[] URL_DATE_FORMATs = { "yyyy-MM-dd", "yyyy-MM-dd+HH-mm-ss", FULL_DATE_FORMAT };

    // private static final Logger logger = LoggerFactory.getLogger( RequestUtils.class );

    private RequestUtils()
    {
    }

    public static String formatUrlDate( final Date date )
    {
        return new SimpleDateFormat( FULL_DATE_FORMAT ).format( date );
    }

    public static Date parseUrlDate( final String value )
        throws ParseException
    {
        Date d = null;

        ParseException originalError = null;
        if ( isNotEmpty( value ) )
        {
            for ( final String format : URL_DATE_FORMATs )
            {
                try
                {
                    d = new SimpleDateFormat( format ).parse( value );
                    break;
                }
                catch ( final ParseException e )
                {
                    if ( originalError == null )
                    {
                        originalError = e;
                    }
                }
            }
        }

        if ( originalError != null )
        {
            throw originalError;
        }

        return d;
    }

    public static String render( final Object obj, final MediaType mt )
    {
        if ( mt == MediaType.APPLICATION_RSS_XML )
        {
            throw new UnsupportedOperationException();
        }
        else if ( mt == MediaType.APPLICATION_XML )
        {
            return getXStream().toXML( obj );
        }
        else if ( mt == MediaType.APPLICATION_JSON )
        {
            return getGson().toJson( obj );
        }

        return null;
    }

    public static MediaType mediaTypeOf( final Request request, final Variant variant )
    {
        final Form query = query( request );
        MediaType mt = variant == null ? null : variant.getMediaType();

        final String fmt = query.getFirstValue( CaptureResourceConstants.PARAM_FORMAT );

        final OutputFormat format = OutputFormat.find( fmt );
        if ( format != null )
        {
            mt = format.mediaType();
        }

        return mt;
    }

    public static RequestMode modeOf( final Request request )
    {
        final Form query = query( request );
        final String mode = query.getFirstValue( CaptureResourceConstants.PARAM_MODE );

        final RequestMode m = RequestMode.find( mode );

        return m == null ? RequestMode.DEFAULT : m;
    }

    public static Form headers( final Request request )
    {
        return (Form) request.getAttributes().get( "org.restlet.http.headers" );
    }

    public static Form query( final Request request )
    {
        return request.getResourceRef().getQueryAsForm();
    }

}
