package com.redhat.rcm.nexus.capture.request;

import static com.redhat.rcm.nexus.capture.serialize.SerializationUtils.getGson;
import static com.redhat.rcm.nexus.capture.serialize.SerializationUtils.getXStream;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.resource.Variant;

public final class RequestUtils
{

    private static final String PARAM_MODE = "mode";

    private static final String PARAM_FORMAT = "format";

    // private static final Logger logger = LoggerFactory.getLogger( RequestUtils.class );

    private RequestUtils()
    {
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
        MediaType mt = variant.getMediaType();

        final String fmt = query.getFirstValue( PARAM_FORMAT );

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
