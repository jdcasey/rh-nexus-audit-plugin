package com.redhat.rcm.nexus.capture;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.proxy.NoSuchResourceStoreException;
import org.sonatype.nexus.proxy.ResourceStore;
import org.sonatype.nexus.rest.AbstractResourceStoreContentPlexusResource;
import org.sonatype.plexus.rest.resource.PathProtectionDescriptor;
import org.sonatype.plexus.rest.resource.PlexusResource;

import com.redhat.rcm.nexus.capture.store.CaptureStore;

@Component( role = PlexusResource.class, hint = "CaptureShowLogResource" )
public class CaptureShowLogResource
    extends AbstractResourceStoreContentPlexusResource
    implements PlexusResource
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Requirement( hint = "json" )
    private CaptureStore captureStore;

    @Override
    public Object getPayloadInstance()
    {
        // if you allow PUT or POST you would need to return your object.
        return null;
    }

    @Override
    public PathProtectionDescriptor getResourceProtection()
    {
        return new PathProtectionDescriptor( "/capture/*/log", String.format( "authcBasic,perms[%s]",
                                                                              CaptureResourceConstants.CAPTURE_PERMISSION ) );
    }

    @Override
    public String getResourceUri()
    {
        return "/capture/{" + CaptureResourceConstants.BUILD_TAG_REPO_ID_KEY + "}/log";
    }

    @Override
    public Object get( final Context context, final Request request, final Response response, final Variant variant )
        throws ResourceException
    {
        return null;
    }

    // NOTE: Not Used. We're overriding the method that requires this.
    @Override
    protected ResourceStore getResourceStore( final Request request )
        throws NoSuchResourceStoreException,
            ResourceException
    {
        return null;
    }
}