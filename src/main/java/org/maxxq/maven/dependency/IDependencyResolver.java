package org.maxxq.maven.dependency;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.maxxq.maven.repository.RepositoryException;

public interface IDependencyResolver {
    /**
     * Store a single maven model
     * 
     * @param model The maven model to be stored
     * @return the GAV of the stored maven model
     * @throws RepositoryException thrown when there was an issue storing the maven model
     */
    public GAV store( Model model ) throws RepositoryException;

    /**
     * Read the maven model from the given input stream and store it
     * 
     * @param inputStream The inputstream for the maven module to be stored
     * @return the GAV of the stored maven model
     * @throws RepositoryException thrown when there was an issue storing the maven model
     */
    public GAV store( InputStream inputStream ) throws RepositoryException;

    /**
     * Store the given maven model input stream for later processing, inspect whether the maven project is a multi module project If
     * so follow the modules and also store all child modules. Use the relative path to identify the relative locations of the
     * modules. If the maven project is not a multi module project, then still store it, in that case the relative path can be empty
     * or null
     * 
     * @param inputStream The inputstream to the parent pom file
     * @param relativePathOfGivenPomStream The relative path where the pom files for the child modules can be found
     * @return Return the GAV of the root project as first element of the returned list and the GAV's of all modules as subsequent
     *         elements in the returned list
     * @throws RepositoryException thrown when there was an issue storing the maven model
     */
    public List<GAV> storeMultiModule( InputStream inputStream, String relativePathOfGivenPomStream ) throws RepositoryException;

