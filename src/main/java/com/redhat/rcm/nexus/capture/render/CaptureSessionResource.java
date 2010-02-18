package com.redhat.rcm.nexus.capture.render;

import static com.redhat.rcm.nexus.capture.request.RequestUtils.buildUri;
import static com.redhat.rcm.nexus.capture.request.RequestUtils.formatUrlDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.redhat.rcm.nexus.capture.CaptureResourceConstants;
import com.redhat.rcm.nexus.capture.model.CaptureSession;
import com.redhat.rcm.nexus.capture.model.CaptureTarget;
import com.redhat.rcm.nexus.capture.serialize.SerializationConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( SerializationConstants.SESSION_ROOT )
public class CaptureSessionResource
{

    private final String user;

    @SerializedName( SerializationConstants.BUILD_TAG_FIELD )
    @XStreamAlias( SerializationConstants.BUILD_TAG_FIELD )
    private final String buildTag;

    @SerializedName( SerializationConstants.CAPTURE_SOURCE_FIELD )
    @XStreamAlias( SerializationConstants.CAPTURE_SOURCE_FIELD )
    private final String captureSource;

    @SerializedName( SerializationConstants.START_DATE_FIELD )
    @XStreamAlias( SerializationConstants.START_DATE_FIELD )
    private final Date started;

    @SerializedName( SerializationConstants.LAST_UPDATE_FIELD )
    @XStreamAlias( SerializationConstants.LAST_UPDATE_FIELD )
    private final Date lastUpdated;

    private final List<CaptureTargetResource> targets;

    private final String url;

    // used for gson deserialization
    @SuppressWarnings( "unused" )
    private CaptureSessionResource()
    {
        this.user = null;
        this.started = null;
        this.lastUpdated = null;
        this.buildTag = null;
        this.captureSource = null;
        this.targets = null;
        this.url = null;
    }

    public CaptureSessionResource( final CaptureSession session, final String applicationUrl )
    {
        this.user = session.getUser();
        this.buildTag = session.getBuildTag();
        this.captureSource = session.getCaptureSource();
        this.started = session.getStartDate();
        this.lastUpdated = session.getLastUpdated();

        final List<CaptureTarget> t = session.getTargets();
        final List<CaptureTargetResource> resources = new ArrayList<CaptureTargetResource>( t.size() );
        for ( final CaptureTarget target : t )
        {
            resources.add( new CaptureTargetResource( target, applicationUrl ) );
        }

        this.targets = resources;
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

    public String getCaptureSource()
    {
        return captureSource;
    }

    public String getUrl()
    {
        return url;
    }

    public String key()
    {
        return CaptureSession.key( buildTag, captureSource, user );
    }

    static String buildResourceUri( final String applicationUrl, final String user, final String buildTag,
                                    final Date date )
    {
        return buildUri( applicationUrl, CaptureResourceConstants.LOG_RESOURCE_BASEURI, user, buildTag,
                         formatUrlDate( date ) );

    }

}
