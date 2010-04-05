package com.redhat.tools.nexus.capture.model;

import org.sonatype.nexus.artifact.Gav;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.redhat.tools.nexus.capture.config.CaptureConfigModel;
import com.redhat.tools.nexus.protocol.ProtocolUtils;
import com.redhat.tools.nexus.protocol.ProtocolUtils.GavCreator;
import com.redhat.tools.nexus.serial.CustomFormatDateConverter;
import com.redhat.tools.nexus.serial.DateToFileMapTypeAdapter;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

public final class ModelSerializationUtils
{

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

    // NOTE: Restlet Reference.getQueryAsForm() renders any date string that contains a '+' to one that uses ' ' in its place.
    // To work around this, we need to include both formats in this array, to cope with both cases coming from Restlet and those
    // that don't.
    private static final String[] DATE_FORMATS = { DATE_FORMAT };

    private static final TypeToken<TreeMap<Date, File>> DATE_TO_FILE_MAP_TT = new TypeToken<TreeMap<Date, File>>()
    {
    };

    private ModelSerializationUtils()
    {
    }

    public static Gson getGson()
    {
        return new GsonBuilder().setPrettyPrinting()
                                .registerTypeAdapter( Gav.class, new GavCreator() )
                                .registerTypeAdapter(
                                                      DATE_TO_FILE_MAP_TT.getType(),
                                                      new DateToFileMapTypeAdapter( DATE_FORMAT, DATE_FORMATS,
                                                                                    "session" ) )
                                .create();
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

        xs.registerLocalConverter( CaptureSessionCatalog.class, "sessions", new DateToFileMapTypeAdapter( DATE_FORMAT,
                                                                                                          DATE_FORMATS,
                                                                                                          "session" ) );

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
        xs.registerConverter( new CustomFormatDateConverter( DATE_FORMAT ) );

        return xs;
    }

}
