package org.sonatype.plugins.it.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StringAssertions
    implements ContentAssertions
{

    private final String entry;

    private final Set<String> strings;

    public StringAssertions( final String entry, final Set<String> strings )
    {
        this.entry = entry;
        this.strings = strings;
    }

    public List<String> assertContents( final String content )
        throws IOException
    {
        List<String> missing = new ArrayList<String>();
        for ( String str : strings )
        {
            if ( content.indexOf( str ) < 0 )
            {
                missing.add( str );
            }
        }

        return missing;
    }

    public String getArchivePath()
    {
        return entry;
    }

}
