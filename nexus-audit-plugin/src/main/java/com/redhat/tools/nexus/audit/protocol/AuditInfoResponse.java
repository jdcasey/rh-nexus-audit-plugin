/*
 *  Copyright (C) 2010 John Casey.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.redhat.tools.nexus.audit.protocol;

import static com.redhat.tools.nexus.request.PathUtils.buildUri;

import com.google.gson.annotations.SerializedName;
import com.redhat.tools.nexus.audit.model.AuditInfo;
import com.redhat.tools.nexus.audit.serial.SerialConstants;
import com.redhat.tools.nexus.audit.serial.SerialUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( SerialConstants.AUDIT_INFO_ROOT )
public class AuditInfoResponse
{

    public static final String AUDIT_SVC = "/service/local/audit";

    @SerializedName( SerialConstants.AUDIT_INFO_RESOURCE_URI )
    @XStreamAlias( SerialConstants.AUDIT_INFO_RESOURCE_URI )
    private final String url;

    @SerializedName( SerialConstants.AUDIT_INFO_TARGET_RESOURCE_URI )
    @XStreamAlias( SerialConstants.AUDIT_INFO_TARGET_RESOURCE_URI )
    private final String targetUrl;

    private final AuditInfo data;

    // For gson deserialize
    @SuppressWarnings( "unused" )
    private AuditInfoResponse()
    {
        data = null;
        url = null;
        targetUrl = null;
    }

    public AuditInfoResponse( final AuditInfo auditInfo, final String appUrl, final String remoteBase )
    {
        data = auditInfo;
        url = appUrl == null ? null : buildUri( appUrl, AUDIT_SVC, auditInfo.getReferencedPath() );
        targetUrl = remoteBase == null ? null : buildUri( remoteBase, auditInfo.getReferencedPath() );
    }

    public String getUrl()
    {
        return url;
    }

    public String getTargetUrl()
    {
        return targetUrl;
    }

    public AuditInfo getData()
    {
        return data;
    }

    public static AuditInfoResponse getUnknown( final String repoId )
    {
        return new AuditInfoResponse( new AuditInfo( "unknown", SerialUtils.UNKNOWN_DATE, null, repoId ), null, null );
    }

}
