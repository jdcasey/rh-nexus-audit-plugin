package com.redhat.rcm.nexus.capture.config;

import static org.codehaus.plexus.util.StringUtils.isNotEmpty;

import com.google.gson.annotations.SerializedName;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( "captureConfig" )
public class CaptureConfigModel
{

    @SerializedName( "captureSource" )
    @XStreamAlias( "captureSource" )
    private String captureSourceRepoId;

    public String getCaptureSourceRepoId()
    {
        return captureSourceRepoId;
    }

    public CaptureConfigModel setCaptureSourceRepoId( final String captureSourceRepoId )
    {
        this.captureSourceRepoId = captureSourceRepoId;
        return this;
    }

    public boolean isValid()
    {
        return isNotEmpty( captureSourceRepoId );
    }

}
