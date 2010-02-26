package com.redhat.rcm.nexus.capture.template;

import java.util.Map;

import javax.inject.Singleton;

import org.sonatype.plugin.Managed;

@Managed
@Singleton
public interface TemplateFormatter
{

    String format( final String templateBasepath, final String templateName, final Map<String, Object> context )
        throws TemplateException;

}
