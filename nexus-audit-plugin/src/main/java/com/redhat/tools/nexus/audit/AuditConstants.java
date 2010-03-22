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

    public static final String AUDIT_TEMPLATE_BASEPATH = "audit/log";

    private AuditConstants()
    {
    }

}
