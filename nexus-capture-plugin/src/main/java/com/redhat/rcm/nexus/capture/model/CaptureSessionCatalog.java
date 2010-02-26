package com.redhat.rcm.nexus.capture.model;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.LoggerFactory;

import com.google.gson.annotations.SerializedName;
import com.redhat.rcm.nexus.protocol.ProtocolConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( ProtocolConstants.CATALOG_ROOT )
public final class CaptureSessionCatalog
{

    @SerializedName( ProtocolConstants.BUILD_TAG_FIELD )
    @XStreamAlias( ProtocolConstants.BUILD_TAG_FIELD )
    private String buildTag;

    private String user;

    @SerializedName( ProtocolConstants.CAPTURE_SOURCE_FIELD )
    @XStreamAlias( ProtocolConstants.CAPTURE_SOURCE_FIELD )
    private String captureSource;

    private final TreeMap<Date, File> sessions = new TreeMap<Date, File>();

    // For Gson serialization...
    @SuppressWarnings( "unused" )
    private CaptureSessionCatalog()
    {
    }

    public CaptureSessionCatalog( final String buildTag, final String captureSource, final String user )
    {
        this.buildTag = buildTag;
        this.captureSource = captureSource;
        this.user = user;
    }

    public CaptureSessionCatalog add( final CaptureSession session )
    {
        if ( session.getFile() != null )
        {
            sessions.put( session.getStartDate(), session.getFile() );
        }
        else
        {
            LoggerFactory.getLogger( getClass() )
                         .warn(
                                String.format( "Session: %s has no associated file. NOT adding to catalog.",
                                               session.key() ) );
        }

        return this;
    }

    public CaptureSessionCatalog remove( final Date sessionDate )
    {
        final File removed = sessions.remove( sessionDate );
        if ( removed.exists() )
        {
            removed.delete();
        }

        return this;
    }

    public CaptureSessionCatalog remove( final File sessionFile )
    {
        Date d = null;
        for ( final Map.Entry<Date, File> entry : sessions.entrySet() )
        {
            if ( entry.getValue().equals( sessionFile ) )
            {
                d = entry.getKey();
                break;
            }
        }

        if ( d != null )
        {
            sessions.remove( d );
        }
        return this;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( buildTag == null ) ? 0 : buildTag.hashCode() );
        result = prime * result + ( ( captureSource == null ) ? 0 : captureSource.hashCode() );
        result = prime * result + ( ( user == null ) ? 0 : user.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final CaptureSessionCatalog other = (CaptureSessionCatalog) obj;
        if ( buildTag == null )
        {
            if ( other.buildTag != null )
            {
                return false;
            }
        }
        else if ( !buildTag.equals( other.buildTag ) )
        {
            return false;
        }
        if ( captureSource == null )
        {
            if ( other.captureSource != null )
            {
                return false;
            }
        }
        else if ( !captureSource.equals( other.captureSource ) )
        {
            return false;
        }
        if ( user == null )
        {
            if ( other.user != null )
            {
                return false;
            }
        }
        else if ( !user.equals( other.user ) )
        {
            return false;
        }
        return true;
    }

    public String getBuildTag()
    {
        return buildTag;
    }

    public String getUser()
    {
        return user;
    }

    public String getCaptureSource()
    {
        return captureSource;
    }

    public TreeMap<Date, File> getSessions()
    {
        return sessions;
    }

    public Date getEarliest()
    {
        return sessions.isEmpty() ? null : sessions.keySet().iterator().next();
    }

    public Date getLatest()
    {
        return sessions.isEmpty() ? null : new LinkedList<Date>( sessions.keySet() ).getLast();
    }
}
