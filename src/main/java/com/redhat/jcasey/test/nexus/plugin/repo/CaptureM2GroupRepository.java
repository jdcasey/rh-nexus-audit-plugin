package com.redhat.jcasey.test.nexus.plugin.repo;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonatype.nexus.artifact.GavCalculator;
import org.sonatype.nexus.configuration.Configurator;
import org.sonatype.nexus.configuration.model.CRepository;
import org.sonatype.nexus.configuration.model.CRepositoryExternalConfigurationHolderFactory;
import org.sonatype.nexus.proxy.maven.AbstractMavenGroupRepository;
import org.sonatype.nexus.proxy.registry.ContentClass;
import org.sonatype.nexus.proxy.repository.GroupRepository;
import org.sonatype.nexus.proxy.repository.Repository;

@Component( role = GroupRepository.class, hint = "m2Capture", instantiationStrategy = "per-lookup", description = "Maven2 Capturing Repository Group" )
public class CaptureM2GroupRepository
    extends AbstractMavenGroupRepository
{

    /**
     * The GAV Calculator.
     */
    @Requirement( hint = "maven2" )
    private GavCalculator gavCalculator;

    /**
     * Content class.
     */
    @Requirement( hint = "maven2" )
    private ContentClass contentClass;

    @Requirement
    private CaptureGroupRepositoryConfigurator configurator;

    @Override
    protected CaptureM2GroupRepositoryConfiguration getExternalConfiguration( final boolean forWrite )
    {
        return (CaptureM2GroupRepositoryConfiguration) super.getExternalConfiguration( forWrite );
    }

    @Override
    protected Configurator getConfigurator()
    {
        return configurator;
    }

    @Override
    public List<Repository> getMemberRepositories()
    {
        final ArrayList<Repository> result = new ArrayList<Repository>();

        final List<Repository> builtIns = super.getMemberRepositories();
        if ( builtIns != null && !builtIns.isEmpty() )
        {
            result.addAll( builtIns );
        }

        // TODO: Find a way to specify which capture-source repository or group to use!

        return result;
    }

    @Override
    protected CRepositoryExternalConfigurationHolderFactory<CaptureM2GroupRepositoryConfiguration> getExternalConfigurationHolderFactory()
    {
        return new CRepositoryExternalConfigurationHolderFactory<CaptureM2GroupRepositoryConfiguration>()
        {
            public CaptureM2GroupRepositoryConfiguration createExternalConfigurationHolder( final CRepository config )
            {
                return new CaptureM2GroupRepositoryConfiguration( (Xpp3Dom) config.getExternalConfiguration() );
            }
        };
    }

    public GavCalculator getGavCalculator()
    {
        return gavCalculator;
    }

    public ContentClass getRepositoryContentClass()
    {
        return contentClass;
    }

}
