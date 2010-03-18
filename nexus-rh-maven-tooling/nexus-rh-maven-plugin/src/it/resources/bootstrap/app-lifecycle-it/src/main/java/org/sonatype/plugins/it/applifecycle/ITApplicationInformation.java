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
package org.sonatype.plugins.it.applifecycle;

import org.sonatype.maven.plugin.app.ApplicationInformation;

/**
 * Implementation of {@link ApplicationInformation}, which supplies Nexus-specific default configurations, plugin
 * packaging, and application-core groupIds. This is simpler to maintain for now than an XML configuration.
 * 
 * @author jdcasey
 * 
 */
public class ITApplicationInformation
    extends ApplicationInformation
{

    public ITApplicationInformation()
    {
        addCoreGroupIdPattern( "org.sonatype.plugins.it.applifecycle" );

        setPluginPackaging( "it-plugin" );

        setApplicationId( "it" );
        setPluginMetadataPath( "${project.build.outputDirectory}/META-INF/it/plugin.xml" );

        setApplicationMinVersion( "1.0" );

        setApplicationEdition( "OSS" );
    }
}
