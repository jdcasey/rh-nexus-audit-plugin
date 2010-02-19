package com.redhat.rcm.nexus.capture.serialize;

import static com.redhat.rcm.nexus.capture.model.serialize.SerializationUtils.getGson;
import static com.redhat.rcm.nexus.capture.model.serialize.SerializationUtils.getXStreamForStore;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.artifact.IllegalArtifactCoordinateException;
import org.sonatype.nexus.proxy.item.StorageItem;

import com.redhat.rcm.nexus.capture.model.CaptureSession;
import com.redhat.rcm.nexus.capture.model.CaptureTarget;

public class CaptureSessionSerialTest
{

    private final List<Object> mocks = new ArrayList<Object>();

    // call manually...
    public void replayMocks()
    {
        if ( mocks != null )
        {
            for ( final Object mock : mocks )
            {
                replay( mock );
            }
        }
    }

    @After
    public void verifyMocks()
    {
        if ( mocks != null )
        {
            for ( final Object mock : mocks )
            {
                verify( mock );
            }
        }
    }

    @Test
    public void roundTripJSON()
        throws IllegalArtifactCoordinateException
    {
        final CaptureSession session = new CaptureSession( "user", "build-tag", "capture-source" );

        final StorageItem[] items =
            { newStorageItem( "/org/groupId/artifactId/VER/artifactId-VER.pom" ),
             newStorageItem( "/org/groupId/artifactId/VER/artifactId-VER.jar" ),
             newStorageItem( "/org/groupId/beetifactId/VER/beetifactId-VER.pom" ),
             newStorageItem( "/org/groupId/beetifactId/VER/beetifactId-VER.jar" ) };

        final Gav[] gavs =
            { new Gav( "org.groupId", "artifactId", "VER" ), new Gav( "org.groupId", "beetifactId", "VER" ) };

        replayMocks();

        int i = 0;
        session.add( new CaptureTarget( Arrays.asList( new String[] { "central", "codehaus", "jboss-releases" } ),
                                        "/org/groupId/artifactId/VER/artifactId-VER.pom",
                                        gavs[0],
                                        items[i++] ) );

        session.add( new CaptureTarget( Arrays.asList( new String[] { "central", "codehaus", "jboss-releases" } ),
                                        "/org/groupId/artifactId/VER/artifactId-VER.jar",
                                        gavs[0],
                                        items[i++] ) );

        session.add( new CaptureTarget( Arrays.asList( new String[] { "central", "codehaus", "jboss-releases" } ),
                                        "/org/groupId/beetifactId/VER/beetifactId-VER.pom",
                                        gavs[1],
                                        items[i++] ) );

        session.add( new CaptureTarget( Arrays.asList( new String[] { "central", "codehaus", "jboss-releases" } ),
                                        "/org/groupId/beetifactId/VER/beetifactId-VER.jar",
                                        gavs[1],
                                        items[i++] ) );

        final String result = getGson().toJson( session );
        System.out.println( result );

        final CaptureSession resultSess = getGson().fromJson( result, CaptureSession.class );
    }

    @Test
    public void roundTripXML()
        throws IllegalArtifactCoordinateException
    {
        final CaptureSession session = new CaptureSession( "user", "build-tag", "capture-source" );

        final StorageItem[] items =
            { newStorageItem( "/org/groupId/artifactId/VER/artifactId-VER.pom" ),
             newStorageItem( "/org/groupId/artifactId/VER/artifactId-VER.jar" ),
             newStorageItem( "/org/groupId/beetifactId/VER/beetifactId-VER.pom" ),
             newStorageItem( "/org/groupId/beetifactId/VER/beetifactId-VER.jar" ) };

        final Gav[] gavs =
            { new Gav( "org.groupId", "artifactId", "VER" ), new Gav( "org.groupId", "beetifactId", "VER" ) };

        replayMocks();

        int i = 0;
        session.add( new CaptureTarget( Arrays.asList( new String[] { "central", "codehaus", "jboss-releases" } ),
                                        "/org/groupId/artifactId/VER/artifactId-VER.pom",
                                        gavs[0],
                                        items[i++] ) );

        session.add( new CaptureTarget( Arrays.asList( new String[] { "central", "codehaus", "jboss-releases" } ),
                                        "/org/groupId/artifactId/VER/artifactId-VER.jar",
                                        gavs[0],
                                        items[i++] ) );

        session.add( new CaptureTarget( Arrays.asList( new String[] { "central", "codehaus", "jboss-releases" } ),
                                        "/org/groupId/beetifactId/VER/beetifactId-VER.pom",
                                        gavs[1],
                                        items[i++] ) );

        session.add( new CaptureTarget( Arrays.asList( new String[] { "central", "codehaus", "jboss-releases" } ),
                                        "/org/groupId/beetifactId/VER/beetifactId-VER.jar",
                                        gavs[1],
                                        items[i++] ) );

        final String result = getXStreamForStore().toXML( session );
        System.out.println( result );

        final CaptureSession resultSess = (CaptureSession) getXStreamForStore().fromXML( result );
    }

    private StorageItem newStorageItem( final String path )
    {
        final StorageItem item = createMock( StorageItem.class );
        mocks.add( item );

        expect( item.getPath() ).andReturn( path ).anyTimes();
        expect( item.getRepositoryId() ).andReturn( "fooRepo" ).anyTimes();

        return item;
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
