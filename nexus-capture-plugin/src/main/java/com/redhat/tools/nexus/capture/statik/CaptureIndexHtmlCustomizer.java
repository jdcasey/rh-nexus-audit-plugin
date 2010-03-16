package com.redhat.tools.nexus.capture.statik;

import java.util.Map;

import org.sonatype.nexus.plugins.rest.AbstractNexusIndexHtmlCustomizer;
import org.sonatype.nexus.plugins.rest.NexusIndexHtmlCustomizer;

public class CaptureIndexHtmlCustomizer
    extends AbstractNexusIndexHtmlCustomizer
    implements NexusIndexHtmlCustomizer
{
    @Override
    public String getPostHeadContribution( final Map<String, Object> ctx )
    {
        final String version =
            getVersionFromJarFile( "/META-INF/maven/com.redhat.rcm.nexus/nexus-capture-plugin/pom.properties" );

        return "<script src=\"js/repoServer/nexus-capture-plugin-all.js" + ( version == null ? "" : "?" + version )
                        + "\" type=\"text/javascript\" charset=\"utf-8\"></script>";

        // return "<script src=\"js/repoServer/repoServer.CaptureConfigPanel.js"
        // + "\" type=\"text/javascript\" charset=\"utf-8\"></script>";
    }
}
