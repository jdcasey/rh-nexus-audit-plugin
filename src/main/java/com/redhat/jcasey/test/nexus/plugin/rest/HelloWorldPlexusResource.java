package com.redhat.jcasey.test.nexus.plugin.rest;

import org.jdom.Document;
import org.jdom.Element;
import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.rest.resource.AbstractPlexusResource;
import org.sonatype.plexus.rest.resource.PathProtectionDescriptor;
import org.sonatype.plexus.rest.resource.PlexusResource;

/**
 * A sample rest resoruce to get you started. By default this will automatically be mounted at:
 * http://localhost:8081/nexus/service/local/sample/hello
 */
public class HelloWorldPlexusResource
    extends AbstractPlexusResource
    implements PlexusResource
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

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

        // you can basically return any object, and it will be serialized

        // we will keep it simple
        logger.info( request.getResourceRef()
                            .getQuery() );

        final Element root = new Element( "info" );
        final Document doc = new Document( root );

        final Reference ref = request.getResourceRef()
                                     .getRelativeRef();
        final String[] parts = ref.getPath()
                                  .split( "\\/" );
        for ( final String part : parts )
        {
            final Element child = new Element( "part" );
            child.setText( part );
            root.addContent( child );
        }

        return doc;
    }
}