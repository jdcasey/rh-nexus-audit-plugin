/*
 * Sonatype Application Build Lifecycle
 * Copyright (C) 2009 Sonatype, Inc.                                                                                                                          
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 */
package com.redhat.tools.nexus.maven.plugin;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PrefixAwareRecursionInterceptor;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link ApplicationInformation}, which supplies Nexus-specific default configurations, plugin
 * packaging, and application-core groupIds. This is simpler to maintain for now than an XML configuration.
 * 
 * @author jdcasey
 * 
 */
public class NexusApplicationInformation
{

    private static final List<String> PROJECT_PREFIXES;

    static
    {
        final List<String> prefixes = new ArrayList<String>();

        prefixes.add( "project." );
        prefixes.add( "pom." );

        PROJECT_PREFIXES = Collections.unmodifiableList( prefixes );
    }

    private Set<String> coreGroupIdPatterns;

    private String pluginPackaging;

    private String pluginMetadataPath;

    private String applicationId;

    private String applicationMinVersion;

    private String applicationMaxVersion;

    private String applicationEdition;

    public NexusApplicationInformation()
    {
        addCoreGroupIdPattern( "org.sonatype.nexus" );
        addCoreGroupIdPattern( "com.sonatype.nexus" );

        setPluginPackaging( "nexus-plugin" );

        setApplicationId( "nexus" );
        setPluginMetadataPath( "${project.build.outputDirectory}/META-INF/nexus/plugin.xml" );

        setApplicationMinVersion( "1.4.0" );

        setApplicationEdition( "OSS" );
    }

    /**
     * Interpolate any project references in the plugin metadata output path, returning a {@link File} reference to the
     * interpolated path.
     * 
     * @see ApplicationInformation#setPluginMetadataPath(String)
     */
    public File getPluginMetadataFile( final MavenProject project )
        throws InterpolationException
    {
        return interpolateToFile( getPluginMetadataPath(), project );
    }

    /**
     * Determine whether the specified groupId matches any of those specified as core groupIds for this application. If
     * there are no application core groupIds, return false. If the groupId matches one of the core groupIds using
     * {@link String#equals(String)}, or using {@link String#matches(String)}, then return true.
     */
    public boolean matchesCoreGroupIds( final String groupId )
    {
        boolean matchedCoreGroupId = false;
        if ( getCoreGroupIdPatterns() != null )
        {
            for ( final String pattern : getCoreGroupIdPatterns() )
            {
                if ( groupId.equals( pattern ) || groupId.matches( pattern ) )
                {
                    matchedCoreGroupId = true;
                    break;
                }
            }
        }

        return matchedCoreGroupId;
    }

    /**
     * @see ApplicationInformation#setCoreGroupIdPatterns(Set)
     */
    public void addCoreGroupIdPattern( final String coreGroupIdPattern )
    {
        if ( coreGroupIdPatterns == null )
        {
            coreGroupIdPatterns = new HashSet<String>();
        }

        coreGroupIdPatterns.add( coreGroupIdPattern );
    }

    /**
     * @see ApplicationInformation#setCoreGroupIdPatterns(Set)
     */
    public Set<String> getCoreGroupIdPatterns()
    {
        return coreGroupIdPatterns;
    }

    /**
     * These are the groupId patterns that are meant to be present ONLY in the application's core. They can be either
     * groupId prefixes (or whole groupIds), or they can be regular expressions. <br/>
     * The mojos in the app-lifecycle-maven-plugin will require that any plugin dependency with a matching groupId be
     * declared with the 'provided' scope. These dependencies will be excluded from the plugin descriptor, and the
     * plugin bundle itself.
     */
    public void setCoreGroupIdPatterns( final Set<String> coreGroupIdPatterns )
    {
        this.coreGroupIdPatterns = coreGroupIdPatterns;
    }

    /**
     * @see ApplicationInformation#setPluginPackaging(String)
     */
    public String getPluginPackaging()
    {
        return pluginPackaging;
    }

    /**
     * This is the POM packaging (also, the dependency type) used for plugins in this application. Plugin dependencies
     * with this type specification MUST be declared with 'provided' scope, and will be included in a separate section
     * of the plugin descriptor from its external dependencies. Inter-plugin dependencies will later be resolved using
     * the application's plugin manager.
     */
    public void setPluginPackaging( final String pluginPackaging )
    {
        this.pluginPackaging = pluginPackaging;
    }

    /**
     * @see ApplicationInformation#setPluginMetadataPath(String)
     */
    public String getPluginMetadataPath()
    {
        return pluginMetadataPath;
    }

    /**
     * Path where the plugin descriptor should be written during the build. This path may make reference to Maven
     * project expressions just like any plugin or POM would. <br/>
     * Normally, this path will start with ${project.build.outputDirectory/META-INF/.
     */
    public void setPluginMetadataPath( final String pluginMetadataFile )
    {
        pluginMetadataPath = pluginMetadataFile;
    }

    /**
     * Default application ID.
     */
    public String getApplicationId()
    {
        return applicationId;
    }

    /**
     * Default application ID.
     */
    public void setApplicationId( final String applicationId )
    {
        this.applicationId = applicationId;
    }

    /**
     * @see ApplicationInformation#setApplicationMinVersion(String)
     */
    public String getApplicationMinVersion()
    {
        return applicationMinVersion;
    }

    /**
     * The default minimum application version with which this plugin being built is compatible.
     */
    public void setApplicationMinVersion( final String applicationMinVersion )
    {
        this.applicationMinVersion = applicationMinVersion;
    }

    /**
     * @see ApplicationInformation#setApplicationMaxVersion(String)
     */
    public String getApplicationMaxVersion()
    {
        return applicationMaxVersion;
    }

    /**
     * The default maximum application version with which this plugin being built is compatible.
     */
    public void setApplicationMaxVersion( final String applicationMaxVersion )
    {
        this.applicationMaxVersion = applicationMaxVersion;
    }

    /**
     * @see ApplicationInformation#setApplicationEdition(String)
     */
    public String getApplicationEdition()
    {
        return applicationEdition;
    }

    /**
     * The default edition of this application (OSS, Pro, etc.) with which this plugin is meant to work.
     */
    public void setApplicationEdition( final String applicationEdition )
    {
        this.applicationEdition = applicationEdition;
    }

    private File interpolateToFile( final String pattern, final MavenProject project )
        throws InterpolationException
    {
        if ( pattern == null )
        {
            return null;
        }

        final Interpolator interpolator = new StringSearchInterpolator();
        interpolator.addValueSource( new PrefixedObjectValueSource( PROJECT_PREFIXES, project, false ) );

        final RecursionInterceptor ri = new PrefixAwareRecursionInterceptor( PROJECT_PREFIXES );

        return new File( interpolator.interpolate( pattern, ri ) );
    }

}
