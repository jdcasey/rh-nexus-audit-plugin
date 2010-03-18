package org.sonatype.plugins.it.util;

import org.codehaus.groovy.control.CompilationFailedException;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroovyAssertions
    implements ContentAssertions
{

    private final File groovyFile;

    private final Map<String, Object> context;

    private final String entry;

    public GroovyAssertions( final String entry, final File groovyFile, final Map<String, Object> context )
    {
        this.entry = entry;
        this.groovyFile = groovyFile;
        this.context = context;
    }

    public List<String> assertContents( final String content )
        throws IOException
    {
        Binding binding = new Binding( context );
        binding.setVariable( "content", content );

        GroovyShell shell = new GroovyShell( Thread.currentThread().getContextClassLoader(), binding );

        List<?> result;
        try
        {
            result = (List<?>) shell.evaluate( groovyFile );
        }
        catch ( CompilationFailedException e )
        {
            IOException err = new IOException( "Failed to parse Groovy: " + e.getMessage() );
            err.initCause( e );

            throw err;
        }

        List<String> messages = new ArrayList<String>();
        if ( result != null && !result.isEmpty() )
        {
            for ( Object obj : result )
            {
                if ( obj != null )
                {
                    messages.add( obj.toString() );
                }
            }
        }

        return messages;
    }

    public String getArchivePath()
    {
        return entry;
    }

}
