package org.maxxq.maven.repository;

import org.junit.jupiter.api.Test;

import okhttp3.Request;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultRemoteRepositoryRequestBuilderTest {
    private DefaultRemoteRepositoryRequestBuilder requestBuilder = new DefaultRemoteRepositoryRequestBuilder();

    @Test
    void apply() {
        Request result = requestBuilder.apply( "https://repo1.maven.org/maven2/org/apache/maven/maven/3.3.9/maven-3.3.9.pom" );

        assertEquals( "https://repo1.maven.org/maven2/org/apache/maven/maven/3.3.9/maven-3.3.9.pom", result.url().toString() );
    }

}
