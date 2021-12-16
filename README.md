# MavenDependencyResolver

Resolve maven dependencies programmatically through simple to use Java classes.

For single pom

    Set<Dependency> resolvedDependencies = new ResolveDependencies("https://repo1.maven.org/maven2/")
                                           .getDependencies(InputStream pomStream)

For reactor build

   
