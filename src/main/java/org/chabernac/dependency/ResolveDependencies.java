package org.chabernac.dependency;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.chabernac.maven.repository.IRepository;

public class ResolveDependencies implements IDependencyResolver {

    private final IRepository  repository;
    private boolean            ignoreIconsistencies = false;
    private IPomStreamProvider pomStreamProvider;

    public ResolveDependencies( IRepository repository ) {
        this.repository = repository;
    }

    public boolean isIgnoreIconsistencies() {
        return ignoreIconsistencies;
    }

    public ResolveDependencies setIgnoreIconsistencies( boolean ignoreIconsistencies ) {
        this.ignoreIconsistencies = ignoreIconsistencies;
        return this;
    }

    public IPomStreamProvider getPomStreamProvider() {
        return pomStreamProvider;
    }

    public ResolveDependencies setPomStreamProvider( IPomStreamProvider pomStreamProvider ) {
        this.pomStreamProvider = pomStreamProvider;
        return this;
    }

    @Override
    public List<GAV> storeMultiModule( InputStream inputStream, String relativePathOfGivenPomStream ) {
        return storeMultiModule( new ModelIO().getModelFromInputStream( inputStream ), relativePathOfGivenPomStream );
    }

    @Override
    public List<GAV> storeMultiModule( Model model, String relativePathOfGivenPomModel ) {
        List<GAV> gavs = new ArrayList<>();
        GAV gav = repository.store( model );
        gavs.add( gav );
        if ( pomStreamProvider != null && model.getModules() != null ) {
            gavs.addAll( followAndGetGavOfModules( model, relativePathOfGivenPomModel ) );
        }
        return gavs;
    }

    private Collection<GAV> followAndGetGavOfModules( Model model, String relativePathOfGivenPomModel ) {
        return model.getModules()
            .stream()
            .map( module -> relativePathOfGivenPomModel + "/" + module )
            .map( modulePath -> new PomStreamAndRelativeLocation( modulePath, pomStreamProvider.loadPomFromRelativeLocation( modulePath ) ) )
            .filter( pomStreamAndRelativeLocation -> pomStreamAndRelativeLocation.isPresent() )
            .flatMap( pomStreamAndRelativeLocation -> storeMultiModule( pomStreamAndRelativeLocation.getPomStream(), pomStreamAndRelativeLocation.getRelativeLocation() ).stream() )
            .collect( Collectors.toList() );
    }

    private class PomStreamAndRelativeLocation {
        public String                relativeLocation;
        public Optional<InputStream> pomStream;

        public PomStreamAndRelativeLocation( String relativeLocation, Optional<InputStream> pomStream ) {
            super();
            this.relativeLocation = relativeLocation;
            this.pomStream = pomStream;
        }

        public boolean isPresent() {
            return pomStream.isPresent();
        }

        public InputStream getPomStream() {
            return pomStream.get();
        }

        public String getRelativeLocation() {
            return relativeLocation;
        }
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
            return new ResolveDependenciesWorker( model.get(), repository, ignoreIconsistencies ).get();
        } catch ( DepencyResolvingException e ) {
            throw e;
        } catch ( Exception e ) {
            throw new DepencyResolvingException( "Could not resolve dependencies", e );
        }
    }
}
