package org.chabernac.dependency;

import java.io.InputStream;

public class ClasspathPomStreamProvider implements IPomStreamProvider {

    @Override
    public InputStream loadPomFromRelativeLocation( String relativeLocation ) {
        return getClass().getResourceAsStream( relativeLocation + "/pom.xml" );
    }
}
