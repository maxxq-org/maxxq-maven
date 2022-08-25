package org.javaa.maven.dependency;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.javaa.maven.repository.IRepository;

public class ResolveRange implements Function<GAV, Optional<String>> {
    private final IRepository repository;

    public ResolveRange( IRepository repository ) {
        super();
        this.repository = repository;
    }

    @Override
    public Optional<String> apply( GAV gav ) {
        try {
            if ( !isRange( gav.getVersion() ) ) {
                return Optional.of( gav.getVersion() );
            }
            VersionRange range = VersionRange.createFromVersionSpec( gav.getVersion() );
            Optional<Metadata> metaData = repository.getMetaData( gav.getGroupId(), gav.getArtifactId() );
            return metaData.flatMap(
                mataData -> mataData.getVersioning()
                    .getVersions()
                    .stream()
                    .sorted(Collections.reverseOrder())
                    .filter( version -> range.containsVersion( new DefaultArtifactVersion( version ) ) )
                    .findFirst() );

        } catch ( InvalidVersionSpecificationException e ) {
            throw new IllegalArgumentException( "Version '" + gav.getVersion() + "' is not a range version", e );
        }
    }

    public static boolean isRange( String version ) {
        return version.contains( "," );
    }

}
