package com.redhat.rcm.nexus.capture.template;

import static com.redhat.rcm.nexus.util.PathUtils.joinFile;
import static com.redhat.rcm.nexus.util.PathUtils.joinPath;
import static org.codehaus.plexus.util.FileUtils.fileRead;
import static org.codehaus.plexus.util.IOUtil.close;
import static org.codehaus.plexus.util.IOUtil.copy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.velocity.VelocityComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.configuration.application.ApplicationConfiguration;

@Named( "velocity" )
public class VelocityTemplateFormatter
    implements TemplateFormatter, Initializable
{

    private static final String DEFAULT_TEMPLATE_NAME = "default.vm";

    private static final Logger logger = LoggerFactory.getLogger( VelocityTemplateFormatter.class );

    private static final URLClassLoader LOCAL_LOADER;

    static
    {
        URLClassLoader ucl = null;

        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final String cpath = VelocityTemplateFormatter.class.getName().replace( '.', '/' ) + ".class";
        final URL resource = cl.getResource( cpath );

        if ( resource != null )
        {
            String path = resource.toExternalForm();
            final int idx = path.indexOf( '!' );
            if ( path.startsWith( "jar:" ) && idx > -1 )
            {
                path = path.substring( "jar:".length(), idx );
            }

            try
            {
                final URL base = new URL( path );
                ucl = new URLClassLoader( new URL[] { base } );
            }
            catch ( final MalformedURLException e )
            {
                logger.error( String.format( "Cannot create base URL for local plugin jar: %s\nReason: %s", path,
                                             e.getMessage() ), e );
            }
        }
        else
        {
            logger.error( "Cannot find classpath URL for: %s" );
        }

        LOCAL_LOADER = ucl;
    }

    @Inject
    private ApplicationConfiguration configuration;

    @Inject
    private VelocityComponent velocity;

    private File templatesDir;

    public VelocityTemplateFormatter()
    {
    }

    // for testing.
    protected VelocityTemplateFormatter( final VelocityComponent velocity, final File templatesDir )
    {
        this.velocity = velocity;
        this.templatesDir = templatesDir;
    }

    @Override
    public String format( final String templateBasepath, String templateName, final Map<String, Object> context )
        throws TemplateException
    {
        if ( templateName == null )
        {
            templateName = DEFAULT_TEMPLATE_NAME;
        }
        else if ( !templateName.endsWith( ".vm" ) )
        {
            templateName += ".vm";
        }

        final String templatePath = joinPath( '/', templateBasepath, templateName );
        final VelocityContext ctx = new VelocityContext( new HashMap<String, Object>( context ) );
        final StringWriter writer = new StringWriter();

        final String template = readTemplate( templatePath );

        if ( template == null )
        {
            throw new TemplateException( "Template not found: %s", templateName );
        }

        try
        {
            velocity.getEngine().evaluate( ctx, writer, templateName, template );
        }
        catch ( final ResourceNotFoundException e )
        {
            throw new TemplateException( "Failed to render capture session using Velocity template.\nTemplate: %s\nReason: %s",
                                         e,
                                         templatePath,
                                         e.getMessage() );
        }
        catch ( final ParseErrorException e )
        {
            throw new TemplateException( "Failed to render capture session using Velocity template.\nTemplate: %s\nReason: %s",
                                         e,
                                         templatePath,
                                         e.getMessage() );
        }
        catch ( final MethodInvocationException e )
        {
            throw new TemplateException( "Failed to render capture session using Velocity template.\nTemplate: %s\nReason: %s",
                                         e,
                                         templatePath,
                                         e.getMessage() );
        }
        catch ( final IOException e )
        {
            throw new TemplateException( "Failed to render capture session using Velocity template.\nTemplate: %s\nReason: %s",
                                         e,
                                         templatePath,
                                         e.getMessage() );
        }

        return writer.toString();
    }

    private String readTemplate( final String templatePath )
        throws TemplateException
    {
        String template = null;

        if ( templatesDir != null )
        {
            final File templateFile = new File( templatesDir, templatePath );

            if ( templateFile.exists() && templateFile.isFile() )
            {
                try
                {
                    template = fileRead( templateFile );
                }
                catch ( final IOException e )
                {
                    throw new TemplateException( "Failed to read Velocity template from: %s\nReason: %s",
                                                 e,
                                                 templateFile.getAbsolutePath(),
                                                 e.getMessage() );
                }
            }
        }

        if ( template == null )
        {
            final List<ClassLoader> cloaders = new ArrayList<ClassLoader>();
            if ( LOCAL_LOADER != null )
            {
                cloaders.add( LOCAL_LOADER );
            }
            cloaders.add( Thread.currentThread().getContextClassLoader() );

            for ( final ClassLoader cloader : cloaders )
            {
                final URL resource = cloader.getResource( templatePath );
                if ( resource != null )
                {
                    final StringWriter sw = new StringWriter();
                    InputStream stream = null;
                    try
                    {
                        stream = resource.openStream();
                        copy( stream, sw );

                        template = sw.toString();
                    }
                    catch ( final IOException e )
                    {
                        throw new TemplateException( "Failed to read Velocity template from: %s\nReason: %s",
                                                     e,
                                                     resource.toExternalForm(),
                                                     e.getMessage() );
                    }
                    finally
                    {
                        close( stream );
                    }
                }

                if ( template != null )
                {
                    break;
                }
            }
        }

        return template;
    }

    @Override
    public void initialize()
        throws InitializationException
    {
        if ( configuration != null )
        {
            templatesDir = joinFile( configuration.getConfigurationDirectory(), "capture" );
            try
            {
                final File d = templatesDir.getCanonicalFile();
                templatesDir = d;
            }
            catch ( final IOException e )
            {
            }
        }
    }
}
