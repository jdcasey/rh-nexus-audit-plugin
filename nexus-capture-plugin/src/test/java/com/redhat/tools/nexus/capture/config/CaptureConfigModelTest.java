package com.redhat.tools.nexus.capture.config;

import static com.redhat.tools.nexus.capture.model.ModelSerializationUtils.getGson;
import static com.redhat.tools.nexus.capture.model.ModelSerializationUtils.getXStreamForConfig;
import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import com.redhat.tools.nexus.capture.config.CaptureConfigModel;

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
