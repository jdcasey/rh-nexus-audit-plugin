package com.redhat.rcm.nexus.capture;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.security.realms.tools.AbstractStaticSecurityResource;
import org.sonatype.security.realms.tools.StaticSecurityResource;

@Component( role = StaticSecurityResource.class, hint = "CaptureStaticSecurityResource" )
public class CaptureStaticSecurityResource
    extends AbstractStaticSecurityResource
{

    @Override
    protected String getResourcePath()
    {
        return "/META-INF/capture-security.xml";
    }

}
