package com.redhat.rcm.nexus.capture.model;

import static com.redhat.rcm.nexus.capture.model.ModelSerializationUtils.normalizeDate;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sonatype.nexus.proxy.registry.RepositoryRegistry;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.redhat.rcm.nexus.protocol.CaptureSessionResource;
import com.redhat.rcm.nexus.protocol.CaptureTargetResource;
import com.redhat.rcm.nexus.protocol.ProtocolConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias( ProtocolConstants.SESSION_ROOT )
public class CaptureSession
{

    @XStreamOmitField
    @Expose( deserialize = false, serialize = false )
    private transient File file;

    private final String user;

    @SerializedName( ProtocolConstants.BUILD_TAG_FIELD )
    @XStreamAlias( ProtocolConstants.BUILD_TAG_FIELD )
    private final String buildTag;

    @SerializedName( ProtocolConstants.CAPTURE_SOURCE_FIELD )
    @XStreamAlias( ProtocolConstants.CAPTURE_SOURCE_FIELD )
    private final String captureSource;

    @SerializedName( ProtocolConstants.START_DATE_FIELD )
    @XStreamAlias( ProtocolConstants.START_DATE_FIELD )
    private final Date started;

    @SerializedName( ProtocolConstants.LAST_UPDATE_FIELD )
    @XStreamAlias( ProtocolConstants.LAST_UPDATE_FIELD )
    private Date lastUpdated;

    private final List<CaptureTarget> targets;

    // used for gson deserialization
    @SuppressWarnings( "unused" )
    private CaptureSession()
    {
        this.user = null;
        this.buildTag = null;
        this.captureSource = null;
        this.targets = new ArrayList<CaptureTarget>();
        this.started = null;
        this.lastUpdated = null;
    }

    public CaptureSession( final String user, final String buildTag, final String captureSource )
    {
        this.user = user;
        this.buildTag = buildTag;
        this.captureSource = captureSource;
        this.targets = new ArrayList<CaptureTarget>();
        this.started = normalizeDate( new Date() );
        this.lastUpdated = started;
    }

    public CaptureSession add( final CaptureTarget record )
    {
        targets.add( record );
        lastUpdated = record.getResolutionDate();
        return this;
    }

    public Date getStartDate()
    {
        return started;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public List<CaptureTarget> getTargets()
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

    public File getFile()
    {
        return file;
    }

    public CaptureSession setFile( final File file )
    {
        this.file = file;
        return this;
    }

    public String key()
    {
        return key( user, buildTag );
    }

    public static String key( final String user, final String buildTag )
    {
        // NOTE: CAREFUL! This can bring down the whole house if it throws FormatException or similar...
        return String.format( "%s:%s", user, buildTag );
    }

    public CaptureSessionResource asResource( final String appUrl, final RepositoryRegistry repositoryRegistry )
    {
        final List<CaptureTargetResource> resources = new ArrayList<CaptureTargetResource>( targets.size() );
        for ( final CaptureTarget target : targets )
        {
            resources.add( target.asResource( appUrl, repositoryRegistry ) );
        }

        return new CaptureSessionResource( user, buildTag, started, lastUpdated, resources, appUrl );
    }

    public CaptureSessionRef ref()
    {
        return new CaptureSessionRef( user, buildTag, started );
    }

}
