package com.redhat.rcm.nexus.capture.model.render;

import static com.redhat.rcm.nexus.capture.model.render.CaptureSessionResource.buildResourceUri;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.redhat.rcm.nexus.capture.model.CaptureSessionRef;
import com.redhat.rcm.nexus.capture.serialize.SerializationConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( SerializationConstants.SESSION_REF_ROOT )
public class CaptureSessionRefResource
    implements Comparable<CaptureSessionRefResource>
{

    @SerializedName( SerializationConstants.RESOURCE_URI_FIELD )
    @XStreamAlias( SerializationConstants.RESOURCE_URI_FIELD )
    private final String url;

    private final String user;

    @SerializedName( SerializationConstants.BUILD_TAG_FIELD )
    @XStreamAlias( SerializationConstants.BUILD_TAG_FIELD )
    private final String buildTag;

    @SerializedName( SerializationConstants.DATE_FIELD )
    @XStreamAlias( SerializationConstants.DATE_FIELD )
    private final Date date;

    // used for gson deserialization
    @SuppressWarnings( "unused" )
    private CaptureSessionRefResource()
    {
        this.user = null;
        this.buildTag = null;
        this.date = null;
        this.url = null;
    }

    public CaptureSessionRefResource( final CaptureSessionRef ref, final String applicationUrl )
    {
        this.user = ref.getUser();
        this.buildTag = ref.getBuildTag();
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

}
