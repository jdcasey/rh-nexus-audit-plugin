package com.redhat.rcm.nexus.capture.request;

import static org.codehaus.plexus.util.StringUtils.isNotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.rcm.nexus.capture.CaptureResourceConstants;

public final class RequestUtils
{

    private static final String FULL_DATE_FORMAT = "yyyy-MM-dd+HH-mm-ssZ";

    private static final String[] URL_DATE_FORMATs = { FULL_DATE_FORMAT, "yyyy-MM-dd+HH-mm-ss", "yyyy-MM-dd" };

    private static final Logger logger = LoggerFactory.getLogger( RequestUtils.class );

    private RequestUtils()
    {
    }

    public static String buildUri( final String applicationUrl, final String... parts )
    {
        final StringBuilder sb = new StringBuilder();
        if ( isNotEmpty( applicationUrl ) )
        {
            if ( applicationUrl.endsWith( "/" ) )
            {
                sb.append( applicationUrl.substring( 0, applicationUrl.length() - 1 ) );
            }
            else
            {
                sb.append( applicationUrl );
            }
        }
        else
        {
            sb.append( '/' );
        }

        for ( final String part : parts )
        {
            if ( isNotEmpty( part ) )
            {
                if ( sb.charAt( sb.length() - 1 ) != '/' && part.charAt( 0 ) != '/' )
                {
                    sb.append( '/' );
                }

                sb.append( part );
            }
        }

        return sb.length() == 0 ? null : sb.toString();
    }

    public static Date getDate( final String param, final Request request )
        throws ResourceException
    {
        Date before = null;
        try
        {
            final String value = query( request ).getFirstValue( param );
            before = parseUrlDate( value );
        }
        catch ( final ParseException e )
        {
            final String message =
                String.format( "Invalid date format in %s parameter. Error: %s\nMessage: %s", param, e.getClass()
                                                                                                      .getName(),
                               e.getMessage() );

            logger.error( message, e );

            e.printStackTrace();

            throw new ResourceException( Status.CLIENT_ERROR_BAD_REQUEST, message );
        }

        return before;
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
