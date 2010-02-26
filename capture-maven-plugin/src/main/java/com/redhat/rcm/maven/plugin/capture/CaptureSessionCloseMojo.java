package com.redhat.rcm.maven.plugin.capture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.DefaultProjectBuilderConfiguration;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.settings.Settings;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.util.IOUtil;

import com.redhat.rcm.nexus.protocol.ProtocolConstants;

/**
 * @goal close-session
 * @requiresProject false
 */
public class CaptureSessionCloseMojo
    implements Mojo
{

    private static final List<String> YN_VALUES =
        Arrays.asList( new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() } );

    private Log log;

    /**
     * @parameter default-value="${reactorProjects}"
     * @required
     * @readonly
     */
    private List<MavenProject> allProjects;

    /**
     * @parameter default-value="${settings}"
     * @required
     * @readonly
     */
    private Settings settings;

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     */
    private MavenProjectBuilder projectBuilder;

    /**
     * @component
     */
    private WagonManager wagonManager;

    /**
     * @component
     */
    private Prompter prompter;

    /**
     * @parameter default-value="maven-close-session"
     */
    private String template;

    @Override
    public void execute()
        throws MojoExecutionException,
            MojoFailureException
    {
        if ( allProjects == null || allProjects.size() < 2
                        || ( project != null && project == allProjects.get( allProjects.size() - 1 ) ) )
        {
            verifyProjectIsPresent();

            final ArtifactRepository mirrorRepository =
                wagonManager.getMirrorRepository( (ArtifactRepository) project.getRemoteArtifactRepositories().get( 0 ) );

            AuthenticationInfo authenticationInfo = wagonManager.getAuthenticationInfo( mirrorRepository.getId() );
            ProxyInfo proxy = wagonManager.getProxy( mirrorRepository.getProtocol() );
            String logUrl = buildMyLogUrl( mirrorRepository.getUrl() );

            if ( settings.isInteractiveMode() )
            {
                // defensive copies, in case we change it below...
                final AuthenticationInfo a = new AuthenticationInfo();
                a.setPassword( authenticationInfo.getPassword() );
                a.setUserName( authenticationInfo.getUserName() );

                authenticationInfo = a;

                if ( proxy != null )
                {
                    final ProxyInfo p = new ProxyInfo();
                    p.setHost( proxy.getHost() );
                    p.setNonProxyHosts( proxy.getNonProxyHosts() );
                    p.setNtlmDomain( proxy.getNtlmDomain() );
                    p.setNtlmHost( proxy.getNtlmHost() );
                    p.setPassword( proxy.getPassword() );
                    p.setPort( proxy.getPort() );
                    p.setType( proxy.getType() );
                    p.setUserName( proxy.getUserName() );

                    proxy = p;
                }

                logUrl = verifyAndCorrectInfo( authenticationInfo, proxy, logUrl );
            }

            final StringBuilder query = new StringBuilder();
            query.append( "?strict=true&template=" ).append( template );

            final String queryString = query.toString();
            if ( !logUrl.endsWith( queryString ) )
            {
                logUrl += queryString;
            }

            closeSession( logUrl, authenticationInfo, proxy );
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

    private void closeSession( final String url, final AuthenticationInfo authenticationInfo, final ProxyInfo proxy )
        throws MojoExecutionException
    {
        URL urlObject;
        try
        {
            urlObject = new URL( url );
        }
        catch ( final MalformedURLException e )
        {
            throw new MojoExecutionException( String.format( "Invalid mirror URL: %s\n%s", url, e.getMessage() ), e );
        }

        final DefaultHttpClient client = new DefaultHttpClient();

        if ( proxy != null
                        && ( proxy.getNonProxyHosts() == null || proxy.getNonProxyHosts().indexOf( urlObject.getHost() ) < 0 ) )
        {
            final HttpHost proxyHost = new HttpHost( proxy.getHost(), proxy.getPort() );

            client.getParams().setParameter( ConnRoutePNames.DEFAULT_PROXY, proxyHost );

            if ( proxy.getUserName() != null )
            {
                client.getCredentialsProvider().setCredentials(
                                                                new AuthScope( proxy.getHost(), proxy.getPort() ),
                                                                new UsernamePasswordCredentials( proxy.getUserName(),
                                                                                                 proxy.getPassword() ) );
            }
        }

        if ( authenticationInfo != null && authenticationInfo.getUserName() != null )
        {
            client.getCredentialsProvider()
                  .setCredentials(
                                   new AuthScope( urlObject.getHost(), urlObject.getPort() ),
                                   new UsernamePasswordCredentials( authenticationInfo.getUserName(),
                                                                    authenticationInfo.getPassword() ) );
        }

        final HttpPost method = new HttpPost( url );
        method.addHeader( "Accept", "text/plain" );
        // try
        // {
        // method.setEntity( new StringEntity( "foo" ) );
        // }
        // catch ( final UnsupportedEncodingException e )
        // {
        // throw new MojoExecutionException( "Failed to set dummy request body: " + e.getMessage(), e );
        // }

        StatusLine status = null;
        try
        {
            status = client.execute( method, new ResponseHandler<StatusLine>()
            {
                @Override
                public StatusLine handleResponse( final HttpResponse response )
                    throws ClientProtocolException,
                        IOException
                {
                    final StatusLine status = response.getStatusLine();
                    if ( status.getStatusCode() > 199 && status.getStatusCode() < 300 )
                    {
                        getLog().debug( "HTTP response status: " + status );

                        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        IOUtil.copy( response.getEntity().getContent(), baos );

                        getLog().info( String.valueOf( baos.toByteArray() ) );

                        return null;
                    }

                    return status;
                }
            } );
        }
        catch ( final ClientProtocolException e )
        {
            throw new MojoExecutionException( String.format(
                                                             "Failed to close capture session for user: %s\nat URL: %s\nReason: %s",
                                                             authenticationInfo.getUserName(), url, e.getMessage() ),
                                              e );
        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( String.format(
                                                             "Failed to close capture session for user: %s\nat URL: %s\nReason: %s",
                                                             authenticationInfo.getUserName(), url, e.getMessage() ),
                                              e );
        }

        if ( status != null )
        {
            throw new MojoExecutionException( String.format(
                                                             "\n\n\nFailed to close capture session.\nStatus: %s\n\n\n",
                                                             status ) );
        }
    }

    private void verifyProjectIsPresent()
        throws MojoExecutionException
    {
        if ( project == null )
        {
            try
            {
                project = projectBuilder.buildStandaloneSuperProject( new DefaultProjectBuilderConfiguration() );
            }
            catch ( final ProjectBuildingException e )
            {
                throw new MojoExecutionException( "Error retrieving built-in super-project to determine mirror URL: "
                                + e.getMessage(), e );
            }
        }
    }

    private String verifyAndCorrectInfo( final AuthenticationInfo authInfo, final ProxyInfo proxy, final String logUrl )
        throws MojoExecutionException
    {
        final StringBuilder sb = new StringBuilder();
        String url = logUrl;

        sb.append( "\n\n\n\nDetected the following Nexus connection settings:" );
        sb.append( "\n\nLogs URL: " ).append( url );

        sb.append( "\n\nAuthentication Info:" );
        sb.append( "\n============================================" );
        sb.append( "\nUser: " ).append( authInfo.getUserName() == null ? "Not Provided" : authInfo.getUserName() );
        sb.append( "\nPassword: " ).append( authInfo.getPassword() == null ? "Not Provided" : "**********" );

        if ( proxy != null )
        {
            sb.append( "\n\nProxy Info:" );
            sb.append( "\n============================================" );
            sb.append( "\nProxy Protocol: " ).append( proxy.getNonProxyHosts() );
            sb.append( "\nProxy Host/Port: " ).append( proxy.getHost() ).append( '/' ).append( proxy.getPort() );
            sb.append( "\nProxy User: " ).append( proxy.getUserName() == null ? "Not Provided" : proxy.getUserName() );
            sb.append( "\nProxy Password: " ).append( proxy.getPassword() == null ? "Not Provided" : "**********" );
            sb.append( "\nNon-Proxy Hosts: " ).append( proxy.getNonProxyHosts() );
        }

        getLog().info( sb.toString() );

        try
        {
            final boolean correct =
                Boolean.parseBoolean( prompter.prompt( "Are these settings correct?", YN_VALUES,
                                                       Boolean.TRUE.toString() ) );

            if ( !correct )
            {
                while ( true )
                {
                    final String u = prompter.prompt( "Logs URL:", url );
                    try
                    {
                        new URL( u );
                        url = u;
                        break;
                    }
                    catch ( final MalformedURLException e )
                    {
                        getLog().error( "Invalid URL: " + u );
                    }
                }

                authInfo.setUserName( prompter.prompt( "Nexus Username:", authInfo.getUserName() ) );
                authInfo.setUserName( prompter.prompt( "Nexus Password:" ) );

                if ( proxy != null )
                {
                    proxy.setHost( prompter.prompt( "Proxy Host:", proxy.getHost() ) );

                    while ( true )
                    {
                        final String i = prompter.prompt( "Proxy Port:", Integer.toString( proxy.getPort() ) );
                        try
                        {
                            final int port = Integer.parseInt( i );
                            proxy.setPort( port );
                            break;
                        }
                        catch ( final NumberFormatException e )
                        {
                            getLog().error( "Invalid port number: " + i );
                        }
                    }

                    proxy.setType( prompter.prompt( "Proxy Protocol:", proxy.getType() ) );
                    proxy.setNonProxyHosts( prompter.prompt( "Non-Proxy Hosts:", proxy.getNonProxyHosts() ) );
                    // proxy.setNtlmDomain( prompter.prompt( "NTLM Domain:", proxy.getNtlmDomain() ) );
                    // proxy.setNtlmHost( prompter.prompt( "NTLM Host:", proxy.getNtlmHost() ) );
                    proxy.setUserName( prompter.prompt( "Proxy User:", proxy.getUserName() ) );
                    proxy.setPassword( prompter.prompt( "Proxy Password:" ) );
                }
            }
        }
        catch ( final PrompterException e )
        {
            throw new MojoExecutionException( "Prompting for user input has failed. Reason: " + e.getMessage(), e );
        }

        return url;
    }

    private String buildMyLogUrl( final String resolveUrl )
        throws MojoExecutionException
    {
        final StringBuilder sb = new StringBuilder();

        final String resourceFragment = ProtocolConstants.RESOLVE_RESOURCE_BASEURI;
        final int idx = resolveUrl.indexOf( resourceFragment );
        if ( idx < 1 )
        {
            throw new MojoExecutionException( String.format( "Mirror URL is not a capture URL: '%s'", resolveUrl ) );
        }
        else
        {
            sb.append( resolveUrl.substring( 0, idx ) );
            sb.append( ProtocolConstants.MY_LOGS_RESOURCE_BASEURI );
            sb.append( resolveUrl.substring( idx + resourceFragment.length() ) );
        }

        return sb.toString();
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
