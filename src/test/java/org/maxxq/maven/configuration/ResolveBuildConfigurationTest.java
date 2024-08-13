package org.maxxq.maven.configuration;

import java.nio.file.Paths;
import java.util.Optional;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.maxxq.maven.dependency.ModelIO;
import org.maxxq.maven.repository.FileCachingRepository;
import org.maxxq.maven.repository.InMemoryCachingRepository;
import org.maxxq.maven.repository.LocalInMemoryRepository;
import org.maxxq.maven.repository.RemoteRepository;
import org.maxxq.maven.repository.VirtualRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResolveBuildConfigurationTest {
    private ResolveBuildConfiguration resolveConfiguration;

    @BeforeEach
    void setUp() {
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
    void resolveConfigurationEmptyPom() {
        Model result = resolveConfiguration.resolveBuildConfiguration( getClass().getResourceAsStream( "/empty.pom.xml" ) );

        assertNotNull( result );
        new ModelIO().writeModelToStream( result, System.out );
    }

    @Test
    void resolveConfigurationNoParentAndProperty() {
        Model result = resolveConfiguration.resolveBuildConfiguration( getClass().getResourceAsStream( "/pom.with.build.and.property.xml" ) );

        assertNotNull( result );
        new ModelIO().writeModelToStream( result, System.out );
        assertEquals( "3.8.1", result.getBuild().getPluginsAsMap().get( "org.apache.maven.plugins:maven-compiler-plugin" ).getVersion() );
    }

    @Test
    void resolveConfigurationNoParentAndPropertyInConfiguration() {
        Model result = resolveConfiguration.resolveBuildConfiguration( getClass().getResourceAsStream( "/pom.with.build.and.property.in.configuration.xml" ) );

        assertNotNull( result );
        new ModelIO().writeModelToStream( result, System.out );
        assertTrue(
            result.getBuild().getPluginsAsMap().get( "org.apache.maven.plugins:maven-compiler-plugin" ).getConfiguration().toString().contains( "<source>17</source>" ) );
        assertTrue(
            result.getBuild().getPluginsAsMap().get( "org.apache.maven.plugins:maven-compiler-plugin" ).getConfiguration().toString().contains( "<target>17</target>" ) );
    }

    @Test
    void resolveConfigurationWithParent() {
        resolveConfiguration.store( getClass().getResourceAsStream( "/build.parent.pom.xml" ) );

        Model result = resolveConfiguration.resolveBuildConfiguration( getClass().getResourceAsStream( "/build.only.pom.xml" ) );

        assertNotNull( result );
        new ModelIO().writeModelToStream( result, System.out );
        assertEquals( "3.11.0", result.getBuild().getPluginsAsMap().get( "org.apache.maven.plugins:maven-compiler-plugin" ).getVersion() );
    }

    @Test
    void resolveConfigurationWithEmptyParent() {
        resolveConfiguration.store( getClass().getResourceAsStream( "/empty.pom.xml" ) );

        Model result = resolveConfiguration.resolveBuildConfiguration( getClass().getResourceAsStream( "/pom.empty.parent.xml" ) );

        assertNotNull( result );
        new ModelIO().writeModelToStream( result, System.out );
        assertEquals( "3.11.0", result.getBuild().getPluginManagement().getPluginsAsMap().get( "org.apache.maven.plugins:maven-compiler-plugin" ).getVersion() );
    }

    @Test
    void resolveConfigurationEmptyPomWithEmptyParent() {
        resolveConfiguration.store( getClass().getResourceAsStream( "/emptypomwithemptyparent/parent.pom.no.build.xml" ) );

        Model result = resolveConfiguration.resolveBuildConfiguration( getClass().getResourceAsStream( "/emptypomwithemptyparent/pom.with.parent.no.build.xml" ) );

        assertNotNull( result );
    }

    @Test
    void resolveConfigurationEmptyPomWithNonEmptyBuildManagementInParent() {
        resolveConfiguration.store( getClass().getResourceAsStream( "/emptypomwithnonemptypluginmanagementinparent/parent.pom.with.build.xml" ) );

        Model result = resolveConfiguration
            .resolveBuildConfiguration( getClass().getResourceAsStream( "/emptypomwithnonemptypluginmanagementinparent/pom.with.parent.no.build.xml" ) );

        assertNotNull( result );
    }

    @Test
    void resolveConfigurationEmptyPomWithNonEmptyParent() {
        resolveConfiguration.store( getClass().getResourceAsStream( "/emptypomwithnonemptyparent/parent.pom.with.build.xml" ) );

        Model result = resolveConfiguration.resolveBuildConfiguration( getClass().getResourceAsStream( "/emptypomwithnonemptyparent/pom.with.parent.no.build.xml" ) );

        assertNotNull( result );
    }

    @Test
    void resolveConfigurationMultiModule() {
        resolveConfiguration.store( getClass().getResourceAsStream( "/multimodule/parent.pom.xml" ) );

        Model result = resolveConfiguration.resolveBuildConfiguration( getClass().getResourceAsStream( "/multimodule/module1.pom.xml" ) );

        assertNotNull( result );
        Optional<Plugin> compilerPlugin = result.getBuild().getPlugins().stream().filter( plugin -> plugin.getArtifactId().equals( "maven-compiler-plugin" ) ).findFirst();
        assertTrue( compilerPlugin.isPresent() );
        assertEquals( "3.10.1", compilerPlugin.get().getVersion() );
        assertNotNull( compilerPlugin.get().getConfiguration() );
        Xpp3Dom config = (Xpp3Dom) compilerPlugin.get().getConfiguration();
        assertEquals( "17", config.getChild( "source" ).getValue() );
        assertEquals( "17", config.getChild( "target" ).getValue() );
        assertEquals( "true", config.getChild( "debug" ).getValue() );
    }

    @Test
    void resolveConfigurationSpringBoot() {
        Model result = resolveConfiguration.resolveBuildConfiguration( getClass().getResourceAsStream( "/spring.boot.parent.pom.xml" ) );

        assertNotNull( result );
        assertEquals( "1.8", result.getProperties().get( "maven.compiler.source" ) );
    }
}
