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
import com.redhat.tools.nexus.audit.serial.SerialConstants;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Date;

@XStreamAlias( SerialConstants.AUDIT_INFO_ROOT )
public class AuditInfoDTO
{

    public static final String AUDIT_SVC = "/service/local/audit";

    @SerializedName( SerialConstants.AUDIT_INFO_RESOURCE_URI )
    @XStreamAlias( SerialConstants.AUDIT_INFO_RESOURCE_URI )
    private final String url;

    @SerializedName( SerialConstants.AUDIT_INFO_TARGET_RESOURCE_URI )
    @XStreamAlias( SerialConstants.AUDIT_INFO_TARGET_RESOURCE_URI )
    private final String targetUrl;

    @SerializedName( SerialConstants.AUDIT_INFO_OWNER )
    @XStreamAlias( SerialConstants.AUDIT_INFO_OWNER )
    private final String owner;

    @SerializedName( SerialConstants.AUDIT_INFO_CAPTURE_DATE )
    @XStreamAlias( SerialConstants.AUDIT_INFO_CAPTURE_DATE )
    private final Date dateAdded;

    @SerializedName( SerialConstants.AUDIT_INFO_REFERENCED_PATH )
    @XStreamAlias( SerialConstants.AUDIT_INFO_REFERENCED_PATH )
    private final String referencedPath;

    @SerializedName( SerialConstants.AUDIT_INFO_REPO_ID )
    @XStreamAlias( SerialConstants.AUDIT_INFO_REPO_ID )
    private final String repositoryId;

    // For gson deserialize
    @SuppressWarnings( "unused" )
    private AuditInfoDTO()
    {
        owner = null;
        dateAdded = null;
        referencedPath = null;
        repositoryId = null;
        url = null;
        targetUrl = null;
    }

    public AuditInfoDTO( final String owner, final Date dateAdded, final String referencedPath,
                         final String repositoryId, final String appUrl, final String repositoryBase )
    {
        this.owner = owner;
        this.dateAdded = dateAdded;
        this.referencedPath = referencedPath;
        this.repositoryId = repositoryId;

        url = buildUri( appUrl, AUDIT_SVC, referencedPath );
        targetUrl = buildUri( repositoryBase, referencedPath );
    }

    public String getOwner()
    {
        return owner;
    }

    public Date getDateAdded()
    {
        return dateAdded;
    }

    public String getReferencedPath()
    {
        return referencedPath;
    }

    public String getRepositoryId()
    {
        return repositoryId;
    }

    public String getUrl()
    {
        return url;
    }

    public String getTargetUrl()
    {
        return targetUrl;
    }

    @Override
    public String toString()
    {
        return "AuditInfoDTO [dateAdded=" + dateAdded + ", owner=" + owner + ", referencedPath=" + referencedPath
            + ", repositoryId=" + repositoryId + ", targetUrl=" + targetUrl + ", url=" + url + "]";
    }

}