    /**
     * Store the given maven model input stream for later processing, inspect whether the maven project is a multi module project If
     * so follow the modules and also store all child modules. Assume the pom was found at the root so no relative path is required.
     * If the maven project is not a multi module project, then still store it, in that case the relative path can be empty
     * or null
     * 
     * @param inputStream The inputstream to the parent pom file
     * @return Return the GAV of the root project as first element of the returned list and the GAV's of all modules as subsequent
     *         elements in the returned list
     * @throws RepositoryException thrown when there was an issue storing the maven model
     */
    public default List<GAV> storeMultiModule( InputStream inputStream ) throws RepositoryException{
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
     * @param model The parent maven model of the multi module project to be stored 
     * @param relativePathOfGivenPomModel The relative path where the child modules can be found
     * @return List Return the GAV of the root project as first element of the returned list and the GAV's of all modules as
     *         subsequent elements in the returned list
     * @throws RepositoryException thrown when there was an issue storing the maven model         
     */
    public List<GAV> storeMultiModule( Model model, String relativePathOfGivenPomModel ) throws RepositoryException;
    
    /**
     * Store the given maven model for later processing, inspect whether the maven project is a multi module project If
     * so follow the modules and also store all child modules. Assume the pom was found at the root so no relative path is required.
     * If the maven project is not a multi module project, then still store it, in that case the relative path can be empty
     * or null
     * 
     * @param model The parent maven model of the multi module project to be stored
     * @return Return the GAV of the root project as first element of the returned list and the GAV's of all modules as subsequent
     *         elements in the returned list
     * @throws RepositoryException thrown when there was an issue storing the maven model       
     */
    public default List<GAV> storeMultiModule( Model model ) throws RepositoryException{
        return storeMultiModule( model, "");
    }

    /**
     * Calculate and return the dependencies of the maven artifact with the given GAV (GroupId, ArtifactId, Version). The given GAV
     * can be of a maven project that was previously stored or of a maven project that is known in the configured central repository
     * e.g. maven central
     * 
     * @param gav The Groupid-Artifactid-Version for which the dependencies need to be resolved
     * @return Set of found dependencies
     * @throws RepositoryException thrown when there was an issue storing the maven model
     * @throws DepencyResolvingException thrown when there was an issue obtaining the list of dependencies
     */
    public Set<Dependency> getDependencies( GAV gav ) throws RepositoryException, DepencyResolvingException;

    /**
     * Calculate and return the dependencies of the maven projects for which the inputstreams of the pom files are given. The
     * operation is the same as first using the store and then the getDependency method with the GAV of the stored maven model
     * 
     * @param pomStreams Inputstreams to the pom files for which dependencies will be resolved
     * @return Set The identified dependencies
     * @throws RepositoryException thrown when there was an issue storing the maven model
     * @throws DepencyResolvingException thrown when there was an issue obtaining the list of dependencies
     */
    public Set<Dependency> getDependencies( InputStream... pomStreams ) throws RepositoryException, DepencyResolvingException;

    /**
     * Calculate and return the dependencies of the maven project for which the inputstream of the pom file is given. The operation
     * is the same as first using the store and then the getDependency method with the GAV of the stored maven model
     * 
     * @param pomStream Stream to the pom file for which dependencies will be resolved
     * @return Set The identified dependencies
     * @throws RepositoryException thrown when there was an issue storing the maven model
     * @throws DepencyResolvingException thrown when there was an issue obtaining the list of dependencies
     */
    public Set<Dependency> getDependencies( InputStream pomStream ) throws RepositoryException, DepencyResolvingException;

    /**
     * Calculate and return the dependencies of the maven artifacts with the given GAV's (GroupId, ArtifactId, Version). The given
     * GAV's can be of maven projects that were previously stored or of maven projects that are known in the configured central
     * repository e.g. maven central
     * 
     * @param gavs List of Groupid-Artifact-Versions for which the dependencies will be resolved
     * @return Set The identified dependencies
     * @throws DepencyResolvingException thrown when there was an issue obtaining the list of dependencies
     */
    public Set<Dependency> getDependencies( GAV... gavs ) throws DepencyResolvingException;

    /**
     * Calculate and return the dependencies of the maven artifacts with the given GAV's (GroupId, ArtifactId, Version). The given
     * GAV's can be of maven projects that were previously stored or of maven projects that are known in the configured central
     * repository e.g. maven central
     * 
     * @param gavs Collection of Groupid-Artifact-Versions for which the dependencies will be resolved
     * @return Set The identified dependencies
     * @throws DepencyResolvingException thrown when there was an issue obtaining the list of dependencies
     */
    public Set<Dependency> getDependencies( Collection<GAV> gavs ) throws DepencyResolvingException;

    /**
     * Calculate and return the dependencies of the maven artifacts with the given GAV's (GroupId, ArtifactId, Version). The given
     * GAV's can be of maven projects that were previously stored or of maven projects that are known in the configured central
     * repository e.g. maven central
     * 
     * @param gavs Stream of Groupid-Artifact-Versions for which the dependencies will be resolved
     * @return Set The identified dependencies
     * @throws DepencyResolvingException thrown when there was an issue obtaining the list of dependencies
     */
    public Set<Dependency> getDependencies( Stream<GAV> gavs ) throws DepencyResolvingException;

    /**
     * Calculate and return the dependencies of the maven artifacts loaded from the given InputStreams if excludeCorrespondingGavs
     * is true then the returned dependencies will not include the resolved GAV's from the inputstreams. This can be handy if you
     * want to exclude the the modules dependencies in a multi module project
     * 
     * @param excludeCorrespondingGavs
     *            exclude the dependencies corresponding to the given maven projects from the result
     * @param pomStreams Inputstreams to the pom files for which dependencies will be resolved
     * @return Set The identified dependencies
     * @throws DepencyResolvingException thrown when there was an issue obtaining the list of dependencies
     */
    public Set<Dependency> getDependencies( boolean excludeCorrespondingGavs, InputStream... pomStreams ) throws DepencyResolvingException;

    /**
     * Calculate and return the dependencies of the maven artifacts with the given GAV's (GroupId, ArtifactId, Version). The given
     * GAV's can be of maven projects that were previously stored or of maven projects that are known in the configured central
     * repository e.g. maven central Exclude from the result the GAVs that are given in gavsToExclude
     * 
     * @param gavs Stream of Groupid-Artifact-Versions for which the dependencies will be resolved
     * @param gavsToExclude
     *            Dependencies with GAV's listed in gavsToExclude will not be included in the result
     * @return Set The identified dependencies
     * @throws DepencyResolvingException thrown when there was an issue obtaining the list of dependencies
     */
    public Set<Dependency> getDependencies( Stream<GAV> gavs, Collection<GAV> gavsToExclude ) throws DepencyResolvingException;

}
