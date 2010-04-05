package com.redhat.tools.nexus.capture.config;

import static junit.framework.Assert.assertEquals;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Test;

import com.redhat.tools.nexus.capture.config.model.CaptureConfigModel;
import com.redhat.tools.nexus.capture.config.model.io.xpp3.CaptureConfigXpp3Reader;
import com.redhat.tools.nexus.capture.config.model.io.xpp3.CaptureConfigXpp3Writer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class CaptureConfigModelTest
{

    @Test
    public void roundTripInModello()
        throws IOException, XmlPullParserException
    {
        final CaptureConfigModel model = new CaptureConfigModel();
        model.setCaptureSource( "public" );

        final StringWriter sw = new StringWriter();
        new CaptureConfigXpp3Writer().write( sw, model );

        System.out.println( sw.toString() );

        final CaptureConfigModel modelResult = new CaptureConfigXpp3Reader().read( new StringReader( sw.toString() ) );

        assertEquals( model.getCaptureSource(), modelResult.getCaptureSource() );
    }

}
