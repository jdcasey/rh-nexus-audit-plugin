package com.redhat.jcasey.test.nexus.plugin.rest;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.component.annotations.Requirement;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.repository.GroupRepository;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.repository.RepositoryKind;
import org.sonatype.plexus.rest.resource.AbstractPlexusResource;
import org.sonatype.plexus.rest.resource.PathProtectionDescriptor;
import org.sonatype.plexus.rest.resource.PlexusResource;

import com.redhat.devel.pp.PrettyPrinter;

/**
 * A sample rest resoruce to get you started. By default this will automatically be mounted at:
 * http://localhost:8081/nexus/service/local/sample/hello
 */
public class HelloWorldPlexusResource
    extends AbstractPlexusResource
    implements PlexusResource
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Requirement
    private RepositoryRegistry registry;

    private final Map<String, WeakReference<Repository>> captureRepos =
        new HashMap<String, WeakReference<Repository>>();

    @Override
    public Object getPayloadInstance()
    {
        // if you allow PUT or POST you would need to return your object.
        return null;
    }

    @Override
    public PathProtectionDescriptor getResourceProtection()
    {
        // to be controled by a new prermission
        // return new PathProtectionDescriptor( this.getResourceUri(), "authcBasic,perms[nexus:somepermission]" );

        // for an anonymous resoruce
        return new PathProtectionDescriptor( getResourceUri(), "anon" );
    }

    @Override
    public String getResourceUri()
    {
        // note this must start with a '/'
        return "/capture";
    }

    @Override
    public Object get( final Context context, final Request request, final Response response, final Variant variant )
        throws ResourceException
    {
        final List<Repository> repos = registry.getRepositories();

        final Element root = new Element( "info" );
        final Document doc = new Document( root );

        for ( final Repository repo : repos )
        {
            final String pp = PrettyPrinter.pp( repo );
            logger.info( pp );

            final Element child = new Element( "repo" );
            final List<Element> elements = new ArrayList<Element>();

            elements.add( new Element( "id" ).setText( repo.getId() ) );
            elements.add( new Element( "name" ).setText( repo.getName() ) );
            elements.add( new Element( "path-prefix" ).setText( repo.getPathPrefix() ) );
            elements.add( new Element( "status" ).setText( repo.getLocalStatus().name() ) );

            final RepositoryKind kind = repo.getRepositoryKind();
            elements.add( new Element( "group" ).setText( Boolean.toString( kind.isFacetAvailable( GroupRepository.class ) ) ) );

            child.addContent( elements );

            root.addContent( child );
        }

        final Reference ref = request.getResourceRef().getRelativeRef();

        final String[] parts = ref.getPath().split( "\\/" );

        for ( final String part : parts )
        {
            final Element child = new Element( "part" );
            child.setText( part );
            root.addContent( child );
        }

        return new XMLOutputter().outputString( doc );
    }
}