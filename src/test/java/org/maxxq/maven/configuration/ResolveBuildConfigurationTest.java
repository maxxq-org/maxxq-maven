package org.maxxq.maven.configuration;

import java.nio.file.Paths;
import java.util.Optional;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxxq.maven.repository.FileCachingRepository;
import org.maxxq.maven.repository.InMemoryCachingRepository;
import org.maxxq.maven.repository.LocalInMemoryRepository;
import org.maxxq.maven.repository.RemoteRepository;
import org.maxxq.maven.repository.VirtualRepository;

public class ResolveBuildConfigurationTest {
    private ResolveBuildConfiguration resolveConfiguration;

    @Before
    public void setUp() {
        resolveConfiguration = new ResolveBuildConfiguration(
            new VirtualRepository()
                .addRepository( new LocalInMemoryRepository() )
                .addRepository(
                    new InMemoryCachingRepository(
                        new FileCachingRepository(
                            Paths.get( System.getProperty( "java.io.tmpdir" ), "pomcache" ),
                            new RemoteRepository() ) ) ) );
    }

    @Test
    public void resolveConfigurationMultiModule() {
        resolveConfiguration.store( getClass().getResourceAsStream( "/multimodule/parent.pom.xml" ) );

        Model result = resolveConfiguration.resolveBuildConfiguration( getClass().getResourceAsStream( "/multimodule/module1.pom.xml" ) );

        Assert.assertNotNull( result );
        Optional<Plugin> compilerPlugin = result.getBuild().getPlugins().stream().filter( plugin -> plugin.getArtifactId().equals( "maven-compiler-plugin" ) ).findFirst();
        Assert.assertTrue( compilerPlugin.isPresent() );
        Assert.assertEquals( "3.10.1", compilerPlugin.get().getVersion() );
        Assert.assertNotNull( compilerPlugin.get().getConfiguration() );
        Xpp3Dom config = (Xpp3Dom) compilerPlugin.get().getConfiguration();
        Assert.assertEquals( "17", config.getChild( "source" ).getValue() );
        Assert.assertEquals( "17", config.getChild( "target" ).getValue() );
        Assert.assertEquals( "true", config.getChild( "debug" ).getValue() );
    }
}
