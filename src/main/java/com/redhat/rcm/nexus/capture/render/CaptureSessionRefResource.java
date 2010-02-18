package com.redhat.rcm.nexus.capture.render;

import static com.redhat.rcm.nexus.capture.render.CaptureSessionResource.buildResourceUri;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.redhat.rcm.nexus.capture.model.CaptureSession;
import com.redhat.rcm.nexus.capture.model.CaptureSessionRef;
import com.redhat.rcm.nexus.capture.serialize.SerializationConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( SerializationConstants.SESSION_REF_ROOT )
public class CaptureSessionRefResource
    implements Comparable<CaptureSessionRefResource>
{

    private final String user;

    @SerializedName( SerializationConstants.BUILD_TAG_FIELD )
    @XStreamAlias( SerializationConstants.BUILD_TAG_FIELD )
    private final String buildTag;

    @SerializedName( SerializationConstants.CAPTURE_SOURCE_FIELD )
    @XStreamAlias( SerializationConstants.CAPTURE_SOURCE_FIELD )
    private final String captureSource;

    @SerializedName( SerializationConstants.DATE_FIELD )
    @XStreamAlias( SerializationConstants.DATE_FIELD )
    private final Date date;

    @SerializedName( SerializationConstants.RESOURCE_URI_FIELD )
    @XStreamAlias( SerializationConstants.RESOURCE_URI_FIELD )
    private final String url;

    // used for gson deserialization
    @SuppressWarnings( "unused" )
    private CaptureSessionRefResource()
    {
        this.user = null;
        this.buildTag = null;
        this.captureSource = null;
        this.date = null;
        this.url = null;
    }

    public CaptureSessionRefResource( final CaptureSessionRef ref, final String applicationUrl )
    {
        this.user = ref.getUser();
        this.buildTag = ref.getBuildTag();
        this.captureSource = ref.getCaptureSource();
        this.date = ref.getDate();

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

    public String getCaptureSource()
    {
        return captureSource;
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

    public String key()
    {
        return CaptureSession.key( buildTag, captureSource, user );
    }

}
