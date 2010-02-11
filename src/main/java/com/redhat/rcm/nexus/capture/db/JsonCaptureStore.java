package com.redhat.rcm.nexus.capture.db;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.configuration.application.ApplicationConfiguration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component( role = CaptureStore.class, hint = "json" )
public class JsonCaptureStore
    implements CaptureStore
{

    private static final char PS = File.separatorChar;

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Requirement
    private ApplicationConfiguration applicationConfiguration;

    private final Map<String, CaptureSession> sessions = new HashMap<String, CaptureSession>();

    public void record( final String user, final String buildTag, final String captureSource,
                        final List<String> processedRepositories, final String path, final boolean resolved )
        throws CaptureStoreException
    {
        CaptureSession session = sessions.get( user + buildTag + captureSource );
        if ( session == null )
        {
            session = new CaptureSession( user, buildTag, captureSource );
            sessions.put( user + buildTag + captureSource, session );
        }

        session.add( new CaptureRecord( processedRepositories, path, resolved ) );
        output( session );
    }

    private void output( final CaptureSession session )
        throws CaptureStoreException
    {
        final String date = new SimpleDateFormat( "yyyy-MM-dd" ).format( new Date() );

        final String filename = String.format( "%s-%s.json", session.getBuildTag(), date );

        File sessionFile;
        try
        {
            sessionFile =
                join( applicationConfiguration.getWorkingDirectory().getCanonicalFile(), "capture-sessions",
                      session.getUser(), filename );
        }
        catch ( final IOException e )
        {
            throw new CaptureStoreException( "Failed to get canonical file for: {0}\nReason: {1}",
                                             e,
                                             applicationConfiguration.getWorkingDirectory(),
                                             e.getMessage() );
        }

        if ( !sessionFile.getParentFile().isDirectory() && !sessionFile.getParentFile().mkdirs() )
        {
            throw new CaptureStoreException( "Cannot log capture-session to disk. Failed to create storage directory: {0}.",
                                             sessionFile.getParentFile() );
        }

        FileWriter writer = null;
        try
        {
            writer = new FileWriter( sessionFile );
            writer.write( toGson( session ) );

            logger.info( "Wrote session to: " + sessionFile.getAbsolutePath() );
        }
        catch ( final IOException e )
        {
            throw new CaptureStoreException( "Failed to write capture-session to disk."
                                                             + "\nUser: {0}"
                                                             + "\nBuild-Tag: {1}\nCapture-Source: {2}\nFile Path: {3}\nReason: {4}",
                                             e,
                                             session.getUser(),
                                             session.getBuildTag(),
                                             session.getCaptureSource(),
                                             sessionFile.getAbsolutePath(),
                                             e.getMessage() );
        }
        finally
        {
            if ( writer != null )
            {
                try
                {
                    writer.close();
                }
                catch ( final IOException e )
                {
                }
            }
        }
    }

    private String toGson( final CaptureSession session )
    {
        final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat( "yyyy-MM-dd HH:mm:ss" ).create();

        return gson.toJson( session );
    }

    private File join( final File dir, final String... parts )
    {
        logger.info( "Creating file path, starting with directory: " + dir );

        final StringBuilder builder = new StringBuilder();
        for ( final String part : parts )
        {
            logger.info( "Adding path part: " + part );
            if ( builder.length() > 0 )
            {
                if ( part.length() > 0 && part.charAt( 0 ) != PS )
                {
                    builder.append( PS );
                }

                builder.append( part );
            }
            else if ( part.length() > 0 && part.charAt( 0 ) == PS )
            {
                builder.append( part.substring( 1 ) );
            }
            else
            {
                builder.append( part );
            }

            logger.info( "So far: '" + builder.toString() + "'" );
        }

        final File f = new File( dir, builder.toString() );

        logger.info( "Returning file: '" + f + "'" );

        return f;
    }

}
