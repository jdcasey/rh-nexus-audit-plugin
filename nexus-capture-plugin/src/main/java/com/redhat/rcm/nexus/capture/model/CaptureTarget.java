package com.redhat.rcm.nexus.capture.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.proxy.NoSuchRepositoryException;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.repository.ProxyRepository;

import com.google.gson.annotations.SerializedName;
import com.redhat.rcm.nexus.protocol.CaptureTargetResource;
import com.redhat.rcm.nexus.protocol.ProtocolConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( ProtocolConstants.TARGET_ROOT )
public class CaptureTarget
{

    private transient final Logger logger = LoggerFactory.getLogger( getClass() );

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

    public CaptureTargetResource asResource( final String appUrl, final RepositoryRegistry repositoryRegistry )
    {
        String remoteBase = null;
        if ( resolved )
        {
            try
            {
                final ProxyRepository repository =
                    repositoryRegistry.getRepositoryWithFacet( repositoryId, ProxyRepository.class );

                remoteBase = repository.getRemoteUrl();
            }
            catch ( final NoSuchRepositoryException e )
            {
                logger.warn( String.format(
                                            "Cannot find proxy repository for target. Target may be resolved from a hosted repository."
                                                            + "\nRepository ID: %s\nTarget path: %s", repositoryId,
                                            path ) );
            }
        }

        return new CaptureTargetResource( coordinate,
                                          path,
                                          repositoryId,
                                          resolutionDate,
                                          processedRepositories,
                                          resolved,
                                          appUrl,
                                          remoteBase );
    }

}
