package com.redhat.rcm.nexus.capture.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.proxy.item.StorageItem;

import com.google.gson.annotations.SerializedName;
import com.redhat.rcm.nexus.capture.serialize.SerializationConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( SerializationConstants.TARGET_ROOT )
public class CaptureTarget
{

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
    private CaptureTarget()
    {
        this.processedRepositories = null;
        this.coordinate = null;
        this.path = null;
        this.resolved = false;
        this.repositoryId = null;
    }

    public CaptureTarget( final List<String> processedRepositories, final String path, final Gav gav,
                          final StorageItem item )
    {
        this.coordinate = gav;
        this.processedRepositories = new ArrayList<String>( processedRepositories );
        this.path = path;
        this.resolved = true;
        this.resolutionDate = new Date();
        this.repositoryId = item.getRepositoryId();
    }

    public CaptureTarget( final List<String> processedRepositories, final String path, final Gav gav )
    {
        this.coordinate = gav;
        this.processedRepositories = new ArrayList<String>( processedRepositories );
        this.path = path;
        this.resolved = false;
        this.resolutionDate = new Date();
        this.repositoryId = null;
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

}
