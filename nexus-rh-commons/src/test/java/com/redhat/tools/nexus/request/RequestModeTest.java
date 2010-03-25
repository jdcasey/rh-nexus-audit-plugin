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

package com.redhat.tools.nexus.request;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RequestModeTest
{
    @Test
    public void find_UseDefaultIfNull()
    {
        assertEquals( RequestMode.DEFAULT, RequestMode.find( null ) );
    }

    @Test
    public void find_UseModeMatchingListValue()
    {
        assertEquals( RequestMode.TABLE_OF_CONTENTS, RequestMode.find( "list" ) );
    }

    @Test
    public void find_UseModeMatchingTOCValue()
    {
        assertEquals( RequestMode.TABLE_OF_CONTENTS, RequestMode.find( "TOC" ) );
    }

    @Test
    public void find_UseModeMatchingDEFValue()
    {
        assertEquals( RequestMode.DEFAULT, RequestMode.find( "DEF" ) );
    }

    @Test
    public void modeOf_UseDefaultForUnmatchedValue()
    {
        assertEquals( RequestMode.DEFAULT, RequestMode.find( "FOO" ) );
    }
}
