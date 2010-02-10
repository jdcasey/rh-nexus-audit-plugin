package com.redhat.jcasey.test.nexus.plugin.rest;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.security.realms.tools.AbstractStaticSecurityResource;
import org.sonatype.security.realms.tools.StaticSecurityResource;

@Component( role = StaticSecurityResource.class, hint = "CaptureSecurityResource" )
public class CaptureSecurityResource
    extends AbstractStaticSecurityResource
{

    @Override
    protected String getResourcePath()
    {
        return "/META-INF/capture-security.xml";
    }

}
