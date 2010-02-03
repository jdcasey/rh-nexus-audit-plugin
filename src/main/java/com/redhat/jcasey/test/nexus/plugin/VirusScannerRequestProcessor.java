package com.redhat.jcasey.test.nexus.plugin;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.access.Action;
import org.sonatype.nexus.proxy.item.AbstractStorageItem;
import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.repository.ProxyRepository;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.plexus.appevents.ApplicationEventMulticaster;

import com.redhat.jcasey.test.nexus.plugin.events.InfectedItemFoundEvent;

@Named( "virusScanner" )
public class VirusScannerRequestProcessor
// implements RequestProcessor
{
    @Inject
    private ApplicationEventMulticaster applicationEventMulticaster;

    @Inject
    private @Named( "XY" )
    VirusScanner virusScanner;

    // @Inject
    // private @Named("A") CommonDependency commonDependency;

    public boolean process( final Repository repository, final ResourceStoreRequest request, final Action action )
    {
        // Check dependency
        // System.out.println( "VirusScannerRequestProcessor --- CommonDependency data: " + commonDependency.getData()
        // );

        // don't decide until have content
        return true;
    }

    public boolean shouldProxy( final ProxyRepository repository, final ResourceStoreRequest request )
    {
        // don't decide until have content
        return true;
    }

    public boolean shouldCache( final ProxyRepository repository, final AbstractStorageItem item )
    {
        if ( item instanceof StorageFileItem )
        {
            final StorageFileItem file = (StorageFileItem) item;

            // do a virus scan
            final boolean hasVirus = virusScanner.hasVirus( file );

            if ( hasVirus )
            {
                applicationEventMulticaster.notifyEventListeners( new InfectedItemFoundEvent( item.getRepositoryItemUid()
                                                                                                  .getRepository(),
                    file ) );
            }

            return !hasVirus;
        }
        else
        {
            return true;
        }
    }

}
