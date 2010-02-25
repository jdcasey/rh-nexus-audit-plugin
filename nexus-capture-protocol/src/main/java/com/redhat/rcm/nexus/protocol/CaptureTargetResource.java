package com.redhat.rcm.nexus.protocol;

import static com.redhat.rcm.nexus.protocol.ProtocolUtils.buildUri;

import java.util.Date;
import java.util.List;

import org.sonatype.nexus.artifact.Gav;

import com.google.gson.annotations.SerializedName;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( ProtocolConstants.TARGET_ROOT )
public class CaptureTargetResource
{

    @SerializedName( ProtocolConstants.RESOURCE_URI_FIELD )
    @XStreamAlias( ProtocolConstants.RESOURCE_URI_FIELD )
    private final String url;

    private final String path;

    private boolean resolved = false;

    @SerializedName( ProtocolConstants.RESOLVED_REPO_FIELD )
    @XStreamAlias( ProtocolConstants.RESOLVED_REPO_FIELD )
    private final String repositoryId;

    @SerializedName( ProtocolConstants.RESOLVED_ON_FIELD )
    @XStreamAlias( ProtocolConstants.RESOLVED_ON_FIELD )
    private Date resolutionDate;

    private final Gav coordinate;

    @SerializedName( ProtocolConstants.CHECKED_REPOS_FIELD )
    @XStreamAlias( ProtocolConstants.CHECKED_REPOS_FIELD )
    private final List<String> processedRepositories;

    // Used for Gson deserialization.
    @SuppressWarnings( "unused" )
    private CaptureTargetResource()
    {
        this.processedRepositories = null;
        this.coordinate = null;
        this.path = null;
        this.resolved = false;
        this.repositoryId = null;
        this.url = null;
    }

    public CaptureTargetResource( final Gav coordinate, final String path, final String repositoryId,
                                  final Date resolutionDate, final List<String> processedRepositories,
                                  final boolean resolved, final String applicationUrl )
    {

        this.coordinate = coordinate;
        this.path = path;
        this.repositoryId = repositoryId;
        this.resolutionDate = resolutionDate;
        this.processedRepositories = processedRepositories;
        this.resolved = resolved;

        this.url =
            buildUri( applicationUrl, ProtocolConstants.REPOSITORY_RESOURCE_BASEURI, repositoryId,
                      ProtocolConstants.REPOSITORY_CONTENT_URLPART, path );
    }

    public Date getResolutionDate()
    {
        return resolutionDate;
    }

    public List<String> getProcessedRepositories()
    {
        return processedRepositories;
    }

    public String getPath()
    {
        return path;
    }

    public boolean isResolved()
    {
        return resolved;
    }

    public Gav getCoordinate()
    {
        return coordinate;
    }

    public String getRepositoryId()
    {
        return repositoryId;
    }

    public String getUrl()
    {
        return url;
    }

}
