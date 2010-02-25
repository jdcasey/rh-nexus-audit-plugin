package com.redhat.rcm.nexus.capture.serialize;

import com.google.gson.Gson;
import com.redhat.rcm.nexus.capture.config.CaptureConfigModel;
import com.redhat.rcm.nexus.capture.model.CaptureSession;
import com.redhat.rcm.nexus.capture.model.CaptureSessionCatalog;
import com.redhat.rcm.nexus.capture.model.CaptureSessionRef;
import com.redhat.rcm.nexus.capture.model.CaptureTarget;
import com.redhat.rcm.nexus.protocol.ProtocolUtils;
import com.thoughtworks.xstream.XStream;

public final class SerializationUtils
{

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

    private SerializationUtils()
    {
    }

    public static Gson getGson()
    {
        return ProtocolUtils.getGson();
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
