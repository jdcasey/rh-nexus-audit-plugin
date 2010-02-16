package com.redhat.rcm.nexus.capture.request;

import org.restlet.data.MediaType;

public enum OutputFormat
{

    PLAIN( MediaType.TEXT_PLAIN ),
    XML( MediaType.APPLICATION_XML ),
    JSON( MediaType.APPLICATION_JSON ),
    RSS( MediaType.APPLICATION_RSS_XML ),
    ATOM( MediaType.APPLICATION_ATOM_XML );

    private MediaType mt;

    private OutputFormat( final MediaType mt )
    {
        this.mt = mt;
    }

    public MediaType mediaType()
    {
        return mt;
    }

    public static OutputFormat find( String fmt )
    {
        if ( fmt != null )
        {
            fmt = fmt.toUpperCase();
            for ( final OutputFormat format : values() )
            {
                if ( format.toString().equals( fmt ) )
                {
                    return format;
                }
            }
        }

        return null;
    }

}
