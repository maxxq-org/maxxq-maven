package org.maxxq.maven.repository;

import java.util.function.Function;

import okhttp3.Request;

public interface IRemoteRepositoryRequestBuilder extends Function<String, Request> {

}
