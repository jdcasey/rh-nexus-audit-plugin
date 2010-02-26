package com.redhat.rcm.nexus.util;

import static org.codehaus.plexus.util.StringUtils.isNotEmpty;

import java.io.File;

public final class PathUtils
{

    // private static final Logger logger = LoggerFactory.getLogger( PathUtils.class );

    private static final char PATH_SLASH = File.separatorChar;

    private PathUtils()
    {
    }

    public static String joinPath( final char separator, final String basePath, final String... parts )
    {
        return concat( separator, basePath, concat( separator, parts ) );
    }

    public static File joinFile( final File dir, final String... parts )
    {
        final String path = concat( PATH_SLASH, parts );

        final File f = new File( dir, path );

        return f;
    }

    private static String concat( final char separator, final String... parts )
    {
        final StringBuilder builder = new StringBuilder();
        for ( final String part : parts )
        {
            if ( builder.length() > 0 )
            {
                if ( part.length() > 0 && part.charAt( 0 ) != separator )
                {
                    builder.append( separator );
                }

                builder.append( part );
            }
            else if ( part.length() > 0 && part.charAt( 0 ) == separator )
            {
                builder.append( part.substring( 1 ) );
            }
            else
            {
                builder.append( part );
            }
        }

        return builder.toString();
    }

    public static String buildUri( final String applicationUrl, final String... parts )
    {
        final StringBuilder sb = new StringBuilder();
        if ( isNotEmpty( applicationUrl ) )
        {
            if ( applicationUrl.endsWith( "/" ) )
            {
                sb.append( applicationUrl.substring( 0, applicationUrl.length() - 1 ) );
            }
            else
            {
                sb.append( applicationUrl );
            }
        }
        else
        {
            sb.append( '/' );
        }

        for ( final String part : parts )
        {
            if ( isNotEmpty( part ) )
            {
                if ( sb.charAt( sb.length() - 1 ) != '/' && part.charAt( 0 ) != '/' )
                {
                    sb.append( '/' );
                }

                sb.append( part );
            }
        }

        return sb.length() == 0 ? null : sb.toString();
    }

}
