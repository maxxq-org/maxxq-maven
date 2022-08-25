package org.javaa.maven.dependency;

import java.io.InputStream;
import java.util.Optional;

import org.javaa.maven.dependency.IPomStreamProvider;

public class ClasspathPomStreamProvider implements IPomStreamProvider {

    @Override
    public Optional<InputStream> loadPomFromRelativeLocation( String relativeLocation ) {
        return Optional.ofNullable( getClass().getResourceAsStream( relativeLocation + "/pom.xml" ) );
    }
}
