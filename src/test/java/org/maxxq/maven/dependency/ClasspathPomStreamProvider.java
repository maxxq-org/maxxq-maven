package org.maxxq.maven.dependency;

import java.io.InputStream;
import java.util.Optional;

import org.maxxq.maven.dependency.IPomStreamProvider;

public class ClasspathPomStreamProvider implements IPomStreamProvider {

    @Override
    public Optional<InputStream> loadPomFromRelativeLocation( String relativeLocation ) {
        return Optional.ofNullable( getClass().getResourceAsStream( relativeLocation + "/pom.xml" ) );
    }
}
