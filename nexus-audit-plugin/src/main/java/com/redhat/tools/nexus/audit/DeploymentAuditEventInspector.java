/*
 *  Copyright (c) 2010 Red Hat, Inc.
 *  
 *  This program is licensed to you under Version 3 only of the GNU
 *  General Public License as published by the Free Software 
 *  Foundation. This program is distributed in the hope that it will be 
 *  useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 *  PURPOSE.
 *  
 *  See the GNU General Public License Version 3 for more details.
 *  You should have received a copy of the GNU General Public License 
 *  Version 3 along with this program. 
 *  
 *  If not, see http://www.gnu.org/licenses/.
 */

package com.redhat.tools.nexus.audit;

import static org.apache.commons.lang.StringUtils.isBlank;

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
import java.util.HashSet;
import java.util.Set;

@Named( "deploymentAuditInspector" )
public class DeploymentAuditEventInspector
    extends AbstractEventInspector
    implements EventInspector
{

    private static final Logger logger = LoggerFactory.getLogger( DeploymentAuditEventInspector.class );

    private static final Set<String> UNTRACKED_PATH_TOKENS;

    static
    {
        final Set<String> tokens = new HashSet<String>();
        tokens.add( ".audit.json" );
        tokens.add( ".meta" );
        tokens.add( ".index" );
        tokens.add( ".md5" );
        tokens.add( ".sha1" );
        tokens.add( ".asc" );

        UNTRACKED_PATH_TOKENS = tokens;
    }

    private boolean startLogging;

    @Inject
    @Named( AuditConstants.PREFERRED_STORE )
    private AuditStore store;

    public boolean accepts( final Event<?> evt )
    {
        // first event type is necessary to determine when logging should start...see inspect() below.
        return evt instanceof NexusStartedEvent || evt instanceof RepositoryItemEventStore;
    }

    public void inspect( final Event<?> evt )
    {
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

            if ( !isTrackable( evtStore ) )
            {
                return;
            }

            final String repoId = item.getRepositoryId();
            final String path = item.getPath();
            final String owner = item.getAttributes().get( AccessManager.REQUEST_USER );

            if ( logger.isDebugEnabled() )
            {
                logger.debug( String.format( "Updating audit log for: '%s'; owner: '%s'", path, owner ) );
            }

            final AuditInfo audit = new AuditInfo( owner, new Date(), path, repoId );

            try
            {
                store.saveAuditInformation( audit );
            }
            catch ( final AuditStoreException e )
            {
                logger.error( String.format( "Failed to save audit record.\nStorage Item Path: %s" + "\nOwner: %s"
                    + "\nRepository-Id: %s" + "\nReason: %s", path, owner, repoId, e.getMessage() ), e );
            }
        }
    }

    private boolean isTrackable( final RepositoryItemEventStore evtStore )
    {
        final StorageItem item = evtStore.getItem();
        final String path = item.getPath();

        // don't track audit information for anything with a path matching an untracked token. 
        // These are things like checksums, index files, Nexus metadata, etc.
        for ( final String token : UNTRACKED_PATH_TOKENS )
        {
            if ( path.indexOf( token ) > -1 )
            {
                return false;
            }
        }

        // don't track audit information on proxied files.
        return isBlank( item.getRemoteUrl() );
    }

}
