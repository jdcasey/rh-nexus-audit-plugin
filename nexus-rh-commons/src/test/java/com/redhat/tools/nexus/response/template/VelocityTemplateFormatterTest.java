package com.redhat.tools.nexus.response.template;

import static org.junit.Assert.assertEquals;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Collections;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VelocityTemplateFormatterTest
{

    @Test
    public void format_directConstruction()
        throws Exception
    {
        final VelocityEngine velocity = new VelocityEngine();
        velocity.init();

        final VelocityTemplateFormatter formatter =
            new VelocityTemplateFormatter( velocity, new File( "does/not/exist" ) );

        assertFormatting( formatter );
    }

    @SuppressWarnings( "unchecked" )
    private void assertFormatting( final TemplateFormatter formatter )
        throws MalformedURLException, TemplateException
    {
        final String[] hosts = { "foo", "bar", "baz" };

        final List<URL> targets = new ArrayList<URL>();
        for ( final String host : hosts )
        {
            targets.add( new URL( "http://www." + host + ".com/path/to/artifact.jar" ) );
        }

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
