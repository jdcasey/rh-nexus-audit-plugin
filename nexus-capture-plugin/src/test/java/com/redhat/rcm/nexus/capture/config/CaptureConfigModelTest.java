package com.redhat.rcm.nexus.capture.config;

import static com.redhat.rcm.nexus.capture.serialize.SerializationUtils.getGson;
import static com.redhat.rcm.nexus.capture.serialize.SerializationUtils.getXStreamForConfig;
import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class CaptureConfigModelTest
{

    @Test
    public void roundTripInJSON()
    {
        final CaptureConfigModel model = new CaptureConfigModel().setCaptureSourceRepoId( "public" );
        final String json = getGson().toJson( model );

        System.out.println( json );

        final CaptureConfigModel modelResult = getGson().fromJson( json, CaptureConfigModel.class );

        assertEquals( model.getCaptureSourceRepoId(), modelResult.getCaptureSourceRepoId() );
    }

    @Test
    public void roundTripInXML()
    {
        final CaptureConfigModel model = new CaptureConfigModel().setCaptureSourceRepoId( "public" );
        final String xml = getXStreamForConfig().toXML( model );

        System.out.println( xml );

        final CaptureConfigModel modelResult = (CaptureConfigModel) getXStreamForConfig().fromXML( xml );

        assertEquals( model.getCaptureSourceRepoId(), modelResult.getCaptureSourceRepoId() );
    }

}
