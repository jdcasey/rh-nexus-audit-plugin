package com.redhat.rcm.nexus.capture.request;

import java.util.Arrays;
import java.util.List;

public enum RequestMode
{

    TABLE_OF_CONTENTS( "list", "toc" ),
    DEFAULT( "def", "default" );

    private List<String> tokens;

    private RequestMode( final String... tokens )
    {
        this.tokens = Arrays.asList( tokens );
    }

    public static RequestMode find( String m )
    {
        if ( m != null )
        {
            m = m.toUpperCase();
            for ( final RequestMode mode : values() )
            {
                if ( mode.tokens.contains( m ) || mode.toString().equals( m ) )
                {
                    return mode;
                }
            }
        }

        return null;
    }

}
