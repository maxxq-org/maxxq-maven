package org.maxxq.maven.dependency;

import java.io.InputStream;
import java.util.Optional;

public interface IPomStreamProvider {
    public Optional<InputStream> loadPomFromRelativeLocation( String relativeLocation );
}
