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
import java.util.Date;

public final class RequestUtils
{

    public static final String FULL_DATE_FORMAT = "yyyy-MM-dd+HH-mm-ssZ";

    private static final String[] URL_DATE_FORMATs = { FULL_DATE_FORMAT, "yyyy-MM-dd+HH-mm-ss", "yyyy-MM-dd" };

    public static final String PARAM_MODE = "mode";

    public static final String PARAM_FORMAT = "format";

    private static final Logger logger = LoggerFactory.getLogger( RequestUtils.class );

    private RequestUtils()
    {
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

        String fmt = query.getFirstValue( PARAM_FORMAT );
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
            fmt = headers.getFirstValue( "Accept" );
            format = OutputFormat.find( fmt );

            if ( mt == null && format != null )
            {
                mt = format.mediaType();
            }
        }

        return mt;
    }

    public static RequestMode modeOf( final Request request )
    {
        final Form query = query( request );
        final String mode = query.getFirstValue( PARAM_MODE );

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
