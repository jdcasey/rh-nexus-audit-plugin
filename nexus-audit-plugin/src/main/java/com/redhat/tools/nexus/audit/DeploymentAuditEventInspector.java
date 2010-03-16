package com.redhat.tools.nexus.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.proxy.access.AccessManager;
import org.sonatype.nexus.proxy.events.AbstractEventInspector;
import org.sonatype.nexus.proxy.events.EventInspector;
import org.sonatype.nexus.proxy.events.NexusStartedEvent;
import org.sonatype.nexus.proxy.events.RepositoryItemEventStore;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.plexus.appevents.Event;

import com.redhat.tools.nexus.audit.model.AuditInfo;
import com.redhat.tools.nexus.audit.serial.store.AuditStore;
import com.redhat.tools.nexus.audit.serial.store.AuditStoreException;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.Date;

@Named( "deploymentAuditInspector" )
public class DeploymentAuditEventInspector
    extends AbstractEventInspector
    implements EventInspector
{

    private static final Logger logger = LoggerFactory.getLogger( DeploymentAuditEventInspector.class );

    private boolean startLogging;

    @Inject
    @Named( AuditConstants.PREFERRED_STORE )
    private AuditStore store;

    public boolean accepts( final Event<?> evt )
    {
        // first is needed to determine when logging should start...see inspect() below.
        return evt instanceof NexusStartedEvent || evt instanceof RepositoryItemEventStore;
    }

    public void inspect( final Event<?> evt )
    {
        logger.info( "Processing event for deployment info: " + evt );

        if ( evt instanceof NexusStartedEvent )
        {
            // some spurious storage events before nexus starts...not sure why, but skip 'em.
            startLogging = true;
            return;
        }

        if ( startLogging )
        {
            final RepositoryItemEventStore evtStore = (RepositoryItemEventStore) evt;
            final StorageItem item = evtStore.getItem();

            // TODO: Limit logging to actual deployments, NOT cache actions for proxies.

            final String repoId = item.getRepositoryId();
            final String path = item.getPath();
            final String owner = item.getAttributes().get( AccessManager.REQUEST_USER );

            logger.info( String.format( "Adding deployment audit info for: %s in repository: %s\nDeploying user: %s",
                                        path, repoId, owner ) );

            final AuditInfo audit = new AuditInfo( owner, new Date(), path, repoId );

            boolean result = false;
            try
            {
                result = store.saveAuditInformation( audit );
            }
            catch ( final AuditStoreException e )
            {
                logger.error( String.format( "Failed to save audit record.\nStorage Item Path: %s" + "\nOwner: %s"
                    + "\nRepository-Id: %s" + "\nReason: %s", path, owner, repoId, e.getMessage() ), e );
            }

            logger.info( "Result: " + result );
        }
    }
}
