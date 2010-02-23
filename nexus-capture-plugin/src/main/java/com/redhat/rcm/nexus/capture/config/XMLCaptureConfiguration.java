package com.redhat.rcm.nexus.capture.config;

import static com.redhat.rcm.nexus.capture.serialize.SerializationUtils.getXStreamForConfig;
import static org.codehaus.plexus.util.IOUtil.close;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.configuration.application.ApplicationConfiguration;
import org.sonatype.nexus.proxy.NoSuchRepositoryException;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.repository.GroupRepository;
import org.sonatype.nexus.proxy.repository.Repository;

import edu.emory.mathcs.backport.java.util.Collections;

@Named( "xml" )
public class XMLCaptureConfiguration
    implements CaptureConfiguration, Initializable
{

    private static final String CONFIG_FILE = "capture.xml";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    private ApplicationConfiguration appConfig;

    @Inject
    @Named( "default" )
    private RepositoryRegistry unprotectedRepositoryRegistry;

    private CaptureConfigModel configModel;

    @Override
    public CaptureConfigModel getModel()
    {
        return configModel;
    }

    @Override
    public void save()
        throws InvalidConfigurationException
    {
        if ( configModel == null || !configModel.isValid() )
        {
            logger.warn( "Capture configuration is empty, and will NOT be saved." );
            return;
        }

        final File configFile = new File( appConfig.getConfigurationDirectory(), CONFIG_FILE );
        configFile.getParentFile().mkdirs();

        FileWriter writer = null;
        try
        {
            writer = new FileWriter( configFile );
            writer.write( getXStreamForConfig().toXML( configModel ) );
        }
        catch ( final IOException e )
        {
            throw new InvalidConfigurationException( "Failed to write capture configuration to file: %s\nReason: %s",
                                                     e,
                                                     configFile,
                                                     e.getMessage() );
        }
        finally
        {
            close( writer );
        }
    }

    @Override
    public void updateModel( final CaptureConfigModel model )
        throws InvalidConfigurationException
    {
        if ( model == null || !model.isValid() )
        {
            throw new InvalidConfigurationException( "Capture configuration is missing or invalid." );
        }

        this.configModel = model;
        save();
    }

    @Override
    public void initialize()
        throws InitializationException
    {
        final File configFile = new File( appConfig.getConfigurationDirectory(), CONFIG_FILE );

        if ( configFile.isFile() && configFile.canRead() )
        {
            FileReader reader = null;
            try
            {
                reader = new FileReader( configFile );
                configModel = (CaptureConfigModel) getXStreamForConfig().fromXML( reader );
            }
            catch ( final Exception e )
            {
                throw new Error( "[NEXUS-3308] FAILURE to initialize XML configuration: " + e.getMessage(), e );
            }
            finally
            {
                close( reader );
            }
        }
        else
        {
            configModel = new CaptureConfigModel();

            Repository repo = null;
            try
            {
                repo = unprotectedRepositoryRegistry.getRepositoryWithFacet( "public", GroupRepository.class );
            }
            catch ( final NoSuchRepositoryException e )
            {
                final List<GroupRepository> groups =
                    unprotectedRepositoryRegistry.getRepositoriesWithFacet( GroupRepository.class );

                List<Repository> repos;
                if ( groups != null && !groups.isEmpty() )
                {
                    repos = new ArrayList<Repository>( groups );
                }
                else
                {
                    repos = unprotectedRepositoryRegistry.getRepositories();
                }

                if ( repos != null && !repos.isEmpty() )
                {
                    Collections.sort( groups, new Comparator<Repository>()
                    {
                        @Override
                        public int compare( final Repository r1, final Repository r2 )
                        {
                            return r1.getId().compareTo( r2.getId() );
                        }
                    } );

                    repo = groups.get( 0 );
                }
            }

            if ( repo != null )
            {
                configModel.setCaptureSourceRepoId( repo.getId() );
                try
                {
                    save();
                }
                catch ( final InvalidConfigurationException e )
                {
                    throw new Error( "[NEXUS-3308] FAILURE to initialize XML configuration: " + e.getMessage(), e );
                }
            }
        }
    }

}