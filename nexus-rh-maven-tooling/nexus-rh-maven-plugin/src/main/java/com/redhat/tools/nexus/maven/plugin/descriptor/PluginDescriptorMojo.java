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
package com.redhat.tools.nexus.maven.plugin.descriptor;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.License;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.svn.AbstractSvnScmProvider;
import org.apache.maven.scm.provider.svn.command.info.SvnInfoItem;
import org.apache.maven.scm.provider.svn.command.info.SvnInfoScmResult;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.util.StringUtils;
import org.sonatype.plugin.ExtensionPoint;
import org.sonatype.plugin.Managed;
import org.sonatype.plugin.metadata.GAVCoordinate;
import org.sonatype.plugin.metadata.PluginMetadataGenerationRequest;
import org.sonatype.plugin.metadata.PluginMetadataGenerator;
import org.sonatype.plugin.metadata.gleaner.GleanerException;

import com.redhat.tools.nexus.maven.plugin.ClasspathUtils;
import com.redhat.tools.nexus.maven.plugin.NexusApplicationInformation;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generates a plugin's <tt>plugin.xml</tt> descriptor file based on the project's pom and class annotations.
 * 
 * @goal generate-metadata
 * @phase process-classes
 * @requiresDependencyResolution test
 */
public class PluginDescriptorMojo
    extends AbstractMojo
{

    /**
     * Instructions for selecting dependency artifacts to be bundles, vs. those to be excluded
     * (as in cases where they're shaded into the main artifact).
     * 
     * @parameter
     */
    private ArtifactSet artifactSet;

    /**
     * A list of groupId:artifactId references to non-plugin dependencies that contain components which should be
     * gleaned for this plugin build.
     * 
     * @parameter
     */
    private List<String> componentDependencies;

    /**
     * The output location for the generated plugin descriptor. <br/>
     * <b>NOTE:</b> Default value for this field is supplied by the {@link ApplicationInformation} component included
     * via build extension.
     * 
     * @parameter
     */
    private File generatedPluginMetadata;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /**
     * The ID of the target application. For example if this plugin was for the Nexus Repository Manager, the ID would
     * be, 'nexus'. <br/>
     * <b>NOTE:</b> Default value for this field is supplied by the {@link ApplicationInformation} component included
     * via build extension.
     * 
     * @parameter
     */
    private String applicationId;

    /**
     * The edition of the target application. Some applications come in multiple flavors, OSS, PRO, Free, light, etc. <br/>
     * <b>NOTE:</b> Default value for this field is supplied by the {@link ApplicationInformation} component included
     * via build extension.
     * 
     * @parameter expression="OSS"
     */
    private String applicationEdition;

    /**
     * The minimum product version of the target application. <br/>
     * <b>NOTE:</b> Default value for this field is supplied by the {@link ApplicationInformation} component included
     * via build extension.
     * 
     * @parameter
     */
    private String applicationMinVersion;

    /**
     * The maximum product version of the target application. <br/>
     * <b>NOTE:</b> Default value for this field is supplied by the {@link ApplicationInformation} component included
     * via build extension, if it specified at all.
     * 
     * @parameter
     */
    private String applicationMaxVersion;

    /** @component */
    private PluginMetadataGenerator metadataGenerator;

    private static final NexusApplicationInformation mapping = new NexusApplicationInformation();

    /**
     * @parameter expression="${project.scm.developerConnection}"
     * @readonly
     */
    private String urlScm;

    /**
     * The username that is used when connecting to the SCM system.
     * 
     * @parameter expression="${username}"
     * @since 1.0-beta-1
     */
    private String username;

    /**
     * The password that is used when connecting to the SCM system.
     * 
     * @parameter expression="${password}"
     * @since 1.0-beta-1
     */
    private String password;

    /**
     * @component
     */
    private ScmManager scmManager;

    @SuppressWarnings( "unchecked" )
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( !mavenProject.getPackaging().equals( mapping.getPluginPackaging() ) )
        {
            getLog().info( "Project is not of packaging type '" + mapping.getPluginPackaging() + "'." );
            return;
        }

        initConfig();

        final PluginMetadataGenerationRequest request = new PluginMetadataGenerationRequest();
        request.setGroupId( mavenProject.getGroupId() );
        request.setArtifactId( mavenProject.getArtifactId() );
        request.setVersion( mavenProject.getVersion() );
        request.setName( mavenProject.getName() );
        request.setDescription( mavenProject.getDescription() );
        request.setPluginSiteURL( mavenProject.getUrl() );

        request.setApplicationId( applicationId );
        request.setApplicationEdition( applicationEdition );
        request.setApplicationMinVersion( applicationMinVersion );
        request.setApplicationMaxVersion( applicationMaxVersion );

        // licenses
        if ( mavenProject.getLicenses() != null )
        {
            for ( final License mavenLicenseModel : (List<License>) mavenProject.getLicenses() )
            {
                request.addLicense( mavenLicenseModel.getName(), mavenLicenseModel.getUrl() );
            }
        }

        // scm information
        try
        {
            final ScmRepository repository = getScmRepository();

            final SvnInfoScmResult scmResult = scmInfo( repository, new ScmFileSet( mavenProject.getBasedir() ) );

            if ( !scmResult.isSuccess() )
            {
                throw new ScmException( scmResult.getCommandOutput() );
            }

            final SvnInfoItem info = (SvnInfoItem) scmResult.getInfoItems().get( 0 );

            request.setScmVersion( info.getLastChangedRevision() );
            request.setScmTimestamp( info.getLastChangedDate() );
        }
        catch ( final ScmException e )
        {
            getLog().warn( "Failed to get scm information: " + e.getMessage() );
        }

        // dependencies
        final List<Artifact> artifacts = mavenProject.getTestArtifacts();
        final Set<Artifact> classpathArtifacts = new HashSet<Artifact>();
        if ( artifacts != null )
        {
            final Set<String> excludedArtifactIds = new HashSet<String>();
            final ArtifactSelector selector =
                artifactSet == null ? null : new ArtifactSelector( mavenProject.getArtifact(), artifactSet );

            artifactLoop: for ( final Artifact artifact : artifacts )
            {
                final GAVCoordinate artifactCoordinate =
                    new GAVCoordinate( artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(),
                                       artifact.getClassifier(), artifact.getType() );

                if ( artifact.getType().equals( mapping.getPluginPackaging() ) )
                {
                    if ( artifact.isSnapshot() )
                    {
                        artifactCoordinate.setVersion( artifact.getBaseVersion() );
                    }

                    if ( !Artifact.SCOPE_PROVIDED.equals( artifact.getScope() ) )
                    {
                        throw new MojoFailureException( "Plugin dependency \"" + artifact.getDependencyConflictId()
                            + "\" must have the \"provided\" scope!" );
                    }

                    excludedArtifactIds.add( artifact.getId() );

                    request.addPluginDependency( artifactCoordinate );
                }
                else if ( Artifact.SCOPE_PROVIDED.equals( artifact.getScope() )
                    || Artifact.SCOPE_TEST.equals( artifact.getScope() ) )
                {
                    excludedArtifactIds.add( artifact.getId() );
                }
                else if ( ( Artifact.SCOPE_COMPILE.equals( artifact.getScope() ) || Artifact.SCOPE_RUNTIME.equals( artifact.getScope() ) )
                    && ( !mapping.matchesCoreGroupIds( artifact.getGroupId() ) ) )
                {
                    if ( artifact.getDependencyTrail() != null )
                    {
                        for ( final String trailId : (List<String>) artifact.getDependencyTrail() )
                        {
                            if ( excludedArtifactIds.contains( trailId ) )
                            {
                                getLog().debug(
                                                "Dependency artifact: "
                                                    + artifact.getId()
                                                    + " is part of the transitive dependency set for a dependency with 'provided' or 'test' scope: "
                                                    + trailId
                                                    + "\nThis artifact will be excluded from the plugin classpath." );
                                continue artifactLoop;
                            }
                        }
                    }

                    if ( selector == null || selector.isSelected( artifact ) )
                    {
                        if ( componentDependencies != null
                            && componentDependencies.contains( artifact.getGroupId() + ":" + artifact.getArtifactId() ) )
                        {
                            artifactCoordinate.setHasComponents( true );
                        }

                        request.addClasspathDependency( artifactCoordinate );
                        classpathArtifacts.add( artifact );
                    }
                    else
                    {
                        getLog().debug( "Excluding: " + artifact.getId() + "; excluded by artifactSet." );
                        excludedArtifactIds.add( artifact.getId() );
                    }
                }
            }
        }

        request.setOutputFile( generatedPluginMetadata );
        request.setClassesDirectory( new File( mavenProject.getBuild().getOutputDirectory() ) );
        try
        {
            if ( mavenProject.getCompileClasspathElements() != null )
            {
                for ( final String classpathElement : (List<String>) mavenProject.getCompileClasspathElements() )
                {
                    request.getClasspath().add( new File( classpathElement ) );
                }
            }
        }
        catch ( final DependencyResolutionRequiredException e )
        {
            throw new MojoFailureException( "Plugin failed to resolve dependencies: " + e.getMessage(), e );
        }

        request.getAnnotationClasses().add( ExtensionPoint.class );
        request.getAnnotationClasses().add( Managed.class );

        // do the work
        try
        {
            metadataGenerator.generatePluginDescriptor( request );
        }
        catch ( final GleanerException e )
        {
            throw new MojoFailureException( "Failed to generate plugin xml file: " + e.getMessage(), e );
        }

        try
        {
            ClasspathUtils.write( classpathArtifacts, mavenProject );
        }
        catch ( final IOException e )
        {
            throw new MojoFailureException( "Failed to generate classpath properties file: " + e.getMessage(), e );
        }
    }

    private void initConfig()
        throws MojoFailureException
    {
        if ( generatedPluginMetadata == null )
        {
            try
            {
                generatedPluginMetadata = mapping.getPluginMetadataFile( mavenProject );
            }
            catch ( final InterpolationException e )
            {
                throw new MojoFailureException( "Cannot calculate plugin metadata file location from expression: "
                    + mapping.getPluginMetadataPath(), e );
            }
        }

        applicationId = applicationId == null ? mapping.getApplicationId() : applicationId;
        applicationEdition = applicationEdition == null ? mapping.getApplicationEdition() : applicationEdition;
        applicationMinVersion =
            applicationMinVersion == null ? mapping.getApplicationMinVersion() : applicationMinVersion;
        applicationMaxVersion =
            applicationMaxVersion == null ? mapping.getApplicationMaxVersion() : applicationMaxVersion;
    }

    private ScmRepository getScmRepository()
        throws ScmException
    {
        if ( StringUtils.isEmpty( urlScm ) )
        {
            throw new ScmException( "No SCM URL found." );
        }

        ScmRepository repository;

        repository = scmManager.makeScmRepository( urlScm );

        final ScmProviderRepository scmRepo = repository.getProviderRepository();

        if ( !StringUtils.isEmpty( username ) )
        {
            scmRepo.setUser( username );
        }

        if ( !StringUtils.isEmpty( password ) )
        {
            scmRepo.setPassword( password );
        }

        return repository;
    }

    public SvnInfoScmResult scmInfo( final ScmRepository repository, final ScmFileSet fileSet )
        throws ScmException
    {
        final AbstractSvnScmProvider abstractSvnScmProvider =
            (AbstractSvnScmProvider) scmManager.getProviderByType( "svn" );
        return abstractSvnScmProvider.info( repository.getProviderRepository(), fileSet, null );
    }
}
