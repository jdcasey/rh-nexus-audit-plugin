package com.redhat.tools.nexus.response.template;

import static org.junit.Assert.assertEquals;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.velocity.DefaultVelocityComponent;
import org.junit.Test;

import com.redhat.tools.nexus.response.template.TemplateFormatter;
import com.redhat.tools.nexus.response.template.VelocityTemplateFormatter;

import edu.emory.mathcs.backport.java.util.Collections;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VelocityTemplateFormatterTest
{

    @Test
    @SuppressWarnings( "unchecked" )
    public void formatCaptureSessionResource_RemoteURLsOnly()
        throws Exception
    {
        final String[] hosts = { "foo", "bar", "baz" };

        final List<URL> targets = new ArrayList<URL>();
        for ( final String host : hosts )
        {
            targets.add( new URL( "http://www." + host + ".com/path/to/artifact.jar" ) );
        }

        final DefaultVelocityComponent velocity = new DefaultVelocityComponent();
        velocity.enableLogging( new ConsoleLogger( Logger.LEVEL_DEBUG, "test" ) );
        velocity.initialize();

        final VelocityTemplateFormatter formatter =
            new VelocityTemplateFormatter( velocity, new File( "does/not/exist" ) );

        formatter.initialize();

        final Map<String, Object> ctx = Collections.singletonMap( "data", targets );

        final String result = formatter.format( TemplateFormatter.TEMPLATES_ROOT + "test", "default", ctx );

        final StringBuilder sb = new StringBuilder();
        for ( final String host : hosts )
        {
            sb.append( "http://www." ).append( host ).append( ".com/path/to/artifact.jar\n" );
        }

        System.out.println( result );

        assertEquals( sb.toString(), result );
    }

}
