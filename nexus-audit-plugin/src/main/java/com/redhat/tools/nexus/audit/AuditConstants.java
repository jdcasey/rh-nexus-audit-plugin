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

public final class AuditConstants
{

    public static final String PREFERRED_STORE = "json";

    public static final String REPO_ID = "repositoryId";

    public static final String GROUP_ID = "groupId";

    public static final String ARTIFACT_ID = "artifactId";

    public static final String VERSION = "version";

    public static final String PARAM_CLASSIFIER = "c";

    public static final String PARAM_EXTENSION = "t";

    public static final String AUDIT_TEMPLATE_BASEPATH = "templates/audit/log";

    public static final String PARAM_QUIET = "q";

    public static final String PRIV_AUDIT_ACCESS = "nexus:audit-access";

    private AuditConstants()
    {
    }

}
