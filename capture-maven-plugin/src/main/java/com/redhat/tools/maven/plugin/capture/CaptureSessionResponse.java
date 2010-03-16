package com.redhat.tools.maven.plugin.capture;

import org.apache.http.StatusLine;

import com.redhat.tools.nexus.protocol.CaptureSessionRefResource;

public class CaptureSessionResponse
{

    private CaptureSessionRefResource sessionRef;

    private StatusLine status;

    public CaptureSessionResponse( final CaptureSessionRefResource sessionRef, final StatusLine status )
    {
        this.sessionRef = sessionRef;
        this.status = status;
    }

    public boolean isSuccessful()
    {
        final int code = status.getStatusCode();
        return code > 199 && code < 300;
    }

    public CaptureSessionRefResource getSessionRef()
    {
        return sessionRef;
    }

    public void setSessionRef( final CaptureSessionRefResource sessionRef )
    {
        this.sessionRef = sessionRef;
    }

    public StatusLine getStatus()
    {
        return status;
    }

    public void setStatus( final StatusLine status )
    {
        this.status = status;
    }

}
