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

import static com.redhat.tools.nexus.request.RequestUtils.getDate;
import static com.redhat.tools.nexus.request.RequestUtils.mediaTypeOf;
import static com.redhat.tools.nexus.request.RequestUtils.modeOf;
import static com.redhat.tools.nexus.request.RequestUtils.parseDate;
import static com.redhat.tools.nexus.request.RequestUtils.requestAttribute;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestUtilsTest
{

    @Test( expected = ResourceException.class )
    public void requestAttribute_FailIfAttributeIsMissing()
        throws ResourceException
    {
        requestAttribute( "missing", new Request() );
    }

    @Test
    public void requestAttribute_FindValueWhenProvided()
        throws ResourceException
    {
        final Request req = new Request();
        req.setAttributes( Collections.singletonMap( "provided", (Object) "true" ) );

        assertEquals( "true", requestAttribute( "provided", req ) );
    }

    @Test
    public void parseUrlDate_UsingFullDateFormat()
        throws ParseException
    {
        final Date date = newDate();
        assertEquals( date, parseDate( new SimpleDateFormat( RequestUtils.FULL_DATE_FORMAT ).format( date ) ) );
    }

    @Test
    public void parseUrlDate_UsingDateAndTime()
        throws ParseException
    {
        final Date date = newDate();
        assertEquals( date, parseDate( new SimpleDateFormat( "yyyy-MM-dd+HH-mm-ss" ).format( date ) ) );
    }

    @Test
    public void parseUrlDate_UsingDate()
        throws ParseException
    {
        final Date d1 = newDate();
        final Date d2 = parseDate( new SimpleDateFormat( "yyyy-MM-dd" ).format( d1 ) );

        final Calendar c1 = Calendar.getInstance();
        c1.setTime( d1 );

        final Calendar c2 = Calendar.getInstance();
        c2.setTime( d2 );

        assertEquals( c1.get( Calendar.YEAR ), c2.get( Calendar.YEAR ) );
        assertEquals( c1.get( Calendar.MONTH ), c2.get( Calendar.MONTH ) );
        assertEquals( c1.get( Calendar.DATE ), c2.get( Calendar.DATE ) );
    }

    @Test( expected = ParseException.class )
    public void parseUrlDate_FailUsingUnsupportedFormatted()
        throws ParseException
    {
        final Date date = newDate();
        assertFalse( date.equals( parseDate( new SimpleDateFormat( "yyyy/MM/dd hh:m:ss" ).format( date ) ) ) );
    }

    @Test
    public void getDate_UsingFullDateFormat()
        throws ResourceException
    {
        final Date date = newDate();
        final Request req = newRequestWithFormattedDate( "date", date, RequestUtils.FULL_DATE_FORMAT );

        assertEquals( date, getDate( "date", req ) );
    }

    @Test
    public void getDate_UsingDateAndTime()
        throws ResourceException
    {
        final Date date = newDate();
        final Request req = newRequestWithFormattedDate( "date", date, "yyyy-MM-dd+HH-mm-ss" );

        assertEquals( date, getDate( "date", req ) );
    }

    @Test
    public void getDate_UsingDate()
        throws ResourceException
    {
        final Date d1 = newDate();
        final Date d2 = getDate( "date", newRequestWithFormattedDate( "date", d1, "yyyy-MM-dd" ) );

        final Calendar c1 = Calendar.getInstance();
        c1.setTime( d1 );

        final Calendar c2 = Calendar.getInstance();
        c2.setTime( d2 );

        assertEquals( c1.get( Calendar.YEAR ), c2.get( Calendar.YEAR ) );
        assertEquals( c1.get( Calendar.MONTH ), c2.get( Calendar.MONTH ) );
        assertEquals( c1.get( Calendar.DATE ), c2.get( Calendar.DATE ) );
    }

    @Test( expected = ResourceException.class )
    public void getDate_FailUsingUnsupportedFormatted()
        throws ResourceException
    {
        final String format = "yyyy/MM/dd hh:m:ss";

        final Date date = newDate();
        final Request req = newRequestWithFormattedDate( "date", date, format );

        assertFalse( date.equals( getDate( "date", req ) ) );
    }

    @Test
    public void mediaTypeOf_NoDefaultFindAndUseJsonFormatByName()
    {
        assertEquals( MediaType.APPLICATION_JSON, mediaTypeOf( newRequestWithMediaTypeParam( "json" ) ) );
    }

    @Test
    public void mediaTypeOf_NoDefaultFindAndUseXmlFormatByName()
    {
        assertEquals( MediaType.APPLICATION_XML, mediaTypeOf( newRequestWithMediaTypeParam( "xml" ) ) );
    }

    @Test
    public void mediaTypeOf_NoDefaultFindAndUsePlainFormatByName()
    {
        assertEquals( MediaType.TEXT_PLAIN, mediaTypeOf( newRequestWithMediaTypeParam( "plain" ) ) );
    }

    @Test
    public void mediaTypeOf_NoDefaultFindAndUseJsonFormatByContentType()
    {
        assertEquals( MediaType.APPLICATION_JSON, mediaTypeOf( newRequestWithMediaTypeParam( "application/json" ) ) );
    }

    @Test
    public void mediaTypeOf_NoDefaultFindAndUseXmlFormatByContentType()
    {
        assertEquals( MediaType.APPLICATION_XML, mediaTypeOf( newRequestWithMediaTypeParam( "application/xml" ) ) );
    }

    @Test
    public void mediaTypeOf_NoDefaultFindAndUsePlainFormatByContentType()
    {
        assertEquals( MediaType.TEXT_PLAIN, mediaTypeOf( newRequestWithMediaTypeParam( "text/plain" ) ) );
    }

    @Test
    public void mediaTypeOf_UseDefaultIfFormatParamUnspecified()
    {
        assertEquals( MediaType.TEXT_PLAIN, mediaTypeOf( newRequestWithMediaTypeParam( null ),
                                                         new Variant( MediaType.TEXT_PLAIN ) ) );
    }

    @Test
    public void mediaTypeOf_OverrideDefaultWithFormatParam()
    {
        assertEquals( MediaType.TEXT_PLAIN, mediaTypeOf( newRequestWithMediaTypeParam( "plain" ),
                                                         new Variant( MediaType.APPLICATION_JSON ) ) );
    }

    @Test
    public void mediaTypeOf_UseAcceptHeaderIfFormatParamUnspecifiedAndNoDefaultGiven()
    {
        assertEquals( MediaType.TEXT_PLAIN, mediaTypeOf( addHeader( "Accept", MediaType.TEXT_PLAIN.getName(),
                                                                    newRequestWithMediaTypeParam( null ) ) ) );
    }

    @Test
    public void mediaTypeOf_UseMultiValueAcceptHeaderIfFormatParamUnspecifiedAndNoDefaultGiven()
    {
        assertEquals( MediaType.TEXT_PLAIN, mediaTypeOf( addHeader( "Accept", MediaType.APPLICATION_JSON.getName()
            + ", " + MediaType.TEXT_PLAIN, newRequestWithMediaTypeParam( null ) ) ) );
    }

    @Test
    public void mediaTypeOf_UseMultiValueAcceptHeaderWithQOSIfFormatParamUnspecifiedAndNoDefaultGiven()
    {
        assertEquals( MediaType.TEXT_PLAIN, mediaTypeOf( addHeader( "Accept", MediaType.APPLICATION_JSON.getName()
            + "; q=0.2, " + MediaType.TEXT_PLAIN, newRequestWithMediaTypeParam( null ) ) ) );
    }

    @Test
    public void mediaTypeOf_UseAcceptHeaderIfFormatParamUnmatchedAndNoDefaultGiven()
    {
        assertEquals( MediaType.TEXT_PLAIN, mediaTypeOf( addHeader( "Accept", MediaType.TEXT_PLAIN.getName(),
                                                                    newRequestWithMediaTypeParam( "unknown_zzz" ) ) ) );
    }

    @Test
    public void modeOf_UseDefaultIfUnspecified()
    {
        assertEquals( RequestMode.DEFAULT, modeOf( newRequestWithModeParam( null ) ) );
    }

    @Test
    public void modeOf_UseModeMatchingListParamValue()
    {
        assertEquals( RequestMode.TABLE_OF_CONTENTS, modeOf( newRequestWithModeParam( "list" ) ) );
    }

    @Test
    public void modeOf_UseModeMatchingTOCParamValue()
    {
        assertEquals( RequestMode.TABLE_OF_CONTENTS, modeOf( newRequestWithModeParam( "TOC" ) ) );
    }

    @Test
    public void modeOf_UseModeMatchingDEFParamValue()
    {
        assertEquals( RequestMode.DEFAULT, modeOf( newRequestWithModeParam( "DEF" ) ) );
    }

    @Test
    public void modeOf_UseDefaultForUnmatchedParamValue()
    {
        assertEquals( RequestMode.DEFAULT, modeOf( newRequestWithModeParam( "FOO" ) ) );
    }

    private Request addHeader( final String key, final String value, final Request request )
    {
        Map<String, Object> attributes = request.getAttributes();
        if ( attributes == null )
        {
            attributes = new HashMap<String, Object>();
            request.setAttributes( attributes );
        }

        Form headers = (Form) attributes.get( "org.restlet.http.headers" );
        if ( headers == null )
        {
            headers = new Form();
            attributes.put( "org.restlet.http.headers", headers );
        }

        headers.set( key, value, true );

        return request;
    }

    private Request newRequestWithMediaTypeParam( final String mediaType )
    {
        final Reference ref = new Reference();
        if ( mediaType != null )
        {
            ref.setQuery( RequestUtils.PARAM_FORMAT + "=" + mediaType );
        }

        final Request req = new Request();
        req.setResourceRef( ref );

        return req;
    }

    private Request newRequestWithModeParam( final String mode )
    {
        final Reference ref = new Reference();
        if ( mode != null )
        {
            ref.setQuery( RequestUtils.PARAM_MODE + "=" + mode );
        }

        final Request req = new Request();
        req.setResourceRef( ref );

        return req;
    }

    private Request newRequestWithFormattedDate( final String param, final Date date, final String format )
    {
        final Reference ref = new Reference();
        ref.setQuery( param + "=" + new SimpleDateFormat( format ).format( date ) );

        final Request req = new Request();
        req.setResourceRef( ref );

        return req;
    }

    private Date newDate()
    {
        final Calendar cal = Calendar.getInstance();
        cal.set( Calendar.DATE, 11 );
        cal.set( Calendar.MONTH, 11 );
        cal.set( Calendar.YEAR, 2010 );
        cal.set( Calendar.HOUR, 11 );
        cal.set( Calendar.MINUTE, 11 );
        cal.set( Calendar.SECOND, 11 );
        cal.set( Calendar.MILLISECOND, 0 );
        cal.set( Calendar.ZONE_OFFSET, 0 );

        return cal.getTime();
    }

}
