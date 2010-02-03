package com.redhat.jcasey.test.nexus.plugin.repo;

import java.io.IOException;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonatype.configuration.ConfigurationException;
import org.sonatype.nexus.configuration.model.CRepository;
import org.sonatype.nexus.configuration.model.CRepositoryCoreConfiguration;
import org.sonatype.nexus.configuration.model.CRepositoryExternalConfigurationHolderFactory;
import org.sonatype.nexus.configuration.model.DefaultCRepository;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.maven.RepositoryPolicy;
import org.sonatype.nexus.proxy.maven.maven2.Maven2ContentClass;
import org.sonatype.nexus.proxy.repository.GroupRepository;
import org.sonatype.nexus.proxy.repository.RepositoryWritePolicy;
import org.sonatype.nexus.templates.repository.AbstractRepositoryTemplate;
import org.sonatype.nexus.templates.repository.AbstractRepositoryTemplateProvider;

public class CaptureM2GroupRepositoryTemplate
    extends AbstractRepositoryTemplate
{

    private RepositoryPolicy repositoryPolicy;

    public CaptureM2GroupRepositoryTemplate( final AbstractRepositoryTemplateProvider provider, final String id,
                                             final String description )
    {
        super( provider, id, description, new Maven2ContentClass(), CaptureM2GroupRepository.class );
        this.repositoryPolicy = RepositoryPolicy.MIXED;
    }

    @Override
    protected CRepositoryCoreConfiguration initCoreConfiguration()
    {
        // TODO: FIX THIS, IT'S A COPY FROM M2GroupRepositoryTemplate!!!

        final CRepository repo = new DefaultCRepository();

        repo.setId( "" );
        repo.setName( "" );

        repo.setProviderRole( GroupRepository.class.getName() );
        repo.setProviderHint( "m2Capture" );

        final Xpp3Dom ex = new Xpp3Dom( DefaultCRepository.EXTERNAL_CONFIGURATION_NODE_NAME );
        repo.setExternalConfiguration( ex );

        final CaptureM2GroupRepositoryConfiguration exConf = new CaptureM2GroupRepositoryConfiguration( ex );
        repo.externalConfigurationImple = exConf;

        repo.setWritePolicy( RepositoryWritePolicy.READ_ONLY.name() );

        final CRepositoryCoreConfiguration result =
            new CRepositoryCoreConfiguration(
                                              getTemplateProvider().getApplicationConfiguration(),
                                              repo,
                                              new CRepositoryExternalConfigurationHolderFactory<CaptureM2GroupRepositoryConfiguration>()
                                              {
                                                  public CaptureM2GroupRepositoryConfiguration createExternalConfigurationHolder(
                                                                                                                                  final CRepository config )
                                                  {
                                                      return new CaptureM2GroupRepositoryConfiguration(
                                                                                                        (Xpp3Dom) config.getExternalConfiguration() );
                                                  }
                                              } );

        return result;
    }

    // ============================================================================================
    // THE FOLLOWING WAS REPLICATED FROM:
    //
    // org.sonatype.nexus.templates.repository.maven.AbstractMavenRepositoryTemplate
    //
    // Due to constructor that locked the template into use by DefaultRepositoryTemplateProvider.
    // ============================================================================================
    @Override
    public boolean targetFits( final Object clazz )
    {
        return super.targetFits( clazz ) || clazz.equals( getRepositoryPolicy() );
    }

    public RepositoryPolicy getRepositoryPolicy()
    {
        return repositoryPolicy;
    }

    public void setRepositoryPolicy( final RepositoryPolicy repositoryPolicy )
    {
        this.repositoryPolicy = repositoryPolicy;
    }

    @Override
    public MavenRepository create()
        throws ConfigurationException, IOException
    {
        final MavenRepository mavenRepository = (MavenRepository) super.create();

        // huh? see initConfig classes
        if ( getRepositoryPolicy() != null )
        {
            mavenRepository.setRepositoryPolicy( getRepositoryPolicy() );
        }

        return mavenRepository;
    }
}
