package org.maxxq.maven.dependency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.model.Dependency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.maxxq.maven.repository.InMemoryCachingRepository;
import org.maxxq.maven.repository.LocalFileRepository;
import org.maxxq.maven.repository.LocalInMemoryRepository;
import org.maxxq.maven.repository.VirtualRepository;

class ResolveDependenciesTest {
    private ResolveDependencies resolveDependencies;

    @BeforeEach
    void setUp() {
        resolveDependencies = new ResolveDependencies(
            new VirtualRepository()
                .addRepository( new LocalInMemoryRepository() )
                .addRepository(
                    new InMemoryCachingRepository(
                        new LocalFileRepository( Paths.get( "src/test/resources/pomcache" ) ) ) ) );
        // new FileCachingRepository(
        // Paths.get( "src/test/resources/pomcache" ),
        // new RemoteRepository() ) ) ) );
    }

    @Test
    void resolveDependenciesWithExclusion() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies( getClass().getResourceAsStream( "/pom-dependency-with-exclusion.pom.xml" ) );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );

        assertEquals( 1, dependencies.size() );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-settings, version=3.8.4]" ) );
    }

    @Test
    void resolveDependenciesWithExclusion2() {
        Set<Dependency> dependencies = resolveDependencies
            .getDependencies( getClass().getResourceAsStream( "/pom.with.exclusions.xml" ) );

        List<GAV> result = dependencies.stream()
            .map( dependency -> GAV.fromDependency( dependency ) )
            .filter( dependency -> dependency.getGroupId().equals( "junit" ) )
            .collect( Collectors.toList() );

        assertEquals( 0, result.size(), "Did not expect junit as a dependency because it's excluded" );
    }

    @Test
    void resolveDependenciesWithManagedExclusion() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies( getClass().getResourceAsStream( "/pom-dependency-with-managed-exclusion.pom.xml" ) );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );

        assertEquals( 1, dependencies.size() );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-settings, version=3.8.4]" ) );
    }

    @Test
    void resolveDependenciesWithManagedDependency() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies( getClass().getResourceAsStream( "/pom-override-dependency-with-management.pom.xml" ) );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );

        assertEquals( 10, dependencies.size() );
        assertTrue( result.contains( "GAV [groupId=org.apiguardian, artifactId=apiguardian-api, version=1.0.0]" ) );
    }

    @Test
    void resolveDependenciesWithManagedNonTransitiveDependency() {
        GAV project1Gav = resolveDependencies.store( getClass().getResourceAsStream( "/dependencymanagementisnottransitive/project1_uses_project2.pom.xml" ) );
        resolveDependencies.store( getClass().getResourceAsStream( "/dependencymanagementisnottransitive/project2.pom.xml" ) );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( project1Gav );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );
        result.stream().forEach( resultstring -> System.out.println( resultstring ) );
        assertEquals( 6, dependencies.size() );
        assertTrue( result.contains( "GAV [groupId=chabernac, artifactId=project2, version=0.0.1-SNAPSHOT]" ) );
        assertTrue( result.contains( "GAV [groupId=com.squareup.okhttp3, artifactId=okhttp, version=4.9.3]" ) );
        assertTrue( result.contains( "GAV [groupId=com.squareup.okio, artifactId=okio, version=2.8.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.jetbrains.kotlin, artifactId=kotlin-stdlib, version=1.4.10]" ) );
        assertTrue( result.contains( "GAV [groupId=org.jetbrains.kotlin, artifactId=kotlin-stdlib-common, version=1.4.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.jetbrains, artifactId=annotations, version=13.0]" ) );
    }

    @Test
    void resolveDependencies() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies( getClass().getResourceAsStream( "/maven-dependencies.pom.xml" ) );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );

        assertEquals( 10, dependencies.size() );
        assertTrue( result.contains( "GAV [groupId=org.apiguardian, artifactId=apiguardian-api, version=1.0.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.commons, artifactId=commons-lang3, version=3.4]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.jupiter, artifactId=junit-jupiter-api, version=5.4.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.jupiter, artifactId=junit-jupiter-engine, version=5.4.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.platform, artifactId=junit-platform-commons, version=1.4.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.platform, artifactId=junit-platform-engine, version=1.4.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-model, version=3.3.9]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-settings, version=3.8.4]" ) );
        assertTrue( result.contains( "GAV [groupId=org.opentest4j, artifactId=opentest4j, version=1.1.1]" ) );
        assertTrue( result.contains( "GAV [groupId=org.codehaus.plexus, artifactId=plexus-utils, version=3.0.22]" ) );
    }

    @Test
    void resolveDependenciesCustomFilterForScopeCompile() {
        resolveDependencies.setDependenyFilter( ( dependency, depth ) -> dependency.getScope().equals( "compile" ) );
        Set<Dependency> dependencies = resolveDependencies.getDependencies( getClass().getResourceAsStream( "/maven-dependencies.pom.xml" ) );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );

        System.out.println( result );
        assertEquals( 4, dependencies.size() );
        assertTrue( result.contains( "GAV [groupId=org.apache.commons, artifactId=commons-lang3, version=3.4]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-model, version=3.3.9]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-settings, version=3.8.4]" ) );
        assertTrue( result.contains( "GAV [groupId=org.codehaus.plexus, artifactId=plexus-utils, version=3.0.22]" ) );
    }

    @Test
    void resolveDependenciesCustomFilterForDepth() {
        resolveDependencies.setDependenyFilter( ( dependency, depth ) -> depth == 0 );
        Set<Dependency> dependencies = resolveDependencies.getDependencies( getClass().getResourceAsStream( "/maven-dependencies.pom.xml" ) );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );

        System.out.println( result );
        assertEquals( 3, dependencies.size() );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-model, version=3.3.9]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-settings, version=3.8.4]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.jupiter, artifactId=junit-jupiter-engine, version=5.4.0]" ) );
    }

    @Test
    void resolveDependenciesWithRanges() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies( getClass().getResourceAsStream( "/maven-dependencies-range.pom.xml" ) );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );

        result.stream().forEach( resultstring -> System.out.println( resultstring ) );
        assertEquals( 9, dependencies.size() );
        assertTrue( result.contains( "GAV [groupId=org.apiguardian, artifactId=apiguardian-api, version=1.0.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.jupiter, artifactId=junit-jupiter-api, version=5.4.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.jupiter, artifactId=junit-jupiter-engine, version=5.4.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.platform, artifactId=junit-platform-commons, version=1.4.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.platform, artifactId=junit-platform-engine, version=1.4.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-model, version=3.8.5]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-settings, version=3.8.4]" ) );
        assertTrue( result.contains( "GAV [groupId=org.opentest4j, artifactId=opentest4j, version=1.1.1]" ) );
        assertTrue( result.contains( "GAV [groupId=org.codehaus.plexus, artifactId=plexus-utils, version=3.3.0]" ) );
    }

    @Test
    void resolveDependenciesFlawedWithDouble() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies( getClass().getResourceAsStream( "/maven-dependencies-double.pom.xml" ) );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );
        result.stream().forEach( resultstring -> System.out.println( "strange: " + resultstring ) );
        assertEquals( 10, dependencies.size() );
        assertTrue( result.contains( "GAV [groupId=org.apiguardian, artifactId=apiguardian-api, version=1.0.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.commons, artifactId=commons-lang3, version=3.4]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.jupiter, artifactId=junit-jupiter-api, version=5.4.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.jupiter, artifactId=junit-jupiter-engine, version=5.4.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.platform, artifactId=junit-platform-commons, version=1.4.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.platform, artifactId=junit-platform-engine, version=1.4.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-model, version=3.3.9]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-settings, version=3.8.4]" ) );
        assertTrue( result.contains( "GAV [groupId=org.opentest4j, artifactId=opentest4j, version=1.1.1]" ) );
        assertTrue( result.contains( "GAV [groupId=org.codehaus.plexus, artifactId=plexus-utils, version=3.0.22]" ) );
    }

    @Test
    void resolveDependenciesWithImport() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies( getClass().getResourceAsStream( "/commons-io-2.11.0.pom.xml" ) );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );

        result.stream().forEach( resultstring -> System.out.println( resultstring ) );

        assertEquals( 28, dependencies.size() );
        assertTrue( result.contains( "GAV [groupId=org.apiguardian, artifactId=apiguardian-api, version=1.1.0]" ) );
        assertTrue( result.contains( "GAV [groupId=net.bytebuddy, artifactId=byte-buddy, version=1.11.3]" ) );
        assertTrue( result.contains( "GAV [groupId=net.bytebuddy, artifactId=byte-buddy-agent, version=1.11.3]" ) );
        assertTrue( result.contains( "GAV [groupId=org.checkerframework, artifactId=checker-compat-qual, version=2.5.5]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.commons, artifactId=commons-lang3, version=3.12.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.commons, artifactId=commons-math3, version=3.2]" ) );
        assertTrue( result.contains( "GAV [groupId=com.google.errorprone, artifactId=error_prone_annotations, version=2.3.4]" ) );
        assertTrue( result.contains( "GAV [groupId=com.google.guava, artifactId=failureaccess, version=1.0.1]" ) );
        assertTrue( result.contains( "GAV [groupId=com.google.guava, artifactId=guava, version=30.1-android]" ) );
        assertTrue( result.contains( "GAV [groupId=com.google.j2objc, artifactId=j2objc-annotations, version=1.3]" ) );
        assertTrue( result.contains( "GAV [groupId=com.google.jimfs, artifactId=jimfs, version=1.2]" ) );
        assertTrue( result.contains( "GAV [groupId=org.openjdk.jmh, artifactId=jmh-core, version=1.32]" ) );
        assertTrue( result.contains( "GAV [groupId=org.openjdk.jmh, artifactId=jmh-generator-annprocess, version=1.32]" ) );
        assertTrue( result.contains( "GAV [groupId=net.sf.jopt-simple, artifactId=jopt-simple, version=4.6]" ) );
        assertTrue( result.contains( "GAV [groupId=com.google.code.findbugs, artifactId=jsr305, version=3.0.2]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.jupiter, artifactId=junit-jupiter, version=5.7.2]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.jupiter, artifactId=junit-jupiter-api, version=5.7.2]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.jupiter, artifactId=junit-jupiter-engine, version=5.7.2]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.jupiter, artifactId=junit-jupiter-params, version=5.7.2]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit-pioneer, artifactId=junit-pioneer, version=1.4.2]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.platform, artifactId=junit-platform-commons, version=1.7.2]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.platform, artifactId=junit-platform-engine, version=1.7.2]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.platform, artifactId=junit-platform-launcher, version=1.7.2]" ) );
        assertTrue( result.contains( "GAV [groupId=com.google.guava, artifactId=listenablefuture, version=9999.0-empty-to-avoid-conflict-with-guava]" ) );
        assertTrue( result.contains( "GAV [groupId=org.mockito, artifactId=mockito-core, version=3.11.2]" ) );
        assertTrue( result.contains( "GAV [groupId=org.mockito, artifactId=mockito-inline, version=3.11.2]" ) );
        assertTrue( result.contains( "GAV [groupId=org.objenesis, artifactId=objenesis, version=3.2]" ) );
        assertTrue( result.contains( "GAV [groupId=org.opentest4j, artifactId=opentest4j, version=1.2.0]" ) );
    }

    @Test
    void resolveDependenciesMultiModule() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies(
            getClass().getResourceAsStream( "/multimodule/module2.pom.xml" ),
            getClass().getResourceAsStream( "/multimodule/parent.pom.xml" ),
            getClass().getResourceAsStream( "/multimodule/module1.pom.xml" ) );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );

        assertEquals( 26, result.size() );
        assertTrue( result.contains( "GAV [groupId=commons-io, artifactId=commons-io, version=2.11.0]" ) );
        System.out.println( "Add assertions for each library" );
    }

    @Test
    void resolveDependenciesMultiModule2() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies(
            getClass().getResourceAsStream( "/multimodulefollowmodules/pom.xml" ),
            getClass().getResourceAsStream( "/multimodulefollowmodules/module1/pom.xml" ),
            getClass().getResourceAsStream( "/multimodulefollowmodules/module2/pom.xml" ) );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );

        assertEquals( 26, result.size() );
        assertTrue( result.contains( "GAV [groupId=commons-io, artifactId=commons-io, version=2.11.0]" ) );
        System.out.println( "Add assertions for each library" );
    }

    @Test
    void resolveDependenciesMultiModuleFollowModules() {
        List<GAV> gavs = resolveDependencies
            .setPomStreamProvider( new ClasspathPomStreamProvider() )
            .storeMultiModule( getClass().getResourceAsStream( "/multimodulefollowmodules/pom.xml" ), "/multimodulefollowmodules/" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( gavs );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );
        result.stream().forEach( resultstring -> System.out.println( resultstring ) );
        assertEquals( 27, result.size() );
        assertTrue( result.contains( "GAV [groupId=commons-io, artifactId=commons-io, version=2.11.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.commons, artifactId=commons-lang3, version=3.12.0]" ) );
        System.out.println( "Add assertions for each library" );
    }

    @Test
    void resolveDependenciesMultiModuleStoreFirst() {
        resolveDependencies.store( getClass().getResourceAsStream( "/multimodule/parent.pom.xml" ) );
        GAV module1 = resolveDependencies.store( getClass().getResourceAsStream( "/multimodule/module1.pom.xml" ) );
        GAV module2 = resolveDependencies.store( getClass().getResourceAsStream( "/multimodule/module2.pom.xml" ) );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( module1, module2 );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );

        assertEquals( 26, result.size() );
    }

    @Test
    void getDependenciesForNonExistingGAV() {
        Set<Dependency> result = resolveDependencies.getDependencies( new GAV( "groupid", "artifactid", "notexisting" ) );

        assertEquals( 0, result.size() );
    }

    @Test
    void getDependenciesForOkHttpClientWithGavFromMavenCentral() {
        List<String> result = resolveDependencies.getDependencies( new GAV( "com.squareup.okhttp3", "okhttp", "4.9.3" ) )
            .stream()
            .map( dependency -> dependency.toString() )
            .collect( Collectors.toList() );

        assertEquals( 4, result.size() );
        assertTrue( result.contains( "Dependency {groupId=com.squareup.okio, artifactId=okio, version=2.8.0, type=jar}" ) );
        assertTrue( result.contains( "Dependency {groupId=org.jetbrains.kotlin, artifactId=kotlin-stdlib, version=1.4.10, type=jar}" ) );
        assertTrue( result.contains( "Dependency {groupId=org.jetbrains.kotlin, artifactId=kotlin-stdlib-common, version=1.4.0, type=jar}" ) );
        assertTrue( result.contains( "Dependency {groupId=org.jetbrains, artifactId=annotations, version=13.0, type=jar}" ) );
    }

    @Test
    void dependenciesThroughTestScopeShouldAlwaysHaveTestScope() {
        Set<String> scopesForJettyDependencies = resolveDependencies.getDependencies( new GAV( "org.springframework.ws", "spring-ws-core", "4.0.11" ) )
            .stream()
            .filter( dependency -> dependency.getGroupId().contains( "jetty" ) )
            .map( dependency -> dependency.getScope() )
            .collect( Collectors.toSet() );

        assertEquals( 1, scopesForJettyDependencies.size() );
        assertTrue( scopesForJettyDependencies.contains( "test" ) );
    }

    @Test
    void getDependenciesForJongo() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies( getClass().getResourceAsStream( "/jongo-1.3.0.pom" ) );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).sorted().collect( Collectors.toList() );

        assertEquals( 31, result.size() );
    }

    @Test
    void getDependenciesForActiveMQWithGavFromMavenCentral() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies( getClass().getResourceAsStream( "/pom-with-old-property-style.xml" ) );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).sorted().collect( Collectors.toList() );
        assertEquals( 12, result.size() );
        assertTrue( result.contains( "GAV [groupId=avalon-framework, artifactId=avalon-framework, version=4.1.3]" ) );
        assertTrue( result.contains( "GAV [groupId=backport-util-concurrent, artifactId=backport-util-concurrent, version=2.1]" ) );
        assertTrue( result.contains( "GAV [groupId=commons-logging, artifactId=commons-logging, version=1.1]" ) );
        assertTrue( result.contains( "GAV [groupId=commons-logging, artifactId=commons-logging-api, version=1.1]" ) );
        assertTrue( result.contains( "GAV [groupId=javax.servlet, artifactId=servlet-api, version=2.3]" ) );
        assertTrue( result.contains( "GAV [groupId=log4j, artifactId=log4j, version=1.2.12]" ) );
        assertTrue( result.contains( "GAV [groupId=logkit, artifactId=logkit, version=1.0.1]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.activemq, artifactId=activeio-core, version=3.1.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.activemq, artifactId=activemq-core, version=5.2.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.camel, artifactId=camel-core, version=1.5.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.geronimo.specs, artifactId=geronimo-j2ee-management_1.0_spec, version=1.0]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.geronimo.specs, artifactId=geronimo-jms_1.1_spec, version=1.1.1]" ) );
    }

    @Test
    void getDependenciesWithFaultyMultiModuleProjectIgnoreIConsistencies() {
        resolveDependencies.setIgnoreIconsistencies( true );
        Set<Dependency> dependencies = resolveDependencies.getDependencies(
            getClass().getResourceAsStream( "/multimoduleinconsistent/parent.pom.xml" ),
            getClass().getResourceAsStream( "/multimoduleinconsistent/module2.pom.xml" ) );

        assertTrue( dependencies.size() > 0 );
    }

    @Test
    void resolveDependenciesForMultipleBoms() {
        GAV parent = resolveDependencies.store( getClass().getResourceAsStream( "/multipleboms/multiple-bom.pom" ) );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( parent );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );
        assertTrue( dependencies.size() > 0 );
        assertTrue( result.contains( "GAV [groupId=org.apache.logging.log4j, artifactId=log4j-core, version=2.16.0]" ) );
        assertFalse( result.contains( "GAV [groupId=org.apache.logging.log4j, artifactId=log4j-core, version=2.12.1]" ) );
    }

    @Test
    void resolvedDependenciesForPomWithInvalidParent() {
        Set<Dependency> dependencies = resolveDependencies
            .getDependencies( getClass().getResourceAsStream( "/pom-with-invalid-parent-and-properties.xml" ) );

        List<String> result = dependencies.stream()
            .map( dependency -> GAV.fromDependency( dependency ).toString() )
            .collect( Collectors.toList() );

        assertEquals( 7, dependencies.size() );
        assertTrue( result.contains( "GAV [groupId=org.springframework, artifactId=spring-orm, version=3.2.13.RELEASE]" ) );
        assertTrue( result.contains( "GAV [groupId=org.springframework, artifactId=spring-test, version=3.2.13.RELEASE]" ) );
        assertTrue( result.contains( "GAV [groupId=org.springframework, artifactId=spring-beans, version=3.2.13.RELEASE]" ) );
        assertTrue( result.contains( "GAV [groupId=org.springframework, artifactId=spring-tx, version=3.2.13.RELEASE]" ) );
        assertTrue( result.contains( "GAV [groupId=commons-logging, artifactId=commons-logging, version=1.1.3]" ) );
        assertTrue( result.contains( "GAV [groupId=org.springframework, artifactId=spring-core, version=3.2.13.RELEASE]" ) );
        assertTrue( result.contains( "GAV [groupId=org.springframework, artifactId=spring-jdbc, version=3.2.13.RELEASE]" ) );
    }
}
