package com.redhat.tools.nexus.capture;

import static com.redhat.tools.nexus.request.RequestUtils.mediaTypeOf;
import static com.redhat.tools.nexus.request.RequestUtils.parseDate;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.rest.resource.PathProtectionDescriptor;
import org.sonatype.plexus.rest.resource.PlexusResource;

import com.redhat.tools.nexus.capture.model.CaptureSession;
import com.redhat.tools.nexus.capture.model.CaptureSessionRef;
import com.redhat.tools.nexus.capture.store.CaptureStore;
import com.redhat.tools.nexus.capture.store.CaptureStoreException;
import com.redhat.tools.nexus.protocol.ProtocolConstants;
import com.redhat.tools.nexus.response.WebResponseSerializer;
import com.redhat.tools.nexus.template.TemplateFormatter;

import javax.inject.Inject;
import javax.inject.Named;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named( "captureLog" )
public class CaptureLogResource
    extends AbstractNonResolverCaptureResource
    implements PlexusResource
{

    public static final String LOG_TEMPLATE_BASEPATH = TemplateFormatter.TEMPLATES_ROOT + "capture/logs";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    @Named( "json" )
    private CaptureStore captureStore;

    @Inject
    @Named( "capture" )
    private WebResponseSerializer responseSerializer;

    @Override
    public Object getPayloadInstance()
    {
        // if you allow PUT or POST you would need to return your object.
        return null;
    }

    @Override
    public PathProtectionDescriptor getResourceProtection()
    {
        return new PathProtectionDescriptor( ProtocolConstants.LOG_RESOURCE_FRAGMENT + "/*/*/*",
                                             String.format( "authcBasic,perms[%s]",
                                                            CaptureResourceConstants.PRIV_LOG_ACCESS ) );
    }

    @Override
    public String getResourceUri()
    {
        return ProtocolConstants.LOG_RESOURCE_FRAGMENT + "/{" + CaptureResourceConstants.ATTR_USER + "}/{"
            + CaptureResourceConstants.ATTR_BUILD_TAG_REPO_ID + "}/{" + CaptureResourceConstants.ATTR_DATE + "}";
    }

    @Override
    public Object get( final Context context, final Request request, final Response response, final Variant variant )
        throws ResourceException
    {
        final String user = request.getAttributes().get( CaptureResourceConstants.ATTR_USER ).toString();

        final String buildTag =
            request.getAttributes().get( CaptureResourceConstants.ATTR_BUILD_TAG_REPO_ID ).toString();

        final String dateValue = request.getAttributes().get( CaptureResourceConstants.ATTR_DATE ).toString();

        Object data = null;
        try
        {
            final Date date = parseDate( dateValue );

            final CaptureSessionRef ref = new CaptureSessionRef( user, buildTag, date );

            final CaptureSession session = captureStore.readLog( ref );
            if ( session != null )
            {
                data = session.asResource( request.getRootRef().toString(), getRepositoryRegistry() );
            }
            else
            {
                throw new ResourceException( Status.CLIENT_ERROR_NOT_FOUND );
            }
        }
        catch ( final CaptureStoreException e )
        {
            final String message =
                String.format( "Failed to retrieve capture log. Error: %s\nMessage: %s", e.getClass().getName(),
                               e.getMessage() );

            logger.error( message, e );
            e.printStackTrace();

            throw new ResourceException( Status.SERVER_ERROR_INTERNAL, message );
        }
        catch ( final ParseException e )
        {
            final String message =
                String.format( "Failed to retrieve capture log. Invalid date format: '%s' Error: %s\nMessage: %s",
                               dateValue, e.getClass().getName(), e.getMessage() );

            logger.error( message, e );
            e.printStackTrace();

            throw new ResourceException( Status.SERVER_ERROR_INTERNAL, message );
        }

        final MediaType mt = mediaTypeOf( request, variant );
        return responseSerializer.serialize( data, mt, request, LOG_TEMPLATE_BASEPATH );
    }

    @Override
    public List<Variant> getVariants()
    {
        final List<Variant> variants = new ArrayList<Variant>();

        variants.add( new Variant( MediaType.APPLICATION_XML ) );
        variants.add( new Variant( MediaType.APPLICATION_JSON ) );
        variants.add( new Variant( MediaType.TEXT_PLAIN ) );

        return variants;
    }

}