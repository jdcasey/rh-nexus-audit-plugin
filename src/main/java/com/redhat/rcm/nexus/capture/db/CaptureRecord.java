package com.redhat.rcm.nexus.capture.db;

import java.util.Date;
import java.util.List;

public class CaptureRecord
{

    private final List<String> processedRepositories;

    private final String path;

    private boolean resolved = false;

    private Date resolutionDate;

    // Used for Gson deserialization.
    @SuppressWarnings( "unused" )
    private CaptureRecord()
    {
        this.processedRepositories = null;
        this.path = null;
        this.resolved = false;
    }

    public CaptureRecord( final List<String> processedRepositories, final String path, final boolean resolved )
    {
        this.processedRepositories = processedRepositories;
        this.path = path;
        this.resolved = resolved;
        this.resolutionDate = new Date();
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

}
