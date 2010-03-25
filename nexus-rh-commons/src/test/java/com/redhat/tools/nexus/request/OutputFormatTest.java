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
import org.restlet.data.MediaType;

public class OutputFormatTest
{

    @Test
    public void retrieveTextPlainUsingPlainFormatId()
    {
        assertEquals( OutputFormat.plain, OutputFormat.find( "plain" ) );
    }

    @Test
    public void retrieveTextPlainUsingTextFormatId()
    {
        assertEquals( OutputFormat.plain, OutputFormat.find( "text" ) );
    }

    @Test
    public void retrieveTextPlainUsingTextPlainMediaType()
    {
        assertEquals( OutputFormat.plain, OutputFormat.find( MediaType.TEXT_PLAIN.getName() ) );
    }

    @Test
    public void retrieveAppXmlUsingXMLFormatId()
    {
        assertEquals( OutputFormat.xml, OutputFormat.find( "XML" ) );
    }

    @Test
    public void retrieveAppXmlUsing_xml_FormatId()
    {
        assertEquals( OutputFormat.xml, OutputFormat.find( "xml" ) );
    }

    @Test
    public void retrieveAppXmlUsingAppXmlMediaType()
    {
        assertEquals( OutputFormat.xml, OutputFormat.find( MediaType.APPLICATION_XML.getName() ) );
    }

    @Test
    public void retrieveAppXmlUsingTextXmlMediaType()
    {
        assertEquals( OutputFormat.xml, OutputFormat.find( MediaType.TEXT_XML.getName() ) );
    }

    @Test
    public void retrieveAppJsonUsingJSONFormatId()
    {
        assertEquals( OutputFormat.json, OutputFormat.find( "JSON" ) );
    }

    @Test
    public void retrieveAppJsonUsing_json_FormatId()
    {
        assertEquals( OutputFormat.json, OutputFormat.find( "json" ) );
    }

    @Test
    public void retrieveAppJsonUsingAppJsonMediaType()
    {
        assertEquals( OutputFormat.json, OutputFormat.find( MediaType.APPLICATION_JSON.getName() ) );
    }

}
