## Release notes

## 1.3.3 (29/07/2025)
- Update GPG plugin to 3.2.7
- Fix: fix signing of generated artifacts
- Fix: Improve scope calculation for transitive dependencies
- Refactor: Switch to Java 17 to be able to make use of text blocks 
- Update several dependencies

## 1.3.2 (30/9/2024)
- Various dependency updates
- Rewrite unit tests from JUnit 4 to JUnit 5 with OpenRewrite
- Fix: #227: JUnit detected as dependency while it has been excluded
- Update Log4j to 2.24.1
- Update rewrite-testing-frameworks to 2.19.0
- Update maven-settings to 3.9.9
- Update mockito-junit-jupiter to 5.14.0
- Update okio to 3.9.1
- Update junit-jupiter to 5.11.1
- Update commons-lang3 to 3.17.0
- Update maven-repository-metadata to 3.9.9

## 1.3.1
- Replace mockito-all with mockito-core
- Continue resolving versions from properties even if parent pom is not found
- InMemoryCachingRepository can also store a given maven Model
- Update log4j to 2.22.0
- Update maven-model to 3.9.5
- Update okhttp to 4.12.0
- Update okio to 3.6.0
- Update commons-lang3 to 3.14.0
- Update kotlin-stdlib-common to 1.9.21
- Update kotlin-stdlib to 1.9.20
- Update maven-artifact to 3.9.5
- Update maven-repository-metadata to 3.9.5

## 1.3.0
- Capability of providing a custom dependency filter with ResolveDependencies.setDependencyFilter().

	e.g. ResolveDependencies resolveDependencies = new ResolveDependencies(repo)
				.setDependenyFilter( ( dependency, depth ) -> dependency.getScope().equals( "compile" ) ); //will only retain dependencies with scope 'compile'
				.setDependenyFilter( ( dependency, depth ) -> depth <= 1 );                                //will only retain dependencies from the root and first level transitive dependencies
	
By default DefaultDependencyFilter will be used.  DefaultDependencyFilter will retain:
- dependencies with scope: compile and runtime.  
- dependencies with scope test only for the given pom and not for transitive dependencies.

- upgrade several dependencies: maven-model (3.9.4), maven-settings (3.9.4), okio (3.5.0), commons-lang3 (3.13.0), kotlin-stdlib-common (1.9.10), kotlin-stdlib (1.9.10), maven-repository-metadata (3.9.4)

## 1.2.0
- Implementations of IRepository (FileCachingRespository, LocalFileRepository, RemoteRepository) will return an instance of org.maxxq.maven.model.MavenModel instead of org.apache.maven.model.Model when readPom(GAV gav) is being invoked.  Because org.maxxq.maven.model.MavenModel extends org.apache.maven.model.Model the modifications are fully backwards compatible.  IRepository still defines org.apache.maven.model.Model as return type for readPom(GAV gav).  The user of the library might choose to cast the returned object to org.maxxq.maven.model.MavenModel to get access to additional properties like the creationDate.  The creaztion date contained in org.maxxq.maven.model.MavenModel represents the date the corresponding maven artifact was uploaded to maven central.

### 1.1.3
- ResolveBuildConfiguration did not resolve properties that refer to other properties

## 1.1.2
- Fix NullPointerException when ResolveBuildConfiguration encounters a parent pom.xml with empty build section

## 1.1.1
- Fix NullPointerException when ResolveBuildConfiguration encounters a pom.xml with empty PluginManagement Section

## 1.1.0



# 1.0.0
- First stable release version