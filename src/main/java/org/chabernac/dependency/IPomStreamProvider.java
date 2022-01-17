package org.chabernac.dependency;

import java.io.InputStream;

public interface IPomStreamProvider {
    public InputStream loadPomFromRelativeLocation( String relativeLocation );
}
