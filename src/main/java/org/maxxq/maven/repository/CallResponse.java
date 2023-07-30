package org.maxxq.maven.repository;

import okhttp3.Headers;

public class CallResponse {
    private final String  body;
    private final Headers headers;

    public CallResponse( String body, Headers headers ) {
        super();
        this.body = body;
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public Headers getHeaders() {
        return headers;
    }

}
