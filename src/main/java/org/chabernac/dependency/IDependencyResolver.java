package org.chabernac.dependency;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

public interface IDependencyResolver {
    public Set<Dependency> getDependencies( GAV gav ) throws DepencyResolvingException;

    public GAV store( Model model ) throws DepencyResolvingException;

    public GAV store( InputStream inputStream ) throws DepencyResolvingException;

    public List<GAV> storeMultiModule( InputStream inputStream, String relativePathOfGivenPomStream );

    public List<GAV> storeMultiModule( Model model, String relativePathOfGivenPomModel );

    public Set<Dependency> getDependencies( InputStream... pomStreams ) throws DepencyResolvingException;

    public Set<Dependency> getDependencies( InputStream pomStream ) throws DepencyResolvingException;

    public Set<Dependency> getDependencies( GAV... gavs ) throws DepencyResolvingException;

    public Set<Dependency> getDependencies( Collection<GAV> gavs );

    public Set<Dependency> getDependencies( Stream<GAV> gavs );

    public Set<Dependency> getDependencies( boolean excludeCorrespondingGavs, InputStream... pomStreams );

    public Set<Dependency> getDependencies( Stream<GAV> gavs, Collection<GAV> gavsToExclude );

}
