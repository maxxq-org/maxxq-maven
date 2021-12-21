# MavenDependencyResolver

Resolve maven dependencies programmatically through simple to use Java classes.

For single pom

    Set<Dependency> resolvedDependencies = new ResolveDependencies("https://repo1.maven.org/maven2/")
                                           .getDependencies(InputStream pomStream)

For reactor build

   
	 Set<Dependency> resolvedDependencies = new ResolveDependencies("https://repo1.maven.org/maven2/")
                                           .getDependencies(InputStream pomStream1, InputStream pomStream2, ...)
Advanced config for repositories

	ResolveDependencies resolveDependencies = new ResolveDependencies(
            new VirtualRepository()
                    .addRepository(new InMemoryCachingRepository(new FileCachingRepository(Paths.get("c:/data/pomcache/"), new RemoteRepository(RemoteRepository.MAVEN_CENTRAL))))
                    .addRepository(new LocalInMemoryRepository())); //at least 1 repo is required to which writing is possible for storage of the reactor pom's
                    
    resolveDependencies.getDependencies(InputStream pomStream)