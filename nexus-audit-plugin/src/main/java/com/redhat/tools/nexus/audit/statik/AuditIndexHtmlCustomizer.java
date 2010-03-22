package com.redhat.tools.nexus.audit.statik;

import org.sonatype.nexus.plugins.rest.AbstractNexusIndexHtmlCustomizer;
import org.sonatype.nexus.plugins.rest.NexusIndexHtmlCustomizer;

import java.util.Map;

public class AuditIndexHtmlCustomizer
    extends AbstractNexusIndexHtmlCustomizer
    implements NexusIndexHtmlCustomizer
{
    @Override
    public String getPostHeadContribution( final Map<String, Object> ctx )
    {
        final String version =
            getVersionFromJarFile( "/META-INF/maven/com.redhat.tools.nexus/nexus-audit-plugin/pom.properties" );

        return "<script src=\"js/repoServer/nexus-audit-plugin-all.js" + ( version == null ? "" : "?" + version )
            + "\" type=\"text/javascript\" charset=\"utf-8\"></script>";

        // return "<script src=\"js/repoServer/repoServer.CaptureConfigPanel.js"
        // + "\" type=\"text/javascript\" charset=\"utf-8\"></script>";
    }
}
