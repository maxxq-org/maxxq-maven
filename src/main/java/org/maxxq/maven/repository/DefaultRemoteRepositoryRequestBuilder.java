package org.maxxq.maven.repository;

import okhttp3.Headers;
import okhttp3.Request;

public class DefaultRemoteRepositoryRequestBuilder implements IRemoteRepositoryRequestBuilder {
    private final Headers.Builder headerBuilder = new Headers.Builder();

    @Override
    public Request apply( String endPoint ) {
        return new Request.Builder()
            .url( endPoint )
            .headers( headerBuilder.build() )
            .build();
    }

    public DefaultRemoteRepositoryRequestBuilder addHeader( String key, String value ) {
        headerBuilder.add( key, value );
        return this;
    }

}

