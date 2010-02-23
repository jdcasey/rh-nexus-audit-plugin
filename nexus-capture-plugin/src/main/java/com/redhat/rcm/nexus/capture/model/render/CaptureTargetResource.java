package com.redhat.rcm.nexus.capture.model.render;

import static com.redhat.rcm.nexus.capture.request.RequestUtils.buildUri;

import java.util.Date;
import java.util.List;

import org.sonatype.nexus.artifact.Gav;

import com.google.gson.annotations.SerializedName;
import com.redhat.rcm.nexus.capture.CaptureResourceConstants;
import com.redhat.rcm.nexus.capture.model.CaptureTarget;
import com.redhat.rcm.nexus.capture.serialize.SerializationConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( SerializationConstants.TARGET_ROOT )
public class CaptureTargetResource
{

    @SerializedName( SerializationConstants.RESOURCE_URI_FIELD )
    @XStreamAlias( SerializationConstants.RESOURCE_URI_FIELD )
    private final String url;

    private final String path;

    private boolean resolved = false;

    @SerializedName( SerializationConstants.RESOLVED_REPO_FIELD )
    @XStreamAlias( SerializationConstants.RESOLVED_REPO_FIELD )
    private final String repositoryId;

    @SerializedName( SerializationConstants.RESOLVED_ON_FIELD )
    @XStreamAlias( SerializationConstants.RESOLVED_ON_FIELD )
    private Date resolutionDate;

    private final Gav coordinate;

    @SerializedName( SerializationConstants.CHECKED_REPOS_FIELD )
    @XStreamAlias( SerializationConstants.CHECKED_REPOS_FIELD )
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

    public CaptureTargetResource( final CaptureTarget target, final String applicationUrl )
    {
        this.coordinate = target.getCoordinate();
        this.processedRepositories = target.getProcessedRepositories();
        this.path = target.getPath();
        this.resolved = target.isResolved();
        this.resolutionDate = target.getResolutionDate();
        this.repositoryId = target.getRepositoryId();

        this.url =
            buildUri( applicationUrl, CaptureResourceConstants.REPOSITORY_RESOURCE_BASEURI, repositoryId,
                      CaptureResourceConstants.REPOSITORY_CONTENT_URLPART, path );
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
