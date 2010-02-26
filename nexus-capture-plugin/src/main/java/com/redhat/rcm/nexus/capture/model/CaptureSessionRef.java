package com.redhat.rcm.nexus.capture.model;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.redhat.rcm.nexus.protocol.CaptureSessionRefResource;
import com.redhat.rcm.nexus.protocol.ProtocolConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( ProtocolConstants.SESSION_REF_ROOT )
public class CaptureSessionRef
    implements Comparable<CaptureSessionRef>
{

    private final String user;

    @SerializedName( ProtocolConstants.BUILD_TAG_FIELD )
    @XStreamAlias( ProtocolConstants.BUILD_TAG_FIELD )
    private final String buildTag;

    @SerializedName( ProtocolConstants.DATE_FIELD )
    @XStreamAlias( ProtocolConstants.DATE_FIELD )
    private final Date date;

    // used for gson deserialization
    @SuppressWarnings( "unused" )
    private CaptureSessionRef()
    {
        this.user = null;
        this.buildTag = null;
        this.date = null;
    }

    public CaptureSessionRef( final String user, final String buildTag, final Date date )
    {
        this.user = user;
        this.buildTag = buildTag;
        this.date = date;
    }

    public String getUser()
    {
        return user;
    }

    public String getBuildTag()
    {
        return buildTag;
    }

    public Date getDate()
    {
        return date;
    }

    public int compareTo( final CaptureSessionRef ref )
    {
        return date.compareTo( ref.date );
    }

    public String key()
    {
        return CaptureSession.key( user, buildTag );
    }

    public CaptureSessionRefResource asResource( final String applicationUrl )
    {
        return new CaptureSessionRefResource( user, buildTag, date, applicationUrl );
    }

}
