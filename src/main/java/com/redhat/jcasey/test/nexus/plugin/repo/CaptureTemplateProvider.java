package com.redhat.jcasey.test.nexus.plugin.repo;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.nexus.templates.TemplateProvider;
import org.sonatype.nexus.templates.TemplateSet;
import org.sonatype.nexus.templates.repository.AbstractRepositoryTemplateProvider;

@Component( role = TemplateProvider.class, hint = "capture" )
public class CaptureTemplateProvider
    extends AbstractRepositoryTemplateProvider
{

    public TemplateSet getTemplates()
    {
        final TemplateSet ts = new TemplateSet( null );

        ts.add( new CaptureM2GroupRepositoryTemplate( this, "default_capture", "Maven2 Capture Repository" ) );

        return ts;
    }

}
