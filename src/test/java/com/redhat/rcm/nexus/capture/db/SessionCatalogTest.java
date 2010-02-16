package com.redhat.rcm.nexus.capture.db;

import static com.redhat.rcm.nexus.capture.serialize.CaptureSerializationUtils.getGson;
import static com.redhat.rcm.nexus.capture.serialize.CaptureSerializationUtils.getXStream;

import java.io.File;

import org.junit.Test;

import com.redhat.rcm.nexus.capture.model.CaptureSession;
import com.redhat.rcm.nexus.capture.model.CaptureSessionCatalog;

public class SessionCatalogTest
{

    @Test
    public void serializeToJSON()
        throws InterruptedException
    {
        final String user = "user";
        final String buildTag = "tag";
        final String captureSource = "external";

        final CaptureSessionCatalog cat = new CaptureSessionCatalog( buildTag, captureSource, user );

        cat.add( new CaptureSession( user, buildTag, captureSource ).setFile( new File( "/path/to/session-1.json" ) ) );

        Thread.sleep( 2000 );

        cat.add( new CaptureSession( user, buildTag, captureSource ).setFile( new File( "/path/to/session-2.json" ) ) );

        final String result = getGson().toJson( cat );
        System.out.println( result );
    }

    @Test
    public void serializeToXML()
        throws InterruptedException
    {
        final String user = "user";
        final String buildTag = "tag";
        final String captureSource = "external";

        final CaptureSessionCatalog cat = new CaptureSessionCatalog( buildTag, captureSource, user );

        cat.add( new CaptureSession( user, buildTag, captureSource ).setFile( new File( "/path/to/session-1.json" ) ) );

        Thread.sleep( 2000 );

        cat.add( new CaptureSession( user, buildTag, captureSource ).setFile( new File( "/path/to/session-2.json" ) ) );

        final String result = getXStream().toXML( cat );
        System.out.println( result );
    }

}
