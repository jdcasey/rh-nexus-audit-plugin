package com.redhat.rcm.nexus.capture.db;

import java.util.Arrays;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CaptureSessionTest
{

    @Test
    public void serialize()
    {
        final CaptureSession session = new CaptureSession( "user", "build-tag", "capture-source" );

        session.add( new CaptureRecord( Arrays.asList( new String[] { "central", "codehaus", "jboss-releases" } ),
                                        "/org/groupId/artifactId/VER/artifactId-VER.pom", true ) );

        session.add( new CaptureRecord( Arrays.asList( new String[] { "central", "codehaus", "jboss-releases" } ),
                                        "/org/groupId/artifactId/VER/artifactId-VER.jar", true ) );

        session.add( new CaptureRecord( Arrays.asList( new String[] { "central", "codehaus", "jboss-releases" } ),
                                        "/org/groupId/beetifactId/VER/beetifactId-VER.pom", true ) );

        session.add( new CaptureRecord( Arrays.asList( new String[] { "central", "codehaus", "jboss-releases" } ),
                                        "/org/groupId/beetifactId/VER/beetifactId-VER.jar", true ) );

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();

        final String result = gson.toJson( session );
        System.out.println( result );
    }

    // @Test
    // public void roundTrip()
    // {
    // final CaptureRecord record =
    // new CaptureRecord( "user", "build-tag", "capture-source", Arrays.asList( new String[] { "central",
    // "codehaus", "jboss-releases" } ), "/org/groupId/artifactId/VER/artifactId-VER.pom", true );
    //
    // final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    //
    // final String result = gson.toJson( record );
    //
    // System.out.println( result );
    //
    // final CaptureRecord resultingRecord = gson.fromJson( result, CaptureRecord.class );
    //
    // assertEquals( record.getUser(), resultingRecord.getUser() );
    // assertEquals( record.getBuildTag(), resultingRecord.getBuildTag() );
    // assertEquals( record.getCaptureSource(), resultingRecord.getCaptureSource() );
    // assertEquals( record.getPath(), resultingRecord.getPath() );
    // assertEquals( record.getProcessedRepositories(), resultingRecord.getProcessedRepositories() );
    // }

}
