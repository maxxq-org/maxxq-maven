# MavenDependencyResolver

Resolve maven dependencies programmatically through simple to use Java classes.

## Getting started

Add maven dependency:

    <dependency>
        <groupId>org.maxxq.maven</groupId>
        <artifactId>maxxq-maven</artifactId>
        <version>1.3.3</version>
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


### Resolver Logic

- Traverse parent pom's: copy properties, dependencies and dependency management dependencies if not already existing
- Resolve properties in dependency management 
- Recursively follow pom includes in dependency management
- Apply dependency management on existing dependencies (not on the transitive ones, these are not yet loaded)
- Obtain transitive dependencies recursively. Do not replace existing dependencies (shortest path rule & in case of conflict take the first one) 
- Reapply dependency management on existing dependencies only for the root pom [dependency management is not transitive](src/main/resources/dependencymanagementisnottransitive/readme.md)

## Calculating an effective pom

### For single pom from maven central

    Model resolvedModel = new ResolveBuildConfiguration(new RemoteRepository( "https://repo1.maven.org/maven2/" ))
                                           .resolveBuildConfiguration(new GAV("com.squareup.okhttp3", "okhttp", "4.9.3"));

Same logic applies as for ResolveDependencies for loading pom from other input sources and for multi module projects.

## Links

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
	mvn versions:set              : set the version to the desired release version.  Depending on the change it should be either an increment of major, minor or patch version.  Major version is onlyi incremented if a non backwards compatible change has been made.  Minor version is incremented if a new feauture has been added.  Patch version is incremented if a bug has been fixed.
	git add --all && git commit		: Commit the change of the version in pom.xml to the release branch
	git push --force	      : do a forced push of the change to the remote
	git checkokut development 	: Switch back to the development branch
	git merge release/[1-9] 		: Merge the release branch in the development branch
	mvn versions:set				: Increment the patch version and make it again a snapshot version.
	git add --all && git commit	: Commit the change of the version in the pom.xml
	git push							: Push the change to the remote development branch
	
Update CHANGELOG.MD and document all changes that have been introduced since the previous release version.  Indicate for each change if it was a fix, new feature or non backwards compatible change.

github action [worfklow for code quality](.github/workflows/codeql-analysis.yml) will be triggered after which 
github action [workflow for release version](.github/workflows/maven-publish-release-sonatype.yml) for publishing the release is executed

after successfull execution the release version is uploaded to sonatype where a manual approval will result in a final publishing of the jar.
Go to [https://central.sonatype.com/publishing](https://central.sonatype.com/publishing) and login with your crendentials.  Find the jar ready for publication and either choose 'Publish' to finalize the publication or 'Drop' to discard the release version.  

After closing the artifact is available on [https://search.maven.org/](https://search.maven.org/) but not yet on [https://mvnrepository.com/repos/central](https://mvnrepository.com/repos/central), to be analysed.

### create snapshot version
After publishing to master the [workflow for snapshot version](.github/workflows/maven-publish-snapshot-sonatype.yml) will be triggered which will automatically publish the snapshot version, which is made available on https://s01.oss.sonatype.org/content/repositories/snapshots

## References
https://maven.apache.org/repository/guide-central-repository-upload.html

https://dzone.com/articles/how-to-create-and-release-a-jar-to-maven-central

