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

package com.redhat.tools.nexus.audit.serial.store;

import org.sonatype.nexus.artifact.Gav;
import org.sonatype.plugin.Managed;

import com.redhat.tools.nexus.audit.model.AuditInfo;

import javax.inject.Singleton;

@Managed
@Singleton
public interface AuditStore
{

    boolean saveAuditInformation( AuditInfo auditInfo )
        throws AuditStoreException;

    AuditInfo getAuditInformation( Gav gav, String repoId )
        throws AuditStoreException;

    AuditInfo getAuditInformation( String path, String repoId )
        throws AuditStoreException;

}
