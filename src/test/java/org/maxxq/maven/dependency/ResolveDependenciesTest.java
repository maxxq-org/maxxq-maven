package org.maxxq.maven.dependency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Comparator;
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
        InputStream pomStream = getClass().getResourceAsStream( "/pom-dependency-with-exclusion.pom.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( pomStream );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );
        assertEquals( 1, dependencies.size() );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-settings, version=3.8.4]" ) );
    }

    @Test
    void resolveDependenciesWithExclusion2() {
        InputStream pomStream = getClass().getResourceAsStream( "/pom.with.exclusions.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( pomStream );

        List<GAV> result = dependencies.stream()
            .map( dependency -> GAV.fromDependency( dependency ) )
            .filter( dependency -> dependency.getGroupId().equals( "junit" ) )
            .collect( Collectors.toList() );
        assertEquals( 0, result.size(), "Did not expect junit as a dependency because it's excluded" );
    }

    @Test
    void resolveDependenciesWithManagedExclusion() {
        InputStream pomStream = getClass().getResourceAsStream( "/pom-dependency-with-managed-exclusion.pom.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( pomStream );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );
        assertEquals( 1, dependencies.size() );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-settings, version=3.8.4]" ) );
    }

    @Test
    void resolveDependenciesWithManagedDependency() {
        InputStream pomStream = getClass().getResourceAsStream( "/pom-override-dependency-with-management.pom.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( pomStream );

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
        InputStream pomStream = getClass().getResourceAsStream( "/maven-dependencies.pom.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( pomStream );

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
        InputStream pomStream = getClass().getResourceAsStream( "/maven-dependencies.pom.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( pomStream );

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
        InputStream pomStream = getClass().getResourceAsStream( "/maven-dependencies.pom.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( pomStream );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );
        System.out.println( result );
        assertEquals( 3, dependencies.size() );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-model, version=3.3.9]" ) );
        assertTrue( result.contains( "GAV [groupId=org.apache.maven, artifactId=maven-settings, version=3.8.4]" ) );
        assertTrue( result.contains( "GAV [groupId=org.junit.jupiter, artifactId=junit-jupiter-engine, version=5.4.0]" ) );
    }

    @Test
    void resolveDependenciesWithRanges() {
        InputStream pomStream = getClass().getResourceAsStream( "/maven-dependencies-range.pom.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( pomStream );

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
        InputStream pomStream = getClass().getResourceAsStream( "/maven-dependencies-double.pom.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( pomStream );

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
        InputStream pomStream = getClass().getResourceAsStream( "/commons-io-2.11.0.pom.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( pomStream );

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
        InputStream module2PomStream = getClass().getResourceAsStream( "/multimodule/module2.pom.xml" );
        InputStream parentPomStream = getClass().getResourceAsStream( "/multimodule/parent.pom.xml" );
        InputStream module1PomStream = getClass().getResourceAsStream( "/multimodule/module1.pom.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( module2PomStream, parentPomStream, module1PomStream );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );
        assertEquals( 26, result.size() );
        assertTrue( result.contains( "GAV [groupId=commons-io, artifactId=commons-io, version=2.11.0]" ) );
        System.out.println( "Add assertions for each library" );
    }

    @Test
    void resolveDependenciesMultiModule2() {
        InputStream pomStream = getClass().getResourceAsStream( "/multimodulefollowmodules/pom.xml" );
        InputStream module1PomStream = getClass().getResourceAsStream( "/multimodulefollowmodules/module1/pom.xml" );
        InputStream module2PomStream = getClass().getResourceAsStream( "/multimodulefollowmodules/module2/pom.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( pomStream, module1PomStream, module2PomStream );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );
        assertEquals( 26, result.size() );
        assertTrue( result.contains( "GAV [groupId=commons-io, artifactId=commons-io, version=2.11.0]" ) );
        System.out.println( "Add assertions for each library" );
    }

    @Test
    void resolveDependenciesMultiModuleFollowModules() {
        resolveDependencies.setPomStreamProvider( new ClasspathPomStreamProvider() );
        InputStream pomStream = getClass().getResourceAsStream( "/multimodulefollowmodules/pom.xml" );

        List<GAV> gavs = resolveDependencies.storeMultiModule( pomStream, "/multimodulefollowmodules/" );
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
        GAV gav = new GAV( "groupid", "artifactid", "notexisting" );

        Set<Dependency> result = resolveDependencies.getDependencies( gav );

        assertEquals( 0, result.size() );
    }

    @Test
    void getDependenciesForOkHttpClientWithGavFromMavenCentral() {
        GAV gav = new GAV( "com.squareup.okhttp3", "okhttp", "4.9.3" );

        List<String> result = resolveDependencies.getDependencies( gav )
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
        GAV gav = new GAV( "org.springframework.ws", "spring-ws-core", "4.0.11" );

        Set<String> scopesForJettyDependencies = resolveDependencies.getDependencies( gav )
            .stream()
            .filter( dependency -> dependency.getGroupId().contains( "jetty" ) )
            .map( dependency -> dependency.getScope() )
            .collect( Collectors.toSet() );

        assertEquals( 1, scopesForJettyDependencies.size() );
        assertTrue( scopesForJettyDependencies.contains( "test" ) );
    }

    /**
     * provided, optional and dependency coming through profiles are currently excluded
     */
    @Test
    void validateScopesOnlyOptional() {
        resolveDependencies.setDependenyFilter(
            new DependencyFilter()
                .keepNothing()
                .setKeepOptional( true ));
        GAV gav = new GAV( "org.springframework.ws", "spring-ws-core", "4.0.11" );

        Set<Dependency> result = resolveDependencies.getDependencies( gav );

        StringBuilder resultString = new StringBuilder();
        result.stream()
            .sorted( Comparator.comparing( Dependency::getGroupId ).thenComparing( Dependency::getArtifactId ) )
            .forEach(
                dependency -> resultString.append(
                    String.format(
                        "%s:%s:%s:%s:%s%s\n",
                        dependency.getGroupId(),
                        dependency.getArtifactId(),
                        dependency.getType(),
                        dependency.getVersion(),
                        dependency.getScope(),
                        dependency.isOptional() ? " (optional)" : "" ) ) );

        System.out.println( resultString );

        assertEquals( 38, result.size() );
        assertTrue( resultString.toString().contains( "ch.qos.logback:logback-classic:jar:1.2.12:compile (optional)" ) );
        assertTrue( resultString.toString().contains( "com.google.code.findbugs:jsr305:jar:3.0.2:compile (optional)" ) );
        assertTrue( resultString.toString().contains( "commons-httpclient:commons-httpclient:jar:3.1:compile (optional)" ) );
    }

    @Test
    void validateScopesWithouthOptionalAndTest() {
        resolveDependencies.setDependenyFilter(
            new DependencyFilter()
                .setKeepOptional( false )
                .setKeepTest( false )
                .setKeepTestRoot( false ) );
        GAV gav = new GAV( "org.springframework.ws", "spring-ws-core", "4.0.11" );

        Set<Dependency> result = resolveDependencies.getDependencies( gav );

        StringBuilder resultString = new StringBuilder();
        result.stream()
            .sorted( Comparator.comparing( Dependency::getGroupId ).thenComparing( Dependency::getArtifactId ) )
            .forEach(
                dependency -> resultString.append(
                    String.format(
                        "%s:%s:%s:%s:%s%s\n",
                        dependency.getGroupId(),
                        dependency.getArtifactId(),
                        dependency.getType(),
                        dependency.getVersion(),
                        dependency.getScope(),
                        dependency.isOptional() ? " (optional)" : "" ) ) );

        System.out.println( resultString );

        assertEquals( 22, result.size() );

    }

    @Test
    void getDependenciesForJongo() {
        InputStream pomStream = getClass().getResourceAsStream( "/jongo-1.3.0.pom" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( pomStream );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).sorted().collect( Collectors.toList() );
        assertEquals( 31, result.size() );
    }

    @Test
    void getDependenciesForActiveMQWithGavFromMavenCentral() {
        InputStream pomStream = getClass().getResourceAsStream( "/pom-with-old-property-style.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( pomStream );

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
        InputStream parentPomStream = getClass().getResourceAsStream( "/multimoduleinconsistent/parent.pom.xml" );
        InputStream module2PomStream = getClass().getResourceAsStream( "/multimoduleinconsistent/module2.pom.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( parentPomStream, module2PomStream );

        assertTrue( dependencies.size() > 0 );
    }

    @Test
    void resolveDependenciesForMultipleBoms() {
        InputStream pomStream = getClass().getResourceAsStream( "/multipleboms/multiple-bom.pom" );

        GAV parent = resolveDependencies.store( pomStream );
        Set<Dependency> dependencies = resolveDependencies.getDependencies( parent );

        List<String> result = dependencies.stream().map( dependency -> GAV.fromDependency( dependency ).toString() ).collect( Collectors.toList() );
        assertTrue( dependencies.size() > 0 );
        assertTrue( result.contains( "GAV [groupId=org.apache.logging.log4j, artifactId=log4j-core, version=2.16.0]" ) );
        assertFalse( result.contains( "GAV [groupId=org.apache.logging.log4j, artifactId=log4j-core, version=2.12.1]" ) );
    }

    @Test
    void resolvedDependenciesForPomWithInvalidParent() {
        InputStream pomStream = getClass().getResourceAsStream( "/pom-with-invalid-parent-and-properties.xml" );

        Set<Dependency> dependencies = resolveDependencies.getDependencies( pomStream );

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
