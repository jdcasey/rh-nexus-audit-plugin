package com.redhat.jcasey.test.nexus.plugin.repo;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.nexus.proxy.repository.AbstractGroupRepositoryConfigurator;

@Component( role = CaptureGroupRepositoryConfigurator.class )
public class CaptureGroupRepositoryConfigurator
    extends AbstractGroupRepositoryConfigurator
{

}
