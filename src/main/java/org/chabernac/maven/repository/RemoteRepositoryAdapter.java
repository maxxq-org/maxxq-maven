package org.chabernac.maven.repository;

import java.io.InputStream;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RemoteRepositoryAdapter implements IRemoteRepositoryAdapter {
    private static final Logger                   LOGGER = LogManager.getLogger( RemoteRepositoryAdapter.class );
    private final IRemoteRepositoryRequestBuilder requestBuilder;
    private final OkHttpClient                    client = new OkHttpClient();

    public RemoteRepositoryAdapter( IRemoteRepositoryRequestBuilder requestBuilder ) {
        super();
        this.requestBuilder = requestBuilder;
    }

    @Override
    public Optional<InputStream> call( String endPoint ) {
        try {
            Request request = requestBuilder.apply( endPoint );
            Call call = client.newCall( request );
            Response response = call.execute();

            if ( response.code() == 404 ) {
                LOGGER.warn( "no pom file found at: '{}' in remote repo", endPoint );
            } else if ( response.code() != 200 ) {
                throw new RepositoryException(
                    "Could not retrieve pom at endpoint '" + endPoint + "' http response code '" + response.code() + "'" );
            } else {
                return Optional.of( response.body().byteStream() );
            }

            return Optional.empty();

        } catch ( Exception e ) {
            throw new RepositoryException( "Error occured when calling endpoint: + '" + endPoint + "'", e );
        }
    }

}
