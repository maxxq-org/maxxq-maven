package org.maxxq.maven.dependency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    /**
     * provided, optional and dependency coming through profiles are currently excluded
     */
    @Test
    void validateScopes() {
        Set<Dependency> result = resolveDependencies.getDependencies( new GAV( "org.springframework.ws", "spring-ws-core", "4.0.11" ) );

        StringBuilder resultString = new StringBuilder();
        result.stream()
            .sorted( Comparator.comparing( Dependency::getGroupId ).thenComparing( Dependency::getArtifactId ) )
            .forEach(
                dependency -> resultString.append(
                    String.format(
                        "%s:%s:%s:%s:%s\n",
                        dependency.getGroupId(),
                        dependency.getArtifactId(),
                        dependency.getType(),
                        dependency.getVersion(),
                        dependency.getScope(),
                        dependency.isOptional() ? " (optional)" : "" ) ) );

        System.out.println( resultString );

        // result.stream()
        // .sorted( Comparator.comparing( Dependency::getGroupId ).thenComparing( Dependency::getArtifactId ) )
        // .forEach(
        // dependency -> System.out.printf(
        // "\t%s:%s:%s:%s%n",
        // dependency.getGroupId(),
        // dependency.getArtifactId(),
        // dependency.getVersion(),
        // dependency.getScope(),
        // dependency.isOptional() ? " (optional)" : "" ) );

        // String expected_result = """
        // com.fasterxml.woodstox:woodstox-core:jar:6.5.1:test
        // com.jayway.jsonpath:json-path:jar:2.7.0:test
        // com.sun.istack:istack-commons-runtime:jar:4.1.1:runtime
        // com.sun.xml.messaging.saaj:saaj-impl:jar:2.0.1:compile
        // commons-codec:commons-codec:jar:1.9:compile (optional)
        // commons-httpclient:commons-httpclient:jar:3.1:compile (optional)
        // commons-io:commons-io:jar:2.11.0:test
        // dom4j:dom4j:jar:1.6.1:compile (optional)
        // io.micrometer:micrometer-commons:jar:1.10.13:compile
        // io.micrometer:micrometer-observation:jar:1.10.13:compile
        // io.projectreactor:reactor-core:jar:3.5.14:test
        // jakarta.activation:jakarta.activation-api:jar:2.1.0:compile
        // jakarta.mail:jakarta.mail-api:jar:2.1.0:test
        // jakarta.servlet:jakarta.servlet-api:jar:6.0.0:provided
        // jakarta.xml.bind:jakarta.xml.bind-api:jar:4.0.0:compile
        // jakarta.xml.soap:jakarta.xml.soap-api:jar:3.0.0:compile
        // net.bytebuddy:byte-buddy-agent:jar:1.11.19:test
        // net.bytebuddy:byte-buddy:jar:1.11.19:test
        // net.minidev:accessors-smart:jar:2.4.7:test
        // net.minidev:json-smart:jar:2.4.7:test
        // org.apache.httpcomponents.client5:httpclient5:jar:5.2.1:compile (optional)
        // org.apache.httpcomponents.core5:httpcore5-h2:jar:5.2:compile (optional)
        // org.apache.httpcomponents.core5:httpcore5:jar:5.2:compile (optional)
        // org.apache.httpcomponents:httpclient:jar:4.5.3:compile (optional)
        // org.apache.httpcomponents:httpcore:jar:4.4.6:compile (optional)
        // org.apache.logging.log4j:log4j-api:jar:2.19.0:test
        // org.apache.logging.log4j:log4j-core:jar:2.19.0:test
        // org.apache.ws.xmlschema:xmlschema-core:jar:2.2.2:compile (optional)
        // org.apiguardian:apiguardian-api:jar:1.1.2:test
        // org.aspectj:aspectjrt:jar:1.9.6:test
        // org.aspectj:aspectjweaver:jar:1.9.6:test
        // org.assertj:assertj-core:jar:3.9.0:test
        // org.codehaus.woodstox:stax2-api:jar:4.2.1:test
        // org.easymock:easymock:jar:4.3:test
        // org.eclipse.angus:angus-activation:jar:1.0.0:runtime
        // org.eclipse.angus:angus-mail:jar:1.0.0:test
        // org.eclipse.jetty.toolchain:jetty-jakarta-servlet-api:jar:5.0.2:test
        // org.eclipse.jetty:jetty-http:jar:11.0.12:test
        // org.eclipse.jetty:jetty-io:jar:11.0.12:test
        // org.eclipse.jetty:jetty-security:jar:11.0.12:test
        // org.eclipse.jetty:jetty-server:jar:11.0.12:test
        // org.eclipse.jetty:jetty-servlet:jar:11.0.12:test
        // org.eclipse.jetty:jetty-util:jar:11.0.12:test
        // org.glassfish.jaxb:jaxb-core:jar:4.0.1:runtime
        // org.glassfish.jaxb:jaxb-runtime:jar:4.0.1:runtime
        // org.glassfish.jaxb:txw2:jar:4.0.1:runtime
        // org.jdom:jdom2:jar:2.0.6.1:compile (optional)
        // org.junit.jupiter:junit-jupiter-api:jar:5.9.1:test
        // org.junit.jupiter:junit-jupiter-engine:jar:5.9.1:test
        // org.junit.platform:junit-platform-commons:jar:1.9.1:test
        // org.junit.platform:junit-platform-engine:jar:1.9.1:test
        // org.jvnet.staxex:stax-ex:jar:2.0.1:compile
        // org.mockito:mockito-core:jar:4.0.0:test
        // org.objenesis:objenesis:jar:3.2:test
        // org.opentest4j:opentest4j:jar:1.2.0:test
        // org.ow2.asm:asm:jar:9.1:test
        // org.reactivestreams:reactive-streams:jar:1.0.4:test
        // org.slf4j:slf4j-api:jar:2.0.6:test
        // org.springframework.hateoas:spring-hateoas:jar:2.0.7:test
        // org.springframework.plugin:spring-plugin-core:jar:3.0.0:test
        // org.springframework.ws:spring-xml:jar:4.0.11:compile
        // org.springframework:spring-aop:jar:6.0.16:compile
        // org.springframework:spring-beans:jar:6.0.16:compile
        // org.springframework:spring-context:jar:6.0.16:compile
        // org.springframework:spring-core:jar:6.0.16:compile
        // org.springframework:spring-expression:jar:6.0.16:compile
        // org.springframework:spring-jcl:jar:6.0.16:compile
        // org.springframework:spring-oxm:jar:6.0.16:compile
        // org.springframework:spring-test:jar:6.0.16:test
        // org.springframework:spring-web:jar:6.0.16:compile
        // org.springframework:spring-webflux:jar:6.0.16:test
        // org.springframework:spring-webmvc:jar:6.0.16:compile
        // org.xmlunit:xmlunit-assertj:jar:2.9.0:test
        // org.xmlunit:xmlunit-core:jar:2.9.0:test
        // wsdl4j:wsdl4j:jar:1.6.3:compile (optional)
        // xml-apis:xml-apis:jar:1.0.b2:compile (optional)
        // xom:xom:jar:1.3.7:compile (optional)
        // """;

        String expected_result = """
            com.fasterxml.woodstox:woodstox-core:jar:6.5.1:test
            com.jayway.jsonpath:json-path:jar:2.7.0:test
            com.sun.istack:istack-commons-runtime:jar:4.1.1:runtime
            com.sun.xml.messaging.saaj:saaj-impl:jar:2.0.1:compile
            commons-io:commons-io:jar:2.11.0:test
            io.micrometer:micrometer-commons:jar:1.10.13:compile
            io.micrometer:micrometer-observation:jar:1.10.13:compile
            io.projectreactor:reactor-core:jar:3.5.14:test
            jakarta.activation:jakarta.activation-api:jar:2.1.0:compile
            jakarta.xml.bind:jakarta.xml.bind-api:jar:4.0.0:compile
            jakarta.xml.soap:jakarta.xml.soap-api:jar:3.0.0:compile
            net.bytebuddy:byte-buddy:jar:1.11.19:test
            net.bytebuddy:byte-buddy-agent:jar:1.11.19:test
            net.minidev:accessors-smart:jar:2.4.7:test
            net.minidev:json-smart:jar:2.4.7:test
            org.apache.logging.log4j:log4j-api:jar:2.19.0:test
            org.apache.logging.log4j:log4j-core:jar:2.19.0:test
            org.apiguardian:apiguardian-api:jar:1.1.2:test
            org.aspectj:aspectjrt:jar:1.9.6:test
            org.aspectj:aspectjweaver:jar:1.9.6:test
            org.assertj:assertj-core:jar:3.9.0:test
            org.codehaus.woodstox:stax2-api:jar:4.2.1:test
            org.easymock:easymock:jar:4.3:test
            org.eclipse.angus:angus-activation:jar:1.0.0:runtime
            org.eclipse.jetty:jetty-http:jar:11.0.12:test
            org.eclipse.jetty:jetty-io:jar:11.0.12:test
            org.eclipse.jetty:jetty-security:jar:11.0.12:test
            org.eclipse.jetty:jetty-server:jar:11.0.12:test
            org.eclipse.jetty:jetty-servlet:jar:11.0.12:test
            org.eclipse.jetty:jetty-util:jar:11.0.12:test
            org.eclipse.jetty.toolchain:jetty-jakarta-servlet-api:jar:5.0.2:test
            org.glassfish.jaxb:jaxb-core:jar:4.0.1:runtime
            org.glassfish.jaxb:jaxb-runtime:jar:4.0.1:runtime
            org.glassfish.jaxb:txw2:jar:4.0.1:runtime
            org.junit.jupiter:junit-jupiter-api:jar:5.9.1:test
            org.junit.jupiter:junit-jupiter-engine:jar:5.9.1:test
            org.junit.platform:junit-platform-commons:jar:1.9.1:test
            org.junit.platform:junit-platform-engine:jar:1.9.1:test
            org.jvnet.staxex:stax-ex:jar:2.0.1:compile
            org.mockito:mockito-core:jar:4.0.0:test
            org.objenesis:objenesis:jar:3.2:test
            org.opentest4j:opentest4j:jar:1.2.0:test
            org.ow2.asm:asm:jar:9.1:test
            org.reactivestreams:reactive-streams:jar:1.0.4:test
            org.slf4j:slf4j-api:jar:2.0.6:test
            org.springframework:spring-aop:jar:6.0.16:compile
            org.springframework:spring-beans:jar:6.0.16:compile
            org.springframework:spring-context:jar:6.0.16:compile
            org.springframework:spring-core:jar:6.0.16:compile
            org.springframework:spring-expression:jar:6.0.16:compile
            org.springframework:spring-jcl:jar:6.0.16:compile
            org.springframework:spring-oxm:jar:6.0.16:compile
            org.springframework:spring-test:jar:6.0.16:test
            org.springframework:spring-web:jar:6.0.16:compile
            org.springframework:spring-webflux:jar:6.0.16:test
            org.springframework:spring-webmvc:jar:6.0.16:compile
            org.springframework.hateoas:spring-hateoas:jar:2.0.7:test
            org.springframework.plugin:spring-plugin-core:jar:3.0.0:test
            org.springframework.ws:spring-xml:jar:4.0.11:compile
            org.xmlunit:xmlunit-assertj:jar:2.9.0:test
            org.xmlunit:xmlunit-core:jar:2.9.0:test
                                    """;

        assertEquals( expected_result.trim(), resultString.toString().trim() );
        assertEquals( 61, result.size() );
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
