package com.redhat.tools.nexus.response.template;

import javax.inject.Singleton;

import java.util.Map;

@Singleton
public interface TemplateFormatter
{

    String TEMPLATES_ROOT = "templates/";

    String format( final String templateBasepath, final String templateName, final Map<String, Object> context )
        throws TemplateException;

}
