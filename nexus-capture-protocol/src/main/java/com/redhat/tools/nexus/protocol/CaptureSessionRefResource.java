package com.redhat.tools.nexus.protocol;

import static com.redhat.tools.nexus.protocol.CaptureSessionResource.buildResourceUri;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( ProtocolConstants.SESSION_REF_ROOT )
public class CaptureSessionRefResource
    implements Comparable<CaptureSessionRefResource>
{

    @SerializedName( ProtocolConstants.RESOURCE_URI_FIELD )
    @XStreamAlias( ProtocolConstants.RESOURCE_URI_FIELD )
    private final String url;

    private final String user;

    @SerializedName( ProtocolConstants.BUILD_TAG_FIELD )
    @XStreamAlias( ProtocolConstants.BUILD_TAG_FIELD )
    private final String buildTag;

    @SerializedName( ProtocolConstants.DATE_FIELD )
    @XStreamAlias( ProtocolConstants.DATE_FIELD )
    private final Date date;

    // used for gson deserialization
    private CaptureSessionRefResource()
    {
        this.user = null;
        this.buildTag = null;
        this.date = null;
        this.url = null;
    }

    public CaptureSessionRefResource( final String user, final String buildTag, final Date date,
                                      final String applicationUrl )
    {
        this.user = user;
        this.buildTag = buildTag;
        this.date = date;
        this.url = buildResourceUri( applicationUrl, user, buildTag, date );
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

    public String getUrl()
    {
        return url;
    }

    public int compareTo( final CaptureSessionRefResource ref )
    {
        return date.compareTo( ref.date );
    }

    public static CaptureSessionRefResource payloadPrototype()
    {
        return new CaptureSessionRefResource();
    }

}
