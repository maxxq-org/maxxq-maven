package org.chabernac.dependency;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

public interface IDependencyResolver {
    /**
     * Store a single maven model
     * 
     * @param model
     * @return the GAV of the stored maven model
     * @throws DepencyResolvingException
     */
    public GAV store( Model model ) throws DepencyResolvingException;

    /**
     * Read the maven model from the given input stream and store it
     * 
     * @param inputStream
     * @return the GAV of the stored maven model
     * @throws DepencyResolvingException
     */
    public GAV store( InputStream inputStream ) throws DepencyResolvingException;

    /**
     * Store the given maven model input stream for later processing, inspect whether the maven project is a multi module project If
     * so follow the modules and also store all child modules. Use the relative path to identify the relative locations of the
     * modules. If the maven project is not a multi module project, then still store it, in that case the relative path can be empty
     * or null
     * 
     * @param inputStream
     * @param relativePathOfGivenPomStream
     * @return Return the GAV of the root project as first element of the returned list and the GAV's of all modules as subsequent
     *         elements in the returned list
     */
    public List<GAV> storeMultiModule( InputStream inputStream, String relativePathOfGivenPomStream );

    /**
     * Store the given maven model input stream for later processing, inspect whether the maven project is a multi module project If
     * so follow the modules and also store all child modules. Assume the pom was found at the root so no relative path is required.
     * If the maven project is not a multi module project, then still store it, in that case the relative path can be empty
     * or null
     * 
     * @param inputStream
     * @param relativePathOfGivenPomStream
     * @return Return the GAV of the root project as first element of the returned list and the GAV's of all modules as subsequent
     *         elements in the returned list
     */
    public default List<GAV> storeMultiModule( InputStream inputStream ){
        return storeMultiModule( inputStream, "");
    }

    /**
     * Store the given maven model input stream for later processing, inspect whether the maven project is a multi module project If
     * so follow the modules and also store all child modules. Use the relative path to identify the relative locations of the
     * modules. If the maven project is not a multi module project, then still store it, in that case the relative path can be empty
     * or null return the GAV of the root project as first element of the returned list and the GAV's of all modules as subsequent
     * elements in the returned list
     */

    /**
     * Store the given maven model input stream for later processing, inspect whether the maven project is a multi module project If
     * so follow the modules and also store all child modules. Use the relative path to identify the relative locations of the
     * modules. If the maven project is not a multi module project, then still store it, in that case the relative path can be empty
     * or null
     * 
     * @param model
     * @param relativePathOfGivenPomModel
     * @return List<GAV> Return the GAV of the root project as first element of the returned list and the GAV's of all modules as
     *         subsequent elements in the returned list
     */
    public List<GAV> storeMultiModule( Model model, String relativePathOfGivenPomModel );
    
    /**
     * Store the given maven model for later processing, inspect whether the maven project is a multi module project If
     * so follow the modules and also store all child modules. Assume the pom was found at the root so no relative path is required.
     * If the maven project is not a multi module project, then still store it, in that case the relative path can be empty
     * or null
     * 
     * @param inputStream
     * @param relativePathOfGivenPomStream
     * @return Return the GAV of the root project as first element of the returned list and the GAV's of all modules as subsequent
     *         elements in the returned list
     */
    public default List<GAV> storeMultiModule( Model model ){
        return storeMultiModule( model, "");
    }

    /**
     * Calculate and return the dependencies of the maven artifact with the given GAV (GroupId, ArtifactId, Version). The given GAV
     * can be of a maven project that was previously stored or of a maven project that is known in the configured central repository
     * e.g. maven central
     * 
     * @param gav
     * @return
     * @throws DepencyResolvingException
     */
    public Set<Dependency> getDependencies( GAV gav ) throws DepencyResolvingException;

    /**
     * Calculate and return the dependencies of the maven projects for which the inputstreams of the pom files are given. The
     * operation is the same as first using the store and then the getDependency method with the GAV of the stored maven model
     * 
     * @param pomStreams
     * @return Set<Dependency> The identified dependencies
     * @throws DepencyResolvingException
     */
    public Set<Dependency> getDependencies( InputStream... pomStreams ) throws DepencyResolvingException;

    /**
     * Calculate and return the dependencies of the maven project for which the inputstream of the pom file is given. The operation
     * is the same as first using the store and then the getDependency method with the GAV of the stored maven model
     * 
     * @param pomStream
     * @return Set<Dependency> The identified dependencies
     * @throws DepencyResolvingException
     */
    public Set<Dependency> getDependencies( InputStream pomStream ) throws DepencyResolvingException;

    /**
     * Calculate and return the dependencies of the maven artifacts with the given GAV's (GroupId, ArtifactId, Version). The given
     * GAV's can be of maven projects that were previously stored or of maven projects that are known in the configured central
     * repository e.g. maven central
     * 
     * @param gavs
     * @return Set<Dependency> The identified dependencies
     * @throws DepencyResolvingException
     */
    public Set<Dependency> getDependencies( GAV... gavs ) throws DepencyResolvingException;

    /**
     * Calculate and return the dependencies of the maven artifacts with the given GAV's (GroupId, ArtifactId, Version). The given
     * GAV's can be of maven projects that were previously stored or of maven projects that are known in the configured central
     * repository e.g. maven central
     * 
     * @param gavs
     * @return Set<Dependency> The identified dependencies
     * @throws DepencyResolvingException
     */
    public Set<Dependency> getDependencies( Collection<GAV> gavs );

    /**
     * Calculate and return the dependencies of the maven artifacts with the given GAV's (GroupId, ArtifactId, Version). The given
     * GAV's can be of maven projects that were previously stored or of maven projects that are known in the configured central
     * repository e.g. maven central
     * 
     * @param gavs
     * @return Set<Dependency> The identified dependencies
     * @throws DepencyResolvingException
     */
    public Set<Dependency> getDependencies( Stream<GAV> gavs );

    /**
     * Calculate and return the dependencies of the maven artifacts loaded from the given InputStreams if excludeCorrespondingGavs
     * is true then the returned dependencies will not include the resolved GAV's from the inputstreams. This can be handy if you
     * want to exclude the the modules dependencies in a multi module project
     * 
     * @param excludeCorrespondingGavs
     *            exclude the dependencies corresponding to the given maven projects from the result
     * @param pomStreams
     * @return Set<Dependency> The identified dependencies
     */
    public Set<Dependency> getDependencies( boolean excludeCorrespondingGavs, InputStream... pomStreams );

    /**
     * Calculate and return the dependencies of the maven artifacts with the given GAV's (GroupId, ArtifactId, Version). The given
     * GAV's can be of maven projects that were previously stored or of maven projects that are known in the configured central
     * repository e.g. maven central Exclude from the result the GAVs that are given in gavsToExclude
     * 
     * @param gavs
     * @param gavsToExclude
     *            Dependencies with GAV's listed in gavsToExclude will not be included in the result
     * @return Set<Dependency> The identified dependencies
     */
    public Set<Dependency> getDependencies( Stream<GAV> gavs, Collection<GAV> gavsToExclude );

}
