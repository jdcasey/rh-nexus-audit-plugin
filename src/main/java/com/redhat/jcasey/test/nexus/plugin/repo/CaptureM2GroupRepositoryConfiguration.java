package com.redhat.jcasey.test.nexus.plugin.repo;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonatype.nexus.proxy.maven.AbstractMavenGroupRepositoryConfiguration;

public class CaptureM2GroupRepositoryConfiguration
    extends AbstractMavenGroupRepositoryConfiguration
{
    public CaptureM2GroupRepositoryConfiguration( final Xpp3Dom configuration )
    {
        super( configuration );
    }
}
