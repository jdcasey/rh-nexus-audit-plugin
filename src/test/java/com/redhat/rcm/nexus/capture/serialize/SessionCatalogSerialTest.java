package com.redhat.rcm.nexus.capture.serialize;

import static com.redhat.rcm.nexus.capture.serialize.SerializationUtils.getGson;
import static com.redhat.rcm.nexus.capture.serialize.SerializationUtils.getXStream;

import java.io.File;

import org.junit.Test;

import com.redhat.rcm.nexus.capture.model.CaptureSession;
import com.redhat.rcm.nexus.capture.model.CaptureSessionCatalog;

public class SessionCatalogSerialTest
{

    @Test
    public void roundTripJSON()
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

        final CaptureSessionCatalog resultCat = getGson().fromJson( result, CaptureSessionCatalog.class );
    }

    @Test
    public void roundTripXML()
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

        final CaptureSessionCatalog resultCat = (CaptureSessionCatalog) getXStream().fromXML( result );
    }

}
