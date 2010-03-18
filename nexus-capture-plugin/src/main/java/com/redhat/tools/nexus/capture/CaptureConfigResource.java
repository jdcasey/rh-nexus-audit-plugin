package com.redhat.tools.nexus.capture;

import static com.redhat.tools.nexus.capture.model.ModelSerializationUtils.getXStreamForConfig;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.rest.resource.PathProtectionDescriptor;
import org.sonatype.plexus.rest.resource.PlexusResource;

import com.redhat.tools.nexus.capture.config.CaptureConfigDTO;
import com.redhat.tools.nexus.capture.config.CaptureConfiguration;
import com.redhat.tools.nexus.capture.config.InvalidConfigurationException;
import com.redhat.tools.nexus.protocol.ProtocolConstants;

import javax.inject.Inject;
import javax.inject.Named;

@Named( "captureConfig" )
public class CaptureConfigResource
    extends AbstractNonResolverCaptureResource
    implements PlexusResource
{

    @Inject
    @Named( "xml" )
    private CaptureConfiguration configuration;

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    public CaptureConfigResource()
    {
        setModifiable( true );
    }

    @Override
    public Object getPayloadInstance()
    {
        return new CaptureConfigDTO();
    }

    @Override
    public PathProtectionDescriptor getResourceProtection()
    {
        return new PathProtectionDescriptor( ProtocolConstants.ADMIN_CONFIG_RESOURCE_FRAGMENT,
                                             String.format( "authcBasic,perms[%s]",
                                                            CaptureResourceConstants.PRIV_SETTINGS ) );
    }

    @Override
    public String getResourceUri()
    {
        return ProtocolConstants.ADMIN_CONFIG_RESOURCE_FRAGMENT;
    }

    @Override
    public Object get( final Context context, final Request request, final Response response, final Variant variant )
        throws ResourceException
    {
        logger.info( String.format( "Retrieving capture config with: %s",
                                    getXStreamForConfig().toXML( configuration.getModel() ) ) );

        return new CaptureConfigDTO( configuration.getModel() );
    }

    @Override
    public Object put( final Context context, final Request request, final Response response, final Object payload )
        throws ResourceException
    {
        logger.info( String.format( "Updating capture config with: %s", payload ) );
        if ( payload != null )
        {
            try
            {
                configuration.updateModel( ( (CaptureConfigDTO) payload ).getData() );
            }
            catch ( final InvalidConfigurationException e )
            {
                throw new ResourceException( Status.SERVER_ERROR_INTERNAL, e.getMessage() );
            }
        }
        else
        {
            throw new ResourceException( Status.CLIENT_ERROR_BAD_REQUEST,
                                         "No configuration data was provided in request." );
        }

        return new CaptureConfigDTO( configuration.getModel() );
    }

}
