package org.maxxq.maven.configuration;

import java.io.InputStream;
import java.util.Optional;

import org.apache.maven.model.Model;
import org.maxxq.maven.dependency.GAV;
import org.maxxq.maven.repository.RepositoryException;

public interface IConfigurationResolver {
    public Model resolveBuildConfiguration( Model model ) throws RepositoryException;

    public Model resolveBuildConfiguration( InputStream inputStream ) throws RepositoryException;
    
    public Optional<Model> resolveBuildConfiguration( GAV projectIdentifier ) throws RepositoryException;

    public GAV store( Model model );

    public GAV store( InputStream inputStream );

}
