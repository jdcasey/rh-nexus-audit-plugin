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

package com.redhat.tools.nexus.protocol;

import static com.redhat.tools.nexus.protocol.ProtocolUtils.buildUri;
import static com.redhat.tools.nexus.protocol.ProtocolUtils.formatUrlDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProtocolUtilsTest
{

    @Test
    public void formatUrlDate_ReturnNullWhenDateIsNull()
    {
        assertNull( formatUrlDate( null ) );
    }

    @Test
    public void formatUrlDate_IncludeDateTimeAndTZInFormat()
    {
        final Date d = new Date();
        assertEquals( new SimpleDateFormat( ProtocolConstants.FULL_DATE_FORMAT ).format( d ), formatUrlDate( d ) );
    }

    @Test
    public void buildUri_ReturnNullWhenBasePathIsNull()
    {
        assertNull( buildUri( null, "path", "to", "something" ) );
    }

    @Test
    public void buildUri_ConcatenatePathPartsNotAlreadyTerminatedInSlash()
    {
        assertEquals( "http://www.google.com/path/to/something", buildUri( "http://www.google.com", "path", "to",
                                                                           "something" ) );
    }

    @Test
    public void buildUri_ConcatenatePathPartsWithMixedSlashTerminals()
    {
        assertEquals( "http://www.google.com/path/to/something", buildUri( "http://www.google.com/", "/path", "to/",
                                                                           "something" ) );
    }

}
