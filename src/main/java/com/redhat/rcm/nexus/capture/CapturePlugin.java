package com.redhat.rcm.nexus.capture;

import org.sonatype.nexus.plugins.NexusPlugin;
import org.sonatype.nexus.plugins.PluginContext;

public class CapturePlugin
    implements NexusPlugin
{

    public void init( final PluginContext context )
    {
        System.out.println( "\n\n\n\nINIT!\n\n\n\n" );
    }

    public void install( final PluginContext context )
    {
        System.out.println( "\n\n\n\nINSTALL!\n\n\n\n" );
    }

    public void uninstall( final PluginContext context )
    {
        System.out.println( "\n\n\n\nUNINSTALL!\n\n\n\n" );
    }

}
