package com.redhat.tools.nexus.template;

import org.sonatype.plugin.Managed;

import javax.inject.Singleton;

import java.util.Map;

@Managed
@Singleton
public interface TemplateFormatter
{

    String TEMPLATES_ROOT = "templates/";

    String format( final String templateBasepath, final String templateName, final Map<String, Object> context )
        throws TemplateException;

}
