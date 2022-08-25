package org.javaa.maven.dependency;

import java.util.function.Function;

public class GetMavenRepoURL implements Function<GAV, String> {
    private final String centralRepoURL;

    public GetMavenRepoURL( String centralRepoURL ) {
        super();
        this.centralRepoURL = centralRepoURL;
    }

    @Override
    public String apply( GAV dependency ) {
        StringBuilder builder = new StringBuilder();
        builder.append( centralRepoURL );
        if ( !centralRepoURL.endsWith( "/" ) ) {
            builder.append( "/" );
        }
        builder.append( dependency.getGroupId().replace( ".", "/" ) );
        builder.append( "/" );
        builder.append( dependency.getArtifactId() );
        builder.append( "/" );
        builder.append( dependency.getVersion() );
        builder.append( "/" );
        builder.append( dependency.getArtifactId() );
        builder.append( "-" );
        builder.append( dependency.getVersion() );
        builder.append( ".pom" );
        return builder.toString();
    }

}
