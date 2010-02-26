package com.redhat.rcm.nexus.protocol;

import static com.redhat.rcm.nexus.util.PathUtils.buildUri;
import static com.redhat.rcm.nexus.util.ProtocolUtils.formatUrlDate;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( ProtocolConstants.SESSION_ROOT )
public class CaptureSessionResource
{

    @SerializedName( ProtocolConstants.RESOURCE_URI_FIELD )
    @XStreamAlias( ProtocolConstants.RESOURCE_URI_FIELD )
    private final String url;

    private final String user;

    @SerializedName( ProtocolConstants.BUILD_TAG_FIELD )
    @XStreamAlias( ProtocolConstants.BUILD_TAG_FIELD )
    private final String buildTag;

    @SerializedName( ProtocolConstants.START_DATE_FIELD )
    @XStreamAlias( ProtocolConstants.START_DATE_FIELD )
    private final Date started;

    @SerializedName( ProtocolConstants.LAST_UPDATE_FIELD )
    @XStreamAlias( ProtocolConstants.LAST_UPDATE_FIELD )
    private final Date lastUpdated;

    private final List<CaptureTargetResource> targets;

    // used for gson deserialization
    @SuppressWarnings( "unused" )
    private CaptureSessionResource()
    {
        this.user = null;
        this.started = null;
        this.lastUpdated = null;
        this.buildTag = null;
        this.targets = null;
        this.url = null;
    }

    public CaptureSessionResource( final String user, final String buildTag, final Date started,
                                   final Date lastUpdated, final List<CaptureTargetResource> targets,
                                   final String applicationUrl )
    {
        this.user = user;
        this.buildTag = buildTag;
        this.started = started;
        this.lastUpdated = lastUpdated;
        this.targets = targets;

        this.url = buildResourceUri( applicationUrl, user, buildTag, started );
    }

    public Date getStartDate()
    {
        return started;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public List<CaptureTargetResource> getTargets()
    {
        return targets;
    }

    public String getUser()
    {
        return user;
    }

    public String getBuildTag()
    {
        return buildTag;
    }

    public String getUrl()
    {
        return url;
    }

    static String buildResourceUri( final String applicationUrl, final String user, final String buildTag,
                                    final Date date )
    {
        return applicationUrl == null ? null : buildUri( applicationUrl, ProtocolConstants.LOG_RESOURCE_BASEURI, user,
                                                         buildTag, formatUrlDate( date ) );

    }

}
