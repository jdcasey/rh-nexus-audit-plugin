package com.redhat.tools.nexus.capture.config;

import javax.inject.Singleton;

import org.sonatype.plugin.Managed;

@Managed
@Singleton
public interface CaptureConfiguration
{

    void save()
        throws InvalidConfigurationException;

    void updateModel( CaptureConfigModel model )
        throws InvalidConfigurationException;

    CaptureConfigModel getModel();
}
