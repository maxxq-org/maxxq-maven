package org.chabernac.maven.repository;

import java.io.InputStream;
import java.util.Optional;

public interface IRemoteRepositoryAdapter {
    public Optional<InputStream> call( String endPoint );
}
