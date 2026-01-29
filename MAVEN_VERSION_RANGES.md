# Maven Version Ranges

## Class Identification

**Maven version ranges are dealt with in the `ResolveRange` class.**

### Class Details

- **Package**: `org.maxxq.maven.dependency`
- **Full Class Name**: `org.maxxq.maven.dependency.ResolveRange`
- **Location**: `src/main/java/org/maxxq/maven/dependency/ResolveRange.java`

### Functionality

The `ResolveRange` class is responsible for:

1. **Detecting version ranges** - Uses the `isRange(String version)` static method to determine if a version string contains a range (by checking for the presence of a comma).

2. **Resolving version ranges** - Implements `Function<GAV, Optional<String>>` to resolve a version range to a specific version by:
   - Parsing the version range using Apache Maven's `VersionRange.createFromVersionSpec()`
   - Fetching available versions from the repository metadata
   - Filtering versions that match the range criteria
   - Returning the highest matching version (versions are sorted in reverse order)

3. **Handling non-range versions** - If the version is not a range, it simply returns the version as-is.

### Key Dependencies

The class uses:
- `org.apache.maven.artifact.versioning.VersionRange` - For parsing and working with version ranges
- `org.apache.maven.artifact.versioning.DefaultArtifactVersion` - For version comparison
- `org.maxxq.maven.repository.IRepository` - For fetching metadata about available versions

### Usage

The `ResolveRange` class is used by:
- `ResolveDependenciesWorker` - Which instantiates it to resolve version ranges during dependency resolution

### Example

```java
ResolveRange resolveRange = new ResolveRange(repository);
GAV gav = new GAV("groupId", "artifactId", "[1.0.0,1.0.1]");
Optional<String> resolvedVersion = resolveRange.apply(gav);
// Returns "1.0.1" if versions 1.0.0, 1.0.1, and 1.0.2 are available
```

### Test Coverage

The class has comprehensive test coverage in:
- `src/test/java/org/maxxq/maven/dependency/ResolveRangeTest.java`

Tests cover:
- Resolving a valid version range
- Handling non-range versions
- Handling invalid version ranges
