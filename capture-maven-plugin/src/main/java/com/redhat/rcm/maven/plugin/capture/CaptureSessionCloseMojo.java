package com.redhat.rcm.maven.plugin.capture;

import static com.redhat.rcm.nexus.protocol.ProtocolUtils.getGson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.proxy.ProxyInfo;

import com.redhat.rcm.nexus.protocol.CaptureSessionRefResource;

/**
 * @goal close-session
 * @author jdcasey
 * 
 */
public class CaptureSessionCloseMojo
    implements Mojo
{

    private Log log;

    /**
     * @parameter default-value="${reactorProjects}"
     * @required
     * @readonly
     */
    private List<MavenProject> allProjects;

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     */
    private WagonManager wagonManager;

    @Override
    public void execute()
        throws MojoExecutionException,
            MojoFailureException
    {
        if ( allProjects.size() == 1 || project == allProjects.get( allProjects.size() - 1 ) )
        {
            final ArtifactRepository mirrorRepository =
                wagonManager.getMirrorRepository( (ArtifactRepository) project.getRemoteArtifactRepositories().get( 0 ) );

            URL url;
            try
            {
                url = new URL( mirrorRepository.getUrl() );
            }
            catch ( final MalformedURLException e )
            {
                throw new MojoExecutionException( String.format( "Invalid mirror URL: %s\n%s",
                                                                 mirrorRepository.getUrl(), e.getMessage() ), e );
            }

            final AuthenticationInfo authenticationInfo = wagonManager.getAuthenticationInfo( mirrorRepository.getId() );
            final ProxyInfo proxy = wagonManager.getProxy( url.getProtocol() );

            final DefaultHttpClient client = new DefaultHttpClient();

            if ( proxy != null
                            && ( proxy.getNonProxyHosts() == null || proxy.getNonProxyHosts().indexOf( url.getHost() ) < 0 ) )
            {
                final HttpHost proxyHost = new HttpHost( proxy.getHost(), proxy.getPort() );

                client.getParams().setParameter( ConnRoutePNames.DEFAULT_PROXY, proxyHost );

                if ( proxy.getUserName() != null )
                {
                    client.getCredentialsProvider()
                          .setCredentials( new AuthScope( proxy.getHost(), proxy.getPort() ),
                                           new UsernamePasswordCredentials( proxy.getUserName(), proxy.getPassword() ) );
                }
            }

            if ( authenticationInfo != null && authenticationInfo.getUserName() != null )
            {
                client.getCredentialsProvider()
                      .setCredentials(
                                       new AuthScope( url.getHost(), url.getPort() ),
                                       new UsernamePasswordCredentials( authenticationInfo.getUserName(),
                                                                        authenticationInfo.getPassword() ) );
            }

            // FIXME: This is the WRONG URL!!! Need the /my/logs/ url...
            final HttpPut put = new HttpPut( mirrorRepository.getUrl() );
            put.addHeader( "Accept", "application/json" );

            CaptureSessionRefResource sessionRef;
            try
            {
                sessionRef = client.execute( put, new ResponseHandler<CaptureSessionRefResource>()
                {
                    @Override
                    public CaptureSessionRefResource handleResponse( final HttpResponse response )
                        throws ClientProtocolException,
                            IOException
                    {
                        return getGson().fromJson( new InputStreamReader( response.getEntity().getContent() ),
                                                   CaptureSessionRefResource.class );
                    }
                } );
            }
            catch ( final ClientProtocolException e )
            {
                throw new MojoExecutionException( String.format(
                                                                 "Failed to close capture session for user: %s\nat URL: %s\nReason: %s",
                                                                 authenticationInfo.getUserName(),
                                                                 mirrorRepository.getUrl(), e.getMessage() ),
                                                  e );
            }
            catch ( final IOException e )
            {
                throw new MojoExecutionException( String.format(
                                                                 "Failed to close capture session for user: %s\nat URL: %s\nReason: %s",
                                                                 authenticationInfo.getUserName(),
                                                                 mirrorRepository.getUrl(), e.getMessage() ),
                                                  e );
            }

            getLog().info( String.format( "Closed capture session: %s", sessionRef.getUrl() ) );
        }
        else
        {
            final int idx = allProjects.indexOf( project );
            getLog().info(
                           String.format( "Waiting for last project before closing capture-session.\n"
                                           + "Currently processing project %d out of %d", ( idx + 1 ),
                                          allProjects.size() ) );
        }
    }

    @Override
    public Log getLog()
    {
        return log;
    }

    @Override
    public void setLog( final Log log )
    {
        this.log = log;
    }

}
