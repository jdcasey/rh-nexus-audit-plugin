package com.redhat.rcm.nexus.capture.store;

import static com.redhat.rcm.nexus.capture.model.CaptureSession.key;
import static com.redhat.rcm.nexus.capture.serialize.SerializationUtils.getGson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.artifact.GavCalculator;
import org.sonatype.nexus.artifact.IllegalArtifactCoordinateException;
import org.sonatype.nexus.configuration.application.ApplicationConfiguration;
import org.sonatype.nexus.proxy.item.StorageItem;

import com.google.gson.reflect.TypeToken;
import com.redhat.rcm.nexus.capture.model.CaptureSession;
import com.redhat.rcm.nexus.capture.model.CaptureSessionCatalog;
import com.redhat.rcm.nexus.capture.model.CaptureTarget;
import com.redhat.rcm.nexus.capture.serialize.SerializationConstants;

@Component( role = CaptureStore.class, hint = "json" )
public class JsonCaptureStore
    implements CaptureStore, Initializable
{

    private static final char PS = File.separatorChar;

    private static final TypeToken<Set<CaptureSessionCatalog>> CATALOG_SET_TYPE_TOKEN =
        new TypeToken<Set<CaptureSessionCatalog>>()
        {
        };

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Requirement
    private ApplicationConfiguration applicationConfiguration;

    @Requirement( hint = "maven2" )
    GavCalculator gavCalculator;

    private final Map<String, CaptureSession> sessions = new HashMap<String, CaptureSession>();

    private final Map<String, CaptureSessionCatalog> catalogs = new HashMap<String, CaptureSessionCatalog>();

    private File workDir;

    public CaptureSession closeCurrentLog( final String user, final String buildTag, final String captureSource )
        throws CaptureStoreException
    {
        final CaptureSession session = sessions.remove( key( buildTag, captureSource, user ) );
        return session;
    }

    public void expireLogs( final String user, final String buildTag, final String captureSource, final Date olderThan )
        throws CaptureStoreException
    {
        final CaptureSessionCatalog catalog = catalogs.get( CaptureSession.key( buildTag, captureSource, user ) );
        if ( catalog != null )
        {
            final TreeMap<Date, File> sessions = catalog.getSessions();
            if ( sessions != null && !sessions.isEmpty() )
            {
                for ( final Date d : new HashSet<Date>( sessions.keySet() ) )
                {
                    if ( !d.after( olderThan ) )
                    {
                        sessions.remove( d );
                    }
                }

                writeCatalogs();
            }
        }
    }

    public List<Date> getLogs( final String user, final String buildTag, final String captureSource )
        throws CaptureStoreException
    {
        final CaptureSessionCatalog catalog = catalogs.get( CaptureSession.key( buildTag, captureSource, user ) );
        if ( catalog != null )
        {
            final TreeMap<Date, File> sessions = catalog.getSessions();
            if ( sessions != null && !sessions.isEmpty() )
            {
                return new ArrayList<Date>( sessions.keySet() );
            }
        }

        return null;
    }

    public CaptureSession readLog( final String user, final String buildTag, final String captureSource,
                                   final Date startDate )
        throws CaptureStoreException
    {
        final CaptureSessionCatalog catalog = catalogs.get( CaptureSession.key( buildTag, captureSource, user ) );
        if ( catalog != null )
        {
            final TreeMap<Date, File> sessions = catalog.getSessions();
            final File f = sessions.get( startDate );
            if ( f != null && f.exists() )
            {
                return readSession( f );
            }
        }

        return null;
    }

    public CaptureSession readLatestLog( final String user, final String buildTag, final String captureSource )
        throws CaptureStoreException
    {
        CaptureSession session = getSession( user, buildTag, captureSource, false );
        if ( session == null )
        {
            final CaptureSessionCatalog catalog = catalogs.get( CaptureSession.key( buildTag, captureSource, user ) );
            if ( catalog != null )
            {
                final TreeMap<Date, File> sessions = catalog.getSessions();
                if ( !sessions.isEmpty() )
                {
                    final LinkedList<Date> dates = new LinkedList<Date>( sessions.keySet() );

                    final File f = sessions.get( dates.getLast() );

                    session = readSession( f );
                }
            }
        }

        return session;
    }

    private CaptureSession readSession( final File sessionFile )
        throws CaptureStoreException
    {
        FileReader reader = null;
        try
        {
            reader = new FileReader( sessionFile );

            return getGson().fromJson( reader, CaptureSession.class );
        }
        catch ( final IOException e )
        {
            throw new CaptureStoreException( "Failed to read capture-session from disk."
                            + "\nFile Path: {0}\nReason: {1}", e, sessionFile.getAbsolutePath(), e.getMessage() );
        }
        finally
        {
            IOUtil.close( reader );
        }
    }

    public void logResolved( final String user, final String buildTag, final String captureSource,
                             final List<String> processedRepositories, final String path, final StorageItem item )
        throws CaptureStoreException
    {
        final CaptureSession session = getSession( user, buildTag, captureSource, true );

        final Gav gav = toGav( path );
        session.add( new CaptureTarget( processedRepositories, path, gav, item ) );
        output( session );
    }

    public void logUnresolved( final String user, final String buildTag, final String captureSource,
                               final List<String> processedRepositories, final String path )
        throws CaptureStoreException
    {
        final CaptureSession session = getSession( user, buildTag, captureSource, true );

        final Gav gav = toGav( path );
        session.add( new CaptureTarget( processedRepositories, path, gav ) );
        output( session );
    }

    private synchronized CaptureSession getSession( final String user, final String buildTag,
                                                    final String captureSource, final boolean create )
    {
        CaptureSession session = sessions.get( key( buildTag, captureSource, user ) );
        if ( create && session == null )
        {
            session = new CaptureSession( user, buildTag, captureSource );
            sessions.put( session.key(), session );
        }

        return session;
    }

    private Gav toGav( final String path )
    {
        Gav gav = null;
        try
        {
            gav = gavCalculator.pathToGav( path );
        }
        catch ( final IllegalArtifactCoordinateException e )
        {
            logger.error( String.format( "Cannot calculate GAV (artifact coordinate) from path: '%s'.\nReason: %s",
                                         path, e.getMessage() ), e );
        }

        return gav;
    }

    private synchronized void output( final CaptureSession session )
        throws CaptureStoreException
    {
        final File sessionFile = getSessionFile( session );

        if ( !sessionFile.getParentFile().isDirectory() && !sessionFile.getParentFile().mkdirs() )
        {
            throw new CaptureStoreException( "Cannot log capture-session to disk. Failed to create storage directory: {0}.",
                                             sessionFile.getParentFile() );
        }

        FileWriter writer = null;
        try
        {
            writer = new FileWriter( sessionFile );
            writer.write( getGson().toJson( session ) );

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
            IOUtil.close( writer );
        }

        catalog( session );
    }

    private void catalog( final CaptureSession session )
        throws CaptureStoreException
    {
        CaptureSessionCatalog catalog = catalogs.get( session.key() );
        if ( catalog == null )
        {
            catalog = new CaptureSessionCatalog( session.getBuildTag(), session.getCaptureSource(), session.getUser() );
            catalogs.put( session.key(), catalog );
        }

        catalog.add( session );

        writeCatalogs();
    }

    private void readCatalogs()
        throws IOException
    {
        final File catalogFile = new File( workDir(), SerializationConstants.CATALOG_FILENAME );
        if ( catalogFile.exists() && catalogFile.length() > 0 )
        {
            FileReader reader = null;
            try
            {
                reader = new FileReader( catalogFile );

                Set<CaptureSessionCatalog> cats = null;
                cats = getGson().fromJson( reader, CATALOG_SET_TYPE_TOKEN.getType() );

                if ( cats != null )
                {
                    for ( final CaptureSessionCatalog cat : cats )
                    {
                        catalogs.put( key( cat.getBuildTag(), cat.getCaptureSource(), cat.getUser() ), cat );
                    }
                }
            }
            finally
            {
                IOUtil.close( reader );
            }
        }
    }

    private void writeCatalogs()
        throws CaptureStoreException
    {
        final File catalogFile = new File( workDir(), SerializationConstants.CATALOG_FILENAME );
        catalogFile.getParentFile().mkdirs();

        FileWriter writer = null;
        try
        {
            writer = new FileWriter( catalogFile );
            writer.write( getGson().toJson( new HashSet<CaptureSessionCatalog>( catalogs.values() ) ) );
        }
        catch ( final IOException e )
        {
            throw new CaptureStoreException( "Failed to write capture-session catalog to disk.\nFile: {0}\nReason: {1}",
                                             e,
                                             catalogFile.getAbsolutePath(),
                                             e.getMessage() );
        }
        finally
        {
            IOUtil.close( writer );
        }
    }

    private synchronized File getSessionFile( final CaptureSession session )
        throws CaptureStoreException
    {
        File sessionFile = session.getFile();
        if ( sessionFile == null )
        {
            final String filename =
                String.format( "%1$s-%3$s-%2$tY-%2$tm-%2$td_%2$tH-%2$tM-%2$tS%2$tz.json", session.getBuildTag(),
                               session.getStartDate(), session.getCaptureSource() );

            sessionFile = join( workDir(), session.getUser(), filename );

            session.setFile( sessionFile );
        }

        return sessionFile;
    }

    private File workDir()
    {
        if ( workDir == null )
        {
            File dir;
            try
            {
                dir = applicationConfiguration.getWorkingDirectory().getCanonicalFile();
            }
            catch ( final IOException e )
            {
                dir = applicationConfiguration.getWorkingDirectory().getAbsoluteFile();
            }

            workDir = join( dir, "capture-sessions" );
        }

        return workDir;
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

    public void initialize()
        throws InitializationException
    {
        try
        {
            readCatalogs();
        }
        catch ( final IOException e )
        {
            throw new InitializationException( String.format( "Failed to read catalogs from the filesystem: %s",
                                                              e.getMessage() ), e );
        }
    }

}
