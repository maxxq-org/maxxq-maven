package org.maxxq.maven.configuration;

import java.io.InputStream;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Model;
import org.maxxq.maven.dependency.GAV;
import org.maxxq.maven.dependency.ModelIO;
import org.maxxq.maven.repository.IRepository;
import org.maxxq.maven.repository.RepositoryException;

public class ResolveBuildConfiguration implements IConfigurationResolver {
    private static final Logger LOGGER = LogManager.getLogger( ResolveBuildConfiguration.class );

    private final IRepository   repository;

    public ResolveBuildConfiguration( IRepository repository ) {
        this.repository = repository;
    }

    @Override
    public Optional<Model> resolveBuildConfiguration( GAV projectIdentifier ) throws RepositoryException {
        Optional<Model> model = repository.readPom( projectIdentifier );
        if ( model.isPresent() ) {
            return Optional.of( resolveBuildConfiguration( model.get() ) );
        }
        return Optional.empty();
    }

    @Override
    public Model resolveBuildConfiguration( InputStream inputStream ) throws RepositoryException {
        Model model = new ModelIO().getModelFromInputStream( inputStream );
        return resolveBuildConfiguration( model );
    }

    @Override
    public Model resolveBuildConfiguration( Model model ) throws RepositoryException {
        new ResolveBuildConfigurationWorker( model, repository ).run();
        return model;
    }

    @Override
    public GAV store( Model model ) {
        return repository.store( model );
    }

    @Override
    public GAV store( InputStream inputStream ) {
        return store( new ModelIO().getModelFromInputStream( inputStream ) );
    }

}
