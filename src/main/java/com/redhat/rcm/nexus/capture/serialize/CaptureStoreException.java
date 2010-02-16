package com.redhat.rcm.nexus.capture.serialize;

import java.text.MessageFormat;

public class CaptureStoreException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    private final String messageFormat;

    private Object[] params;

    public CaptureStoreException( final String messageFormat, final Throwable cause, final Object... params )
    {
        super( "", cause );
        this.messageFormat = messageFormat;
        this.params = params;
    }

    public CaptureStoreException( final String messageFormat, final Object... params )
    {
        super( "" );
        this.messageFormat = messageFormat;
        this.params = params;
    }

    public CaptureStoreException( final String message, final Throwable cause )
    {
        super( "", cause );
        messageFormat = message;
    }

    public CaptureStoreException( final String message )
    {
        super( "" );
        messageFormat = message;
    }

    @Override
    public String getLocalizedMessage()
    {
        if ( params == null )
        {
            return messageFormat;
        }
        else
        {
            return MessageFormat.format( messageFormat, params );
        }
    }

    @Override
    public String getMessage()
    {
        return getLocalizedMessage();
    }
}
