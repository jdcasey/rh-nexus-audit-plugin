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

package com.redhat.tools.nexus.serial;

import static com.redhat.tools.nexus.request.RequestUtils.parseDate;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class DateToFileMapTypeAdapter
    implements JsonSerializer<TreeMap<Date, File>>, JsonDeserializer<TreeMap<Date, File>>,
    InstanceCreator<TreeMap<Date, File>>, Converter
{

    private static final String DATE_PROP = "date";

    private static final String FILE_PROP = "file";

    private final String outputFormat;

    private final String entryName;

    private final String[] inputFormats;

    public DateToFileMapTypeAdapter( final String outputFormat, final String[] inputFormats, final String entryName )
    {
        this.outputFormat = outputFormat;
        this.inputFormats = inputFormats;
        this.entryName = entryName;
    }

    public JsonElement serialize( final TreeMap<Date, File> src, final Type typeOfSrc,
                                  final JsonSerializationContext context )
    {
        final JsonArray array = new JsonArray();
        if ( src != null )
        {
            for ( final Map.Entry<Date, File> entry : src.entrySet() )
            {
                final JsonObject obj = new JsonObject();
                obj.addProperty( DATE_PROP, new SimpleDateFormat( outputFormat ).format( entry.getKey() ) );

                String path;
                try
                {
                    path = entry.getValue().getCanonicalPath();
                }
                catch ( final IOException e )
                {
                    path = entry.getValue().getAbsolutePath();
                }

                obj.addProperty( FILE_PROP, path );

                array.add( obj );
            }
        }

        return array;
    }

    public TreeMap<Date, File> deserialize( final JsonElement json, final Type typeOfT,
                                            final JsonDeserializationContext context )
        throws JsonParseException
    {
        final TreeMap<Date, File> result = new TreeMap<Date, File>();

        final JsonArray array = (JsonArray) json;
        for ( final JsonElement el : array )
        {
            final JsonObject obj = (JsonObject) el;
            final String value = obj.get( DATE_PROP ).getAsString();

            Date d;
            try
            {
                d = parseDate( value, inputFormats );
            }
            catch ( final ParseException e )
            {
                throw new IllegalArgumentException( String.format( "Cannot parse date: '%s'. Reason: '%s'", value,
                                                                   e.getMessage() ), e );
            }

            final File f = new File( obj.get( FILE_PROP ).getAsString() );

            result.put( d, f );
        }

        return result;
    }

    public TreeMap<Date, File> createInstance( final Type type )
    {
        return new TreeMap<Date, File>();
    }

    @SuppressWarnings( "unchecked" )
    public void marshal( final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context )
    {
        final TreeMap<Date, File> map = (TreeMap<Date, File>) source;
        for ( final Map.Entry<Date, File> entry : map.entrySet() )
        {
            writer.startNode( entryName );

            writer.startNode( DATE_PROP );
            writer.setValue( new SimpleDateFormat( outputFormat ).format( entry.getKey() ) );
            writer.endNode();

            String path;
            try
            {
                path = entry.getValue().getCanonicalPath();
            }
            catch ( final IOException e )
            {
                path = entry.getValue().getAbsolutePath();
            }

            writer.startNode( FILE_PROP );
            writer.setValue( path );
            writer.endNode();

            writer.endNode();
        }
    }

    public Object unmarshal( final HierarchicalStreamReader reader, final UnmarshallingContext context )
    {
        final TreeMap<Date, File> result = new TreeMap<Date, File>();

        reader.moveDown();
        while ( reader.hasMoreChildren() )
        {
            reader.moveDown();
            Date d = null;
            File f = null;
            while ( reader.hasMoreChildren() )
            {
                if ( reader.getNodeName().equals( DATE_PROP ) )
                {
                    final String value = reader.getValue();
                    try
                    {
                        d = parseDate( value, inputFormats );
                    }
                    catch ( final ParseException e )
                    {
                        throw new IllegalArgumentException( String.format( "Cannot parse date: '%s'. Reason: %s",
                                                                           value, e.getMessage() ), e );
                    }
                }
                else if ( reader.getNodeName().equals( FILE_PROP ) )
                {
                    f = new File( reader.getValue() );
                }
                else
                {
                    throw new IllegalArgumentException( String.format( "Invalid element: '%s'", reader.getNodeName() ) );
                }

                if ( d != null && f != null )
                {
                    break;
                }
            }

            if ( d != null && f != null )
            {
                result.put( d, f );
            }

            reader.moveUp();
        }
        reader.moveUp();

        return result;
    }

    @SuppressWarnings( "unchecked" )
    public boolean canConvert( final Class type )
    {
        return TreeMap.class.isAssignableFrom( type );
    }

}
