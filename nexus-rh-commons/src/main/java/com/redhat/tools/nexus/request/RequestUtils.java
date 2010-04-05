package com.redhat.tools.nexus.request;

import static org.codehaus.plexus.util.StringUtils.isNotEmpty;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class RequestUtils
{

    public static final String FULL_DATE_FORMAT = "yyyy-MM-dd+HH-mm-ssZ";

    // NOTE: Restlet Reference.getQueryAsForm() renders any date string that contains a '+' to one that uses ' ' in its place.
    // To work around this, we need to include both formats in this array, to cope with both cases coming from Restlet and those
    // that don't.
    private static final String[] URL_DATE_FORMATs =
        { FULL_DATE_FORMAT, "yyyy-MM-dd HH-mm-ssZ", "yyyy-MM-dd_HH-mm-ssZ", "yyyy-MM-dd+HH-mm-ss",
            "yyyy-MM-dd HH-mm-ss", "yyyy-MM-dd", "MMM dd, yyyy hh:mm:ss a" };

    public static final String PARAM_MODE = "mode";

    public static final String PARAM_FORMAT = "format";

    private static final Logger logger = LoggerFactory.getLogger( RequestUtils.class );

    private RequestUtils()
    {
    }

    public static String requestAttribute( final String param, final Request request )
        throws ResourceException
    {
        final Object val = request.getAttributes().get( param );

        if ( logger.isDebugEnabled() )
        {
            logger.debug( String.format( "%s attribute value: %s", param, val ) );
        }

        final String value = val == null ? null : val.toString();
        if ( value == null || value.trim().length() < 1 )
        {
            throw new ResourceException( Status.CLIENT_ERROR_BAD_REQUEST, "Request path attribute not found: " + param );
        }

        return value;
    }

    public static Date getDate( final String param, final Request request )
        throws ResourceException
    {
        Date before = null;
        try
        {
            final String value = query( request ).getFirstValue( param );
            before = parseDate( value );
        }
        catch ( final ParseException e )
        {
            final String message =
                String.format( "Invalid date format in %s parameter. Error: %s\nMessage: %s", param, e.getClass()
                                                                                                      .getName(),
                               e.getMessage() );

            if ( logger.isDebugEnabled() )
            {
                logger.debug( message, e );
                e.printStackTrace();
            }

            throw new ResourceException( Status.CLIENT_ERROR_BAD_REQUEST, message );
        }

        return before;
    }

    public static String formatUrlDate( final Date date )
    {
        return new SimpleDateFormat( FULL_DATE_FORMAT ).format( date );
    }

    public static Date parseDate( final String value )
        throws ParseException
    {
        return parseDate( value, URL_DATE_FORMATs );
    }

    public static Date parseDate( final String value, final String[] formats )
        throws ParseException
    {
        Date d = null;

        ParseException originalError = null;
        if ( isNotEmpty( value ) )
        {
            for ( final String format : formats )
            {
                try
                {
                    d = new SimpleDateFormat( format ).parse( value );
                    originalError = null;
                    break;
                }
                catch ( final ParseException e )
                {
                    if ( logger.isDebugEnabled() )
                    {
                        logger.debug( String.format( "Failed to parse date: '%s' using format: '%s'\nReason: %s",
                                                     value, format, e.getMessage() ) );
                    }

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

    /**
     * Order of precedence: <br/>
     * <ol>
     * <li>MediaType that corresponds to 'format' query parameter</li>
     * <li>Variant MediaType passed in by Restlet/Nexus</li>
     * <li>MediaType that corresponds to Accept: header</li>
     * </ol>
     */
    public static MediaType mediaTypeOf( final Request request )
    {
        return mediaTypeOf( request, null );
    }

    /**
     * Order of precedence: <br/>
     * <ol>
     * <li>MediaType that corresponds to 'format' query parameter</li>
     * <li>Variant MediaType passed in by Restlet/Nexus</li>
     * <li>MediaType that corresponds to Accept: header</li>
     * </ol>
     */
    public static MediaType mediaTypeOf( final Request request, final Variant variant )
    {
        MediaType mt = null;

        final Form query = query( request );

        final String fmt = query.getFirstValue( PARAM_FORMAT );
        OutputFormat format = OutputFormat.find( fmt );

        if ( format != null )
        {
            mt = format.mediaType();
        }

        if ( mt == null && variant != null )
        {
            mt = variant.getMediaType();
        }

        if ( mt == null )
        {
            final Form headers = headers( request );
            final String[] formatHeaders = headers.getFirstValue( "Accept" ).split( "," );

            List<String> formats = new ArrayList<String>( Arrays.asList( formatHeaders ) );
            Collections.reverse( formats );

            final List<String> tmp = new ArrayList<String>( formats.size() );
            for ( final String f : formats )
            {
                final int idx = f.indexOf( ';' );
                if ( idx > 0 )
                {
                    tmp.add( f.substring( 0, idx - 1 ).trim() );
                }
                else
                {
                    tmp.add( f.trim() );
                }
            }
            formats = tmp;

            for ( final String f : formats )
            {
                format = OutputFormat.find( f );

                if ( format != null )
                {
                    mt = format.mediaType();
                    break;
                }
            }
        }

        return mt;
    }

    public static RequestMode modeOf( final Request request )
    {
        final Form query = query( request );
        final String mode = query.getFirstValue( PARAM_MODE );

        return RequestMode.find( mode );
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
