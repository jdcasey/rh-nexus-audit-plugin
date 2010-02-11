package com.redhat.rcm.nexus.capture.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CaptureSession
{

    private final String user;

    private final String buildTag;

    private final String captureSource;

    private final Date started = new Date();

    private Date lastUpdated = started;

    private final List<CaptureRecord> records;

    // used for gson deserialization
    @SuppressWarnings( "unused" )
    private CaptureSession()
    {
        this.user = null;
        this.buildTag = null;
        this.captureSource = null;
        this.records = new ArrayList<CaptureRecord>();
    }

    public CaptureSession( final String user, final String buildTag, final String captureSource )
    {
        this.user = user;
        this.buildTag = buildTag;
        this.captureSource = captureSource;
        this.records = new ArrayList<CaptureRecord>();
    }

    public void add( final CaptureRecord record )
    {
        records.add( record );
        lastUpdated = record.getResolutionDate();
    }

    public Date getStartDate()
    {
        return started;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public List<CaptureRecord> getRecords()
    {
        return records;
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

}
