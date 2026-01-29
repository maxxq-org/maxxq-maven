# Maven Version Ranges

## Class Identification

**Maven version ranges are dealt with in the `ResolveRange` class.**

### Class Details

- **Package**: `org.maxxq.maven.dependency`
- **Full Class Name**: `org.maxxq.maven.dependency.ResolveRange`
- **Location**: `src/main/java/org/maxxq/maven/dependency/ResolveRange.java`

### Functionality

The `ResolveRange` class is responsible for:

1. **Detecting version ranges** - Uses the `isRange(String version)` static method to determine if a version string represents a range. A version is considered a range if it contains:
   - A comma `,` (e.g., `[1.0.0,1.0.1]`, `(1.0.0,2.0.0)`)
   - Square brackets `[` or `]` (e.g., `[1.0.0]`, `[1.0.0,)`)
   - Parentheses `(` or `)` (e.g., `(1.0.0,2.0.0]`)

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

Maven version range syntax:
- `[1.0.0]` - Exactly version 1.0.0
- `[1.0.0,1.0.1]` - Any version from 1.0.0 to 1.0.1 (inclusive)
- `[1.0.0,)` - Any version 1.0.0 or higher
- `(1.0.0,2.0.0)` - Any version between 1.0.0 and 2.0.0 (exclusive)
- `(1.0.0,2.0.0]` - Any version between 1.0.0 (exclusive) and 2.0.0 (inclusive)

Usage example:
```java
ResolveRange resolveRange = new ResolveRange(repository);

// Exact version range
GAV gav1 = new GAV("groupId", "artifactId", "[1.0.0]");
Optional<String> version1 = resolveRange.apply(gav1);
// Returns "1.0.0" if it exists

// Range with upper bound
GAV gav2 = new GAV("groupId", "artifactId", "[1.0.0,1.0.1]");
Optional<String> version2 = resolveRange.apply(gav2);
// Returns "1.0.1" if versions 1.0.0, 1.0.1, and 1.0.2 are available

// Open-ended range
GAV gav3 = new GAV("groupId", "artifactId", "[1.0.0,)");
Optional<String> version3 = resolveRange.apply(gav3);
// Returns the highest available version >= 1.0.0
```

### Test Coverage

The class has comprehensive test coverage in:
- `src/test/java/org/maxxq/maven/dependency/ResolveRangeTest.java`

Tests cover:
- Resolving a valid version range with upper and lower bounds
- Handling non-range versions
- Handling invalid version ranges
- Resolving single version ranges (e.g., `[1.0.0]`)
- Resolving open-ended ranges (e.g., `[1.0.0,)`)
