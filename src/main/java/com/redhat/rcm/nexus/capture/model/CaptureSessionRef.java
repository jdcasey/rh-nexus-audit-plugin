package com.redhat.rcm.nexus.capture.model;

import static com.redhat.rcm.nexus.capture.request.RequestUtils.formatUrlDate;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.redhat.rcm.nexus.capture.serialize.SerializationConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( SerializationConstants.SESSION_REF_ROOT )
public class CaptureSessionRef
    implements Comparable<CaptureSessionRef>
{

    private final String user;

    @SerializedName( SerializationConstants.BUILD_TAG_FIELD )
    @XStreamAlias( SerializationConstants.BUILD_TAG_FIELD )
    private final String buildTag;

    @SerializedName( SerializationConstants.CAPTURE_SOURCE_FIELD )
    @XStreamAlias( SerializationConstants.CAPTURE_SOURCE_FIELD )
    private final String captureSource;

    @SerializedName( SerializationConstants.DATE_FIELD )
    @XStreamAlias( SerializationConstants.DATE_FIELD )
    private final Date date;

    private String url;

    // used for gson deserialization
    @SuppressWarnings( "unused" )
    private CaptureSessionRef()
    {
        this.user = null;
        this.buildTag = null;
        this.captureSource = null;
        this.date = null;
    }

    public CaptureSessionRef( final String user, final String buildTag, final String captureSource, final Date date )
    {
        this.user = user;
        this.buildTag = buildTag;
        this.captureSource = captureSource;
        this.date = date;
    }

    public CaptureSessionRef( final String user, final String buildTag, final String captureSource, final Date date,
                              final String baseUrl )
    {
        this.user = user;
        this.buildTag = buildTag;
        this.captureSource = captureSource;
        this.date = date;

        configureUrl( baseUrl );
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

    public Date getDate()
    {
        return date;
    }

    public String getUrl()
    {
        return url;
    }

    public void configureUrl( final String baseUrl )
    {
        final StringBuilder sb = new StringBuilder();

        sb.append( baseUrl );

        if ( !baseUrl.endsWith( "/" ) )
        {
            sb.append( "/" );
        }

        sb.append( user )
          .append( '/' )
          .append( buildTag )
          .append( '/' )
          .append( captureSource )
          .append( '/' )
          .append( formatUrlDate( date ) );

        url = sb.toString();
    }

    public int compareTo( final CaptureSessionRef ref )
    {
        return date.compareTo( ref.date );
    }

    public String key()
    {
        return CaptureSession.key( buildTag, captureSource, user );
    }

}
