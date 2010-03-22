package com.redhat.tools.nexus.audit.statik;

import org.sonatype.nexus.plugins.rest.AbstractNexusResourceBundle;
import org.sonatype.nexus.plugins.rest.DefaultStaticResource;
import org.sonatype.nexus.plugins.rest.NexusResourceBundle;
import org.sonatype.nexus.plugins.rest.StaticResource;

import java.util.ArrayList;
import java.util.List;

public class AuditResourceBundle
    extends AbstractNexusResourceBundle
    implements NexusResourceBundle
{
    @Override
    public List<StaticResource> getContributedResouces()
    {
        final List<StaticResource> result = new ArrayList<StaticResource>();

        result.add( new DefaultStaticResource( getClass().getResource( "/static/js/nexus-audit-plugin-all.js" ),
                                               "/js/repoServer/nexus-audit-plugin-all.js", "application/x-javascript" ) );

        return result;
    }

}
