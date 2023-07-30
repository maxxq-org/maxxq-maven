package org.maxxq.maven.repository;

import java.util.Optional;

public interface IRemoteRepositoryAdapter {
    public Optional<CallResponse> call( String endPoint );
}
