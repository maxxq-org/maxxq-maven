package org.chabernac.dependency;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.chabernac.maven.repository.IRepository;

public class ResolveDependencies implements IDependencyResolver {

    private final IRepository repository;

    public ResolveDependencies( IRepository repository ) {
        this.repository = repository;
    }

    @Override
    public GAV store( Model model ) {
        return repository.store( model );
    }

    @Override
    public GAV store( InputStream inputStream ) {
        return store( new ModelIO().getModelFromInputStream( inputStream ) );
    }

    @Override
    public Set<Dependency> getDependencies( InputStream... pomStreams ) {
        Set<GAV> gavs = Arrays.stream( pomStreams )
            .map( pomStream -> store( pomStream ) )
            .collect( Collectors.toSet() );

        return getDependencies( gavs.stream() );
    }

    @Override
    public Set<Dependency> getDependencies( boolean excludeCorrespondingGavs, InputStream... pomStreams ) {
        Set<GAV> gavs = Arrays.stream( pomStreams )
            .map( pomStream -> store( pomStream ) )
            .collect( Collectors.toSet() );

        return getDependencies( gavs.stream(), excludeCorrespondingGavs ? gavs : new ArrayList<GAV>() );
    }

    @Override
    public Set<Dependency> getDependencies( InputStream pomStream ) {
        GAV gav = store( pomStream );
        return getDependencies( gav );
    }

    @Override
    public Set<Dependency> getDependencies( Collection<GAV> gavs ) {
        return getDependencies( gavs.stream() );
    }

    @Override
    public Set<Dependency> getDependencies( Stream<GAV> gavs ) {
        return gavs.flatMap( gav -> getDependencies( gav ).stream() )
            .collect( Collectors.toSet() );
    }

    @Override
    public Set<Dependency> getDependencies( Stream<GAV> gavs, Collection<GAV> gavsToExclude ) {
        return gavs.flatMap( gav -> getDependencies( gav ).stream() )
            .filter( dependency -> !gavsToExclude.contains( GAV.fromDependency( dependency ) ) )
            .collect( Collectors.toSet() );
    }

    @Override
    public Set<Dependency> getDependencies( GAV... gavs ) {
        return getDependencies( Arrays.stream( gavs ) );
    }

    @Override
    public Set<Dependency> getDependencies( GAV gav ) {
        try {
            Optional<Model> model = repository.readPom( gav );
            if ( !model.isPresent() ) {
                return new LinkedHashSet<>();
            }
            return new ResolveDependenciesWorker( model.get(), repository ).get();
        } catch ( DepencyResolvingException e ) {
            throw e;
        } catch ( Exception e ) {
            throw new DepencyResolvingException( "Could not resolve dependencies", e );
        }
    }
}
