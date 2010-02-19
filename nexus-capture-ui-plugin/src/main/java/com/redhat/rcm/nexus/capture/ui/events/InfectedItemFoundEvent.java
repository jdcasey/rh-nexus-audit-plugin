package com.redhat.rcm.nexus.capture.ui.events;

import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.plexus.appevents.AbstractEvent;

public class InfectedItemFoundEvent
    extends AbstractEvent<Repository>
{
    private final StorageFileItem file;

    public InfectedItemFoundEvent( Repository component, StorageFileItem file )
    {
        super( component );

        this.file = file;
    }

    public StorageFileItem getInfectedFile()
    {
        return file;
    }
}
