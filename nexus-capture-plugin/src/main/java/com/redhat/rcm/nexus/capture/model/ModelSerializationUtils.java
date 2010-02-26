package com.redhat.rcm.nexus.capture.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.redhat.rcm.nexus.capture.config.CaptureConfigModel;
import com.redhat.rcm.nexus.util.ProtocolUtils;
import com.thoughtworks.xstream.XStream;

public final class ModelSerializationUtils
{

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

    private ModelSerializationUtils()
    {
    }

    public static Gson getGson()
    {
        return ProtocolUtils.getGson();
    }

    public static Date normalizeDate( final Date d )
    {
        final SimpleDateFormat fmt = new SimpleDateFormat( DATE_FORMAT );
        try
        {
            return fmt.parse( fmt.format( d ) );
        }
        catch ( final ParseException e )
        {
            throw new IllegalStateException( String.format( "Format-Parse round trip for java.util.Date failed."
                            + "\nFormat: %s\nDate: %s\nReason: %s", DATE_FORMAT, d, e.getMessage() ), e );
        }
    }

    public static XStream getXStreamForStore()
    {
        final XStream xs = createXStream();

        xs.registerLocalConverter( CaptureTarget.class, "processedRepositories",
                                   new ProtocolUtils.StringListConverter( "repository" ) );

        xs.registerLocalConverter( CaptureSessionCatalog.class, "sessions",
                                   new ProtocolUtils.DateToFileMapTypeAdapter( DATE_FORMAT, "session" ) );

        // Model/Query classes
        xs.processAnnotations( CaptureSession.class );
        xs.processAnnotations( CaptureTarget.class );
        xs.processAnnotations( CaptureSessionCatalog.class );
        xs.processAnnotations( CaptureSessionRef.class );

        return xs;
    }

    public static XStream getXStreamForConfig()
    {
        final XStream xs = createXStream();

        xs.processAnnotations( CaptureConfigModel.class );

        return xs;
    }

    private static XStream createXStream()
    {
        final XStream xs = new XStream();

        xs.setMode( XStream.NO_REFERENCES );
        xs.registerConverter( new ProtocolUtils.CustomFormatDateConverter( DATE_FORMAT ) );

        return xs;
    }

}
