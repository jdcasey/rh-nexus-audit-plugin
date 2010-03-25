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

import static org.junit.Assert.assertNull;

import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.component.composition.CycleDetectedInComponentGraphException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.repository.Repository;

import javax.inject.Inject;
import javax.inject.Named;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class JsonAuditStoreIntegrationTest
    extends AbstractNexusIntegrationTest
{

    private AuditStore store;

    private RepositoryRegistry reg;

    @Before
    public void injectComponents()
        throws Exception
    {
        addComponent( AuditStore.class, "json", JsonAuditStore.class );
        store = lookup( AuditStore.class, "json" );
        reg = lookup( RepositoryRegistry.class, "protected" );
    }

    private <T> void addComponent( final Class<T> role, final String hint, final Class<? extends T> impl )
        throws CycleDetectedInComponentGraphException
    {
        if ( !getContainer().hasComponent( role, hint ) )
        {
            if ( impl == null )
            {
                throw new IllegalStateException( String.format( "Cannot find component with role: %s and hint: %s. "
                    + "No implementation was provided to allow its addition.", role.getName(), hint ) );
            }

            final ComponentDescriptor<T> cd = new ComponentDescriptor<T>();

            cd.setRoleClass( role );
            cd.setRoleHint( hint );
            cd.setImplementationClass( impl );

            Class<?> c = impl;
            while ( c != null && !c.getPackage().getName().startsWith( "java" ) )
            {
                final Field[] fields = c.getDeclaredFields();
                for ( final Field field : fields )
                {
                    addRequirementIfFound( cd, field, field.getName(), field.getType(), true );
                }

                final Method[] methods = c.getDeclaredMethods();
                for ( final Method method : methods )
                {
                    final String name = method.getName();
                    final Class<?>[] params = method.getParameterTypes();
                    if ( name.startsWith( "set" ) && params != null && params.length == 1 )
                    {
                        addRequirementIfFound( cd, method, name, params[0], false );
                    }
                }

                c = c.getSuperclass();
            }

            System.out.println( String.format( "Adding component descriptor: %s\nWith %d requirements: %s", cd,
                                               cd.getRequirements().size(), StringUtils.join( cd.getRequirements(),
                                                                                              "\n" ) ) );

            getContainer().addComponentDescriptor( cd );
        }
    }

    private <T> void addRequirementIfFound( final ComponentDescriptor<T> cd, final AnnotatedElement e,
                                            final String elementName, final Class<?> elementType,
                                            final boolean includeFieldName )
    {
        if ( e.isAnnotationPresent( Inject.class ) )
        {
            final ComponentRequirement req = new ComponentRequirement();

            if ( includeFieldName )
            {
                req.setFieldName( elementName );
            }

            String name = null;
            final Named anno = e.getAnnotation( Named.class );
            if ( anno != null )
            {
                name = anno.value();
                req.setRoleHint( name );
            }

            final LinkedList<Class<?>> candidates = new LinkedList<Class<?>>();
            candidates.addFirst( elementType );

            while ( !candidates.isEmpty() )
            {
                final Class<?> r = candidates.removeLast();
                final String rName = r.getName();
                if ( name != null )
                {
                    if ( getContainer().hasComponent( rName, name ) )
                    {
                        req.setRole( rName );
                        cd.addRequirement( req );
                        break;
                    }
                }
                else
                {
                    if ( getContainer().hasComponent( rName ) )
                    {
                        req.setRole( rName );
                        cd.addRequirement( req );
                        break;
                    }
                }

                if ( r.getInterfaces() != null )
                {
                    for ( final Class<?> ifc : r.getInterfaces() )
                    {
                        candidates.addFirst( ifc );
                    }
                }

                if ( r.getSuperclass() != null )
                {
                    candidates.addFirst( r.getSuperclass() );
                }
            }

            if ( req.getRole() == null )
            {
                if ( name != null )
                {
                    throw new IllegalStateException(
                                                     String.format(
                                                                    "Cannot find component to satisfy requirement: %s\nRole: %s\nHint: %s",
                                                                    elementName, elementType.getName(), name ) );
                }
                else
                {
                    throw new IllegalStateException(
                                                     String.format(
                                                                    "Cannot find component to satisfy requirement: %s\nRole: %s",
                                                                    elementName, elementType.getName() ) );
                }
            }
        }
    }

    @Test
    public void getByPath_ReturnNullIfAuditInfoMissing()
        throws AuditStoreException
    {
        final List<Repository> repos = reg.getRepositories();

        System.out.println( "Found repositories:\n\n" );
        for ( final Repository r : repos )
        {
            System.out.println( r.getId() );
        }

        assertNull( store.getAuditInformation( "/missing/path.txt", "releases" ) );
    }
}
