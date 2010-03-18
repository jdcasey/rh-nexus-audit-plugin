package com.redhat.tools.nexus.capture.config;

import org.sonatype.plugin.Managed;

import javax.inject.Singleton;

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
