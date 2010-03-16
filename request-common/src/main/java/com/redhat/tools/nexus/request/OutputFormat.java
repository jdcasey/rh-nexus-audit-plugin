package com.redhat.tools.nexus.request;

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

    public static OutputFormat find( final String fmt )
    {
        if ( fmt != null )
        {
            final MediaType mt = MediaType.valueOf( fmt );

            if ( mt != null )
            {
                for ( final OutputFormat format : values() )
                {
                    if ( format.mediaType().equals( mt ) )
                    {
                        return format;
                    }
                }
            }
        }

        return null;
    }

}
