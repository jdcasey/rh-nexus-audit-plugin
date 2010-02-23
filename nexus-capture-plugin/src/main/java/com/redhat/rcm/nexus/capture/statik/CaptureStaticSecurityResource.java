package com.redhat.rcm.nexus.capture.statik;

import javax.inject.Named;

import org.sonatype.security.realms.tools.AbstractStaticSecurityResource;
import org.sonatype.security.realms.tools.StaticSecurityResource;

@Named( "captureSecurity" )
public class CaptureStaticSecurityResource
    extends AbstractStaticSecurityResource
    implements StaticSecurityResource
{

    @Override
    protected String getResourcePath()
    {
        return "/META-INF/capture-security.xml";
    }

}
