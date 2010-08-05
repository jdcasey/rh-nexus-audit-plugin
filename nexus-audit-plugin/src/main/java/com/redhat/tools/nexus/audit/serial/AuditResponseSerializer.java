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

package com.redhat.tools.nexus.audit.serial;

import com.google.gson.Gson;
import com.redhat.tools.nexus.response.AbstractWebResponseSerializer;
import com.redhat.tools.nexus.response.WebResponseSerializer;
import com.thoughtworks.xstream.XStream;

import javax.inject.Named;

@Named( "audit" )
public class AuditResponseSerializer
    extends AbstractWebResponseSerializer
    implements WebResponseSerializer
{

    //    AuditReponseSerializer()
    //    {
    //    }

    @Override
    protected Gson getGson()
    {
        return SerialUtils.getGson();
    }

    @Override
    protected XStream getXStream()
    {
        return SerialUtils.getXStreamForREST();
    }

}
