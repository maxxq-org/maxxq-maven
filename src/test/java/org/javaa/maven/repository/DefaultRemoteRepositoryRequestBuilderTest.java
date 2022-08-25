package org.javaa.maven.repository;

import org.javaa.maven.repository.DefaultRemoteRepositoryRequestBuilder;
import org.junit.Assert;
import org.junit.Test;

import okhttp3.Request;

public class DefaultRemoteRepositoryRequestBuilderTest {
    private DefaultRemoteRepositoryRequestBuilder requestBuilder = new DefaultRemoteRepositoryRequestBuilder();

    @Test
    public void apply() {
        Request result = requestBuilder.apply( "https://repo1.maven.org/maven2/org/apache/maven/maven/3.3.9/maven-3.3.9.pom" );

        Assert.assertEquals( "https://repo1.maven.org/maven2/org/apache/maven/maven/3.3.9/maven-3.3.9.pom", result.url().toString() );
    }

}
