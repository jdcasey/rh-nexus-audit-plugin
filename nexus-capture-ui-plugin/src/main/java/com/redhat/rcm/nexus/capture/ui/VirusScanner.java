package com.redhat.rcm.nexus.capture.ui;

import javax.inject.Singleton;

import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.plugin.Managed;

@Managed
@Singleton
public interface VirusScanner
{
    boolean hasVirus( StorageFileItem file );
}
