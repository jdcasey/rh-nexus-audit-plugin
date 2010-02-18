package com.redhat.rcm.nexus.capture.model;

import static org.codehaus.plexus.util.StringUtils.isNotEmpty;

import java.util.Date;

public class CaptureSessionQuery
{

    public static enum QueryMode
    {
        ALL_MATCHING,
        FIRST_MATCHING;
    }

    private final QueryMode mode = QueryMode.FIRST_MATCHING;

    private String user;

    private String buildTag;

    private Date before;

    private Date since;

    public boolean matches( final CaptureSessionCatalog catalog )
    {
        if ( isNotEmpty( user ) && !user.equals( catalog.getUser() ) )
        {
            return false;
        }

        if ( isNotEmpty( buildTag ) && !buildTag.equals( catalog.getBuildTag() ) )
        {
            return false;
        }

        if ( before != null && catalog.getEarliest() != null && !catalog.getEarliest().before( before ) )
        {
            return false;
        }

        if ( since != null && catalog.getLatest() != null && !catalog.getLatest().after( since ) )
        {
            return false;
        }

        return true;
    }

    public boolean matches( final CaptureSession session )
    {
        if ( isNotEmpty( user ) && !user.equals( session.getUser() ) )
        {
            return false;
        }

        if ( isNotEmpty( buildTag ) && !buildTag.equals( session.getBuildTag() ) )
        {
            return false;
        }

        if ( before != null && !session.getStartDate().before( before ) )
        {
            return false;
        }

        if ( since != null && !session.getStartDate().after( since ) )
        {
            return false;
        }

        return true;
    }

    public boolean matches( final Date sessionStartDate )
    {
        if ( before != null && !sessionStartDate.before( before ) )
        {
            return false;
        }

        if ( since != null && !sessionStartDate.after( since ) )
        {
            return false;
        }

        return true;
    }

    public String getUser()
    {
        return user;
    }

    public CaptureSessionQuery setUser( final String user )
    {
        this.user = user;
        return this;
    }

    public String getBuildTag()
    {
        return buildTag;
    }

    public CaptureSessionQuery setBuildTag( final String buildTag )
    {
        this.buildTag = buildTag;
        return this;
    }

    public Date getBefore()
    {
        return before;
    }

    public CaptureSessionQuery setBefore( final Date before )
    {
        this.before = before;
        return this;
    }

    public Date getSince()
    {
        return since;
    }

    public CaptureSessionQuery setSince( final Date since )
    {
        this.since = since;
        return this;
    }

    public QueryMode getMode()
    {
        return mode;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append( "Query: {" );
        if ( isNotEmpty( user ) )
        {
            sb.append( "user=" ).append( user );
        }

        if ( isNotEmpty( buildTag ) )
        {
            sb.append( "build-tag=" ).append( buildTag );
        }

        if ( before != null )
        {
            sb.append( "starting-before=" ).append( before );
        }

        if ( since != null )
        {
            sb.append( "starting-after=" ).append( since );
        }

        return sb.toString();
    }

}
