# MavenDependencyResolver

Resolve maven dependencies programmatically through simple to use Java classes.

## For single pom from maven central

    Set<Dependency> resolvedDependencies = new ResolveDependencies("https://repo1.maven.org/maven2/")
                                           .getDependencies(new GAV("com.squareup.okhttp3", "okhttp", "4.9.3"));

## For single pom provided as InputStream

    Set<Dependency> resolvedDependencies = new ResolveDependencies("https://repo1.maven.org/maven2/")
                                           .getDependencies(InputStream pomStream)

## For reactor build with pom's provided as input streams

   
	 Set<Dependency> resolvedDependencies = new ResolveDependencies("https://repo1.maven.org/maven2/")
                                           .getDependencies(InputStream pomStream1, InputStream pomStream2, ...)
                                           
                                           
## For specific modules of reactor build with pom's provided as input streams

     ResolveDependencies resolveDependencies = new ResolveDependencies("https://repo1.maven.org/maven2/")
	  GAV parent = resolveDependencies.store(getClass().getResourceAsStream("/parent/pom.xml"));
     GAV module1 = resolveDependencies.store(getClass().getResourceAsStream("/module1/pom.xml"));
     GAV module2 = resolveDependencies.store(getClass().getResourceAsStream("/module2/pom.xml"));

     Set<Dependency> dependencies = resolveDependencies.getDependencies(module1, module2);
                                       
## Advanced config for repositories

	ResolveDependencies resolveDependencies = new ResolveDependencies(
            new VirtualRepository()
                    .addRepository(new LocalInMemoryRepository()) //at least 1 repo is required to which writing is possible for storage of the reactor pom's
                    .addRepository(new InMemoryCachingRepository(new FileCachingRepository(Paths.get("c:/data/pomcache/"), new RemoteRepository(RemoteRepository.MAVEN_CENTRAL))))); 
    resolveDependencies.getDependencies(InputStream pomStream)
    
## Using custom request builder

A custom request builder can be given to RemoteRepository to craft request with the specific authentication that might be required for private repositories

	new RemoteRepository(RemoteRepository.MAVEN_CENTRAL, customRequestBuilder)


## resolver logic

- traverse parent pom's: copy properties, dependencies and dependency management dependencies if not already existing
- resolve properties in dependency management 
- recursively follow pom includes in dependency management
- apply dependency management on existing dependencies (not on the transitive ones, these are not yet loaded)
- obtain transitive dependencies recursively. Do not replace existing dependencies (shortest path rule & in case of conflict take the first one) 
- reapply dependency management on existing dependencies only for the root pom [dependency management is not transitive](src/main/resources/dependencymanagementisnottransitive/readme.md)


## links

[maven bom](https://reflectoring.io/maven-bom/)

[maven dependency mechanism](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html)
