package com.redhat.tools.nexus.audit.statik;

import org.sonatype.security.realms.tools.AbstractStaticSecurityResource;
import org.sonatype.security.realms.tools.StaticSecurityResource;

import javax.inject.Named;

@Named( "auditSecurity" )
public class AuditStaticSecurityResource
    extends AbstractStaticSecurityResource
    implements StaticSecurityResource
{

    @Override
    protected String getResourcePath()
    {
        return "/META-INF/nexus/audit-security.xml";
    }

}
