package com.redhat.jcasey.test.nexus.plugin;

import javax.inject.Named;

import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.nexus.plugins.NexusPlugin;
import org.sonatype.nexus.plugins.PluginContext;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.registry.RepositoryTypeDescriptor;
import org.sonatype.nexus.proxy.registry.RepositoryTypeRegistry;

import com.redhat.jcasey.test.nexus.plugin.repo.CaptureM2GroupRepository;

@Named( "Test" )
public class TestPlugin
    implements NexusPlugin
{

    @Requirement
    private RepositoryTypeRegistry repoTypeRegistry;

    @Requirement
    private RepositoryRegistry repoRegistry;

    public void init( final PluginContext ctx )
    {
        repoTypeRegistry.registerRepositoryTypeDescriptors( new RepositoryTypeDescriptor(
                                                                                          CaptureM2GroupRepository.class.getName(),
                                                                                          "capture" ) );
    }

    public void install( final PluginContext ctx )
    {
        // TODO Auto-generated method stub
    }

    public void uninstall( final PluginContext ctx )
    {
        // TODO Auto-generated method stub

    }

}
