package org.maxxq.maven.configuration;

import java.io.InputStream;

import org.apache.maven.model.Model;
import org.maxxq.maven.dependency.GAV;
import org.maxxq.maven.repository.RepositoryException;

public interface IConfigurationResolver {
    public Model resolve( Model model ) throws RepositoryException;

    public Model resolve( InputStream inputStream ) throws RepositoryException;

    public GAV store( Model model );

    public GAV store( InputStream inputStream );

}
