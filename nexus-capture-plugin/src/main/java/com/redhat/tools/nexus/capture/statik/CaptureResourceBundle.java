package com.redhat.tools.nexus.capture.statik;

import java.util.ArrayList;
import java.util.List;

import org.sonatype.nexus.plugins.rest.AbstractNexusResourceBundle;
import org.sonatype.nexus.plugins.rest.DefaultStaticResource;
import org.sonatype.nexus.plugins.rest.NexusResourceBundle;
import org.sonatype.nexus.plugins.rest.StaticResource;

public class CaptureResourceBundle
    extends AbstractNexusResourceBundle
    implements NexusResourceBundle
{
    @Override
    public List<StaticResource> getContributedResouces()
    {
        final List<StaticResource> result = new ArrayList<StaticResource>();

        result.add( new DefaultStaticResource( getClass().getResource( "/static/js/nexus-capture-plugin-all.js" ),
                                               "/js/repoServer/nexus-capture-plugin-all.js",
                                               "application/x-javascript" ) );

        return result;
    }

}
