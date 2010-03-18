package org.sonatype.plugins.it.util;

import java.io.IOException;
import java.util.List;

public interface ContentAssertions
{

    String getArchivePath();

    List<String> assertContents( String content )
        throws IOException;

}
