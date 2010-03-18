package org.sonatype.plugins.it.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class RegexAssertions
    implements ContentAssertions
{

    private final String entry;

    private final Set<String> patterns;

    public RegexAssertions( final String entry, final Set<String> patterns )
    {
        this.entry = entry;
        this.patterns = patterns;
    }

    public List<String> assertContents( final String content )
        throws IOException
    {
        List<String> missing = new ArrayList<String>();
        for ( String pattern : patterns )
        {
            Pattern p = Pattern.compile( pattern );
            if ( !p.matcher( content ).find() )
            {
                missing.add( pattern );
            }
        }

        return missing;
    }

    public String getArchivePath()
    {
        return entry;
    }

}
