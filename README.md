# MavenDependencyResolver

Resolve maven dependencies programmatically through simple to use Java classes.

## Getting started

Add maven dependency:

    <dependency>
        <groupId>org.maxxq.maven</groupId>
        <artifactId>maxxq-maven</artifactId>
        <version>1.3.0</version>
    </dependency>

## Resolving dependencies

### For single pom from maven central

    Set<Dependency> resolvedDependencies = new ResolveDependencies(new RemoteRepository( "https://repo1.maven.org/maven2/" ))
                                           .getDependencies(new GAV("com.squareup.okhttp3", "okhttp", "4.9.3"));

### For single pom provided as InputStream

    Set<Dependency> resolvedDependencies = new ResolveDependencies(new RemoteRepository( "https://repo1.maven.org/maven2/" )))
                                           .getDependencies(InputStream pomStream)

### For reactor build with pom's provided as input streams

   
	 Set<Dependency> resolvedDependencies = new ResolveDependencies(new RemoteRepository( "https://repo1.maven.org/maven2/" )))
                                           .getDependencies(InputStream pomStream1, InputStream pomStream2, ...)
                                           
                                           
### For specific modules of reactor build with pom's provided as input streams

     ResolveDependencies resolveDependencies = new ResolveDependencies(new RemoteRepository( "https://repo1.maven.org/maven2/" )))
	  GAV parent = resolveDependencies.store(getClass().getResourceAsStream("/parent/pom.xml"));
     GAV module1 = resolveDependencies.store(getClass().getResourceAsStream("/module1/pom.xml"));
     GAV module2 = resolveDependencies.store(getClass().getResourceAsStream("/module2/pom.xml"));

     Set<Dependency> dependencies = resolveDependencies.getDependencies(module1, module2);
                                       
### Advanced config for repositories

	ResolveDependencies resolveDependencies = new ResolveDependencies(
            new VirtualRepository()
                    .addRepository(new LocalInMemoryRepository()) //at least 1 repo is required to which writing is possible for storage of the reactor pom's
                    .addRepository(new InMemoryCachingRepository(new FileCachingRepository(Paths.get("c:/data/pomcache/"), new RemoteRepository(RemoteRepository.MAVEN_CENTRAL))))); 
    resolveDependencies.getDependencies(InputStream pomStream)
    
### Using custom request builder

A custom request builder can be given to RemoteRepository to craft request with the specific authentication that might be required for private repositories

	new RemoteRepository(RemoteRepository.MAVEN_CENTRAL, customRequestBuilder)


### resolver logic

- traverse parent pom's: copy properties, dependencies and dependency management dependencies if not already existing
- resolve properties in dependency management 
- recursively follow pom includes in dependency management
- apply dependency management on existing dependencies (not on the transitive ones, these are not yet loaded)
- obtain transitive dependencies recursively. Do not replace existing dependencies (shortest path rule & in case of conflict take the first one) 
- reapply dependency management on existing dependencies only for the root pom [dependency management is not transitive](src/main/resources/dependencymanagementisnottransitive/readme.md)

## Calculating an effective pom

### For single pom from maven central

    Model resolvedModel = new ResolveBuildConfiguration(new RemoteRepository( "https://repo1.maven.org/maven2/" ))
                                           .resolveBuildConfiguration(new GAV("com.squareup.okhttp3", "okhttp", "4.9.3"));

Same logic applies as for ResolveDependencies for loading pom from other input sources and for multi module projects.

## links

[maven bom](https://reflectoring.io/maven-bom/)

[maven dependency mechanism](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html)

## Local Deployment
snapshot: mvn deploy -DaltDeploymentRepository=ossrh::default::https://s01.oss.sonatype.org/content/repositories/snapshots
release:  mvn deploy -DaltDeploymentRepository=ossrh::default::https://s01.oss.sonatype.org/service/local/staging/deploy/maven2

## Deployment with github action
https://github.com/marketplace/actions/action-maven-publish

### create release version
	git checkout release/[1-9] 1-9: major version of the release
	git reset --hard master	      : reset to the last version for which a release needs to be created
	mvn versions:set              : set the version to the desired release version
	git push --force	      : force the release branch to this version containing the release version

github action [worfklow for code quality](.github/workflows/codeql-analysis.yml) will be triggered after which 
github action [workflow for release version](.github/workflows/maven-publish-release-sonatype.yml) for publishing the release is executed

after successfull execution the release version is uploaded to sonatype where a manual approval will result in a final publishing of the jar.
Go to [https://s01.oss.sonatype.org/#stagingRepositories](https://s01.oss.sonatype.org/#stagingRepositories) and login with your crendentials.  Find the jar ready for publication and either choose 'close' to finalize the publication or 'reject' to discard the release version.  [https://central.sonatype.org/publish/release/#locate-and-examine-your-staging-repository](sonatype publish staged artifacts)

After closing the artifact is available on [https://search.maven.org/](https://search.maven.org/) but not yet on [https://mvnrepository.com/repos/central](https://mvnrepository.com/repos/central), to be analysed.

### create snapshot version
After publishing to master the [workflow for snapshot version](.github/workflows/maven-publish-snapshot-sonatype.yml) will be triggered which will automatically publish the snapshot version, which is made available on https://s01.oss.sonatype.org/content/repositories/snapshots

## References
https://maven.apache.org/repository/guide-central-repository-upload.html

https://dzone.com/articles/how-to-create-and-release-a-jar-to-maven-central

## Release notes

## 1.3.1
- Replace mockito-all with mockito-core
- Compile with Java 17, target Java 8

### 1.3.0
- Capability of providing a custom dependency filter with ResolveDependencies.setDependencyFilter().

	e.g. ResolveDependencies resolveDependencies = new ResolveDependencies(repo)
				.setDependenyFilter( ( dependency, depth ) -> dependency.getScope().equals( "compile" ) ); //will only retain dependencies with scope 'compile'
				.setDependenyFilter( ( dependency, depth ) -> depth <= 1 );                                //will only retain dependencies from the root and first level transitive dependencies
	
By default DefaultDependencyFilter will be used.  DefaultDependencyFilter will retain:
- dependencies with scope: compile and runtime.  
- dependencies with scope test only for the given pom and not for transitive dependencies.

- upgrade several dependencies: maven-model (3.9.4), maven-settings (3.9.4), okio (3.5.0), commons-lang3 (3.13.0), kotlin-stdlib-common (1.9.10), kotlin-stdlib (1.9.10), maven-repository-metadata (3.9.4)

### 1.2.0
- Implementations of IRepository (FileCachingRespository, LocalFileRepository, RemoteRepository) will return an instance of org.maxxq.maven.model.MavenModel instead of org.apache.maven.model.Model when readPom(GAV gav) is being invoked.  Because org.maxxq.maven.model.MavenModel extends org.apache.maven.model.Model the modifications are fully backwards compatible.  IRepository still defines org.apache.maven.model.Model as return type for readPom(GAV gav).  The user of the library might choose to cast the returned object to org.maxxq.maven.model.MavenModel to get access to additional properties like the creationDate.  The creaztion date contained in org.maxxq.maven.model.MavenModel represents the date the corresponding maven artifact was uploaded to maven central.

### 1.1.3
- ResolveBuildConfiguration did not resolve properties that refer to other properties

### 1.1.2
- Fix NullPointerException when ResolveBuildConfiguration encounters a parent pom.xml with empty build section

### 1.1.1
- Fix NullPointerException when ResolveBuildConfiguration encounters a pom.xml with empty PluginManagement Section

### 1.1.0



### 1.0.0
- First stable release version

