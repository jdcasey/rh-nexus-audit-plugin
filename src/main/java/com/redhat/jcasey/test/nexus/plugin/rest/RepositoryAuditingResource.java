package com.redhat.jcasey.test.nexus.plugin.rest;

import java.io.IOException;

import org.codehaus.plexus.component.annotations.Component;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.proxy.AccessDeniedException;
import org.sonatype.nexus.proxy.IllegalOperationException;
import org.sonatype.nexus.proxy.ItemNotFoundException;
import org.sonatype.nexus.proxy.NoSuchResourceStoreException;
import org.sonatype.nexus.proxy.ResourceStore;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.StorageException;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.rest.AbstractResourceStoreContentPlexusResource;
import org.sonatype.plexus.rest.resource.PathProtectionDescriptor;
import org.sonatype.plexus.rest.resource.PlexusResource;

/**
 * Capture resource, which will try to resolve first from a build-tag repository (first part of URL after /capture/),
 * then from a capture-source repository (second part of the URL after /capture/). <br/>
 * NOTE: If the user does not have access to the capture-source repository, the retrieve attempt will fail.
 */
@Component( role = PlexusResource.class, hint = "RepositoryAuditingResource" )
public class RepositoryAuditingResource
    extends AbstractResourceStoreContentPlexusResource
    implements PlexusResource
{

    private static final String CAPTURE_SOURCE_REPO_ID_KEY = "capture-source";

    private static final String BUILD_TAG_REPO_ID_KEY = "build-tag";

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
        // return new PathProtectionDescriptor( "/capture/*/*/content/**", "authcBasic" );
        return new PathProtectionDescriptor( "/capture/**", "authcBasic" );
    }

    @Override
    public String getResourceUri()
    {
        // return "/repositories/{" + AbstractRepositoryPlexusResource.REPOSITORY_ID_KEY + "}/content";
        // note this must start with a '/'
        return "/capture/{" + BUILD_TAG_REPO_ID_KEY + "}/{" + CAPTURE_SOURCE_REPO_ID_KEY + "}";
    }

    @Override
    public Object get( final Context context, final Request request, final Response response, final Variant variant )
        throws ResourceException
    {
        final ResourceStoreRequest req = getResourceStoreRequest( request );

        // request.getAttributes().get( AbstractRepositoryPlexusResource.REPOSITORY_ID_KEY ).toString()
        final String buildTag = request.getAttributes().get( BUILD_TAG_REPO_ID_KEY ).toString();
        final String capture = request.getAttributes().get( CAPTURE_SOURCE_REPO_ID_KEY ).toString();

        logger.info( "AUDIT REPO: Using build-tag: '{}' and capture-source: '{}'", buildTag, capture );

        try
        {
            logger.info( "Attempting to resolve: '{}' from build-tag repository: '{}'", req.getRequestPath(), buildTag );

            final Repository buildTagRepo = getUnprotectedRepositoryRegistry().getRepository( buildTag );

            StorageItem item = null;
            try
            {
                item = buildTagRepo.retrieveItem( req );
            }
            catch ( final ItemNotFoundException e )
            {
                if ( capture == null )
                {
                    return handleNotFound( e, context, request, response, variant, req );
                }

                // FIXME: Can we be more pro-active to determine whether the user has access to even attempt this part??
                logger.info(
                             "Resolve from build-tag repository: '{}' MISSED! Attempting to resolve from capture-source: '{}'",
                             buildTag, capture );

                final Repository captureRepo = getUnprotectedRepositoryRegistry().getRepository( capture );

                try
                {
                    item = captureRepo.retrieveItem( req );
                }
                catch ( final ItemNotFoundException eCap )
                {
                    return handleNotFound( eCap, context, request, response, variant, req );
                }
                catch ( final AccessDeniedException accessEx )
                {
                    logger.error( "Capture failed. Access to: '{}' was denied.", capture );

                    return null;
                }
            }

            return renderItem( context, request, response, variant, item );
        }
        catch ( final Exception e )
        {
            System.out.println( "Unprotected Registry:\n\n" );
            for ( final Repository repo : getUnprotectedRepositoryRegistry().getRepositories() )
            {
                System.out.println( repo.getId() );
            }

            System.out.println( "\n\nProtected Registry:\n\n" );
            for ( final Repository repo : getRepositoryRegistry().getRepositories() )
            {
                System.out.println( repo.getId() );
            }
            System.out.println( "\n\n" );

            logger.error( "Capture failed. Error: {}\nMessage: {}", e.getClass().getName(), e.getMessage() );
            e.printStackTrace();

            handleException( request, response, e );

            return null;
        }
    }

    private Object handleNotFound( final ItemNotFoundException e, final Context context, final Request request,
                                   final Response response, final Variant variant, final ResourceStoreRequest req )
        throws AccessDeniedException,
            StorageException,
            IOException,
            NoSuchResourceStoreException,
            IllegalOperationException,
            ItemNotFoundException,
            ResourceException
    {
        if ( isDescribe( request ) )
        {
            return renderDescribeItem( context, request, response, variant, req, null );
        }
        else
        {
            // FIXME: This will hide the build-tag source of the ItemNotFoundException...
            throw e;
        }
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