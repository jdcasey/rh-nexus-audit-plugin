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

package com.redhat.tools.nexus.audit.serial.store;

public class AuditStoreException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    private final Object[] params;

    public AuditStoreException( final String message, final Throwable cause )
    {
        super( message, cause );
        params = null;
    }

    public AuditStoreException( final String message )
    {
        super( message );
        params = null;
    }

    public AuditStoreException( final String message, final Throwable cause, final Object... params )
    {
        super( message, cause );
        this.params = params;
    }

    public AuditStoreException( final String message, final Object... params )
    {
        super( message );
        this.params = params;
    }

    @Override
    public String getLocalizedMessage()
    {
        return getMessage();
    }

    @Override
    public String getMessage()
    {
        final String msg = super.getMessage();
        if ( params == null )
        {
            return msg;
        }
        else
        {
            try
            {
                return String.format( msg, params );
            }
            catch ( final Throwable t )
            {
                return msg;
            }
        }
    }

}
