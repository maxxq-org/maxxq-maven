package org.javaa.maven.repository;

import java.util.Optional;

public interface IRemoteRepositoryAdapter {
    public Optional<String> call( String endPoint );
}
