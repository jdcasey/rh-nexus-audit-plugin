package org.sonatype.plugins.it;

import static org.sonatype.plugins.it.util.Bootstrap.bootstrap;
import static org.sonatype.plugins.it.util.TestUtils.getTestDir;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.jdom.JDOMException;
import org.junit.Test;
import org.sonatype.plugins.it.util.ContentAssertions;
import org.sonatype.plugins.it.util.TestUtils;
import org.sonatype.plugins.it.util.XPathContentAssertions;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;

public class IT003_NonPluginComponentDeps
{

    @Test
    public void run()
        throws IOException, URISyntaxException, VerificationException, JDOMException
    {
        bootstrap();

        File dir = getTestDir( "003-nonPluginComponentDeps" );

        String version = TestUtils.getPomVersion( new File( dir, "pom.xml" ) );

        Verifier verifier = new Verifier( dir.getAbsolutePath() );

        verifier.executeGoal( "package" );

        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        Set<String> xpaths =
            Collections.singleton( "//classpathDependency[artifactId/text()=\"it-component-dependency\" "
                + "and hasComponents/text()=\"true\"]" );

        ContentAssertions assertions = new XPathContentAssertions( "META-INF/it/plugin.xml", xpaths );

        Set<String> banned = Collections.emptySet();

        File archive = new File( verifier.getBasedir(), "target/003-nonPluginComponentDeps-" + version + ".jar" );

        TestUtils.assertZipContents( Collections.singletonList( assertions ), banned, archive );
    }

}
