package com.redhat.tools.nexus.capture.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( "captureConfig" )
public class CaptureConfigDTO
{

    private CaptureConfigModel data;

    public CaptureConfigDTO()
    {
    }

    public CaptureConfigDTO( final CaptureConfigModel data )
    {
        this.data = data;
    }

    public CaptureConfigModel getData()
    {
        return data;
    }

    public void setData( final CaptureConfigModel data )
    {
        this.data = data;
    }

}
