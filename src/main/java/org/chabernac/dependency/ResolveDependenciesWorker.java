package org.chabernac.dependency;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.chabernac.maven.repository.IRepository;

public class ResolveDependenciesWorker implements Supplier<Set<Dependency>> {
    private static final Logger       LOGGER             = LogManager.getLogger( ResolveDependenciesWorker.class );
    private final Model               project;
    private final IRepository         repository;
    private final IPOMUtils           pomUtils           = new POMUtils();

    private Map<GAV, Set<Dependency>> cachedDependencies = new HashMap<>();

    public ResolveDependenciesWorker( Model project,
                                      IRepository repository ) {
        super();
        if ( project == null ) {
            throw new IllegalArgumentException( "input project must not be null" );
        }
        this.project = project;
        this.repository = repository;
    }

    @Override
    public Set<Dependency> get() {
        return processPomStream( project, null, true );
    }

    private Set<Dependency> processPomStream( Model model, List<Exclusion> exclusions, boolean inclusiveTestScope ) {
        GAV gav = GAV.fromModel( model );
        if ( cachedDependencies.containsKey( gav ) ) {
            LOGGER.debug( "Returning cached dependencies for: " + gav );
            return cachedDependencies.get( gav );
        }

        try {
            Set<Dependency> dependencies = new HashSet<>();
            cachedDependencies.put( gav, dependencies );
            LOGGER.debug( "Retrieving dependencies for: {}", gav );
            getPropertiesAndDependencyManagementFromParents( model );
            resolveVersionsFromDependencyManagement( model );
            followDependencyManagementImports( model );
            addDependenciesWithValidScopeToList( dependencies, model, exclusions, inclusiveTestScope );
            addTransitiveDependencies( dependencies, model, exclusions, inclusiveTestScope );
            return dependencies;
        } catch ( Exception e ) {
            throw new DepencyResolvingException( "Could not resolve dependencies", e );
        }
    }

    private void addDependenciesWithValidScopeToList( Set<Dependency> dependencies, Model model, List<Exclusion> exclusions, boolean inclusiveTestScope ) {
        model.getDependencies()
            .stream()
            .filter( dependency -> isValidScope( dependency.getScope(), inclusiveTestScope ) )
            .forEach( dependency -> dependencies.add( dependency ) );
    }

    private void addTransitiveDependencies( Set<Dependency> dependencies, Model model, List<Exclusion> exclusions, boolean inclusiveTestScope ) {
        model.getDependencies()
            .stream()
            .filter( dependency -> isValidScope( dependency.getScope(), inclusiveTestScope ) )
            .filter( dependency -> !isExcluded( dependency, exclusions ) )
            .forEach( dependency -> dependencies.addAll( filterDependenciesAlreadyAdded( getTransitiveDependencies( dependency ), dependencies ) ) );
    }

    private void followDependencyManagementImports( Model model ) {

    }

    private List<Dependency> filterDependenciesAlreadyAdded( Set<Dependency> transitiveDependencies, Set<Dependency> dependencies ) {
        return transitiveDependencies
            .stream()
            .filter( dependency -> !dependencyExist( dependency, dependencies ) )
            .collect( Collectors.toList() );
    }

    private boolean dependencyExist( Dependency dependency, Set<Dependency> dependencies ) {
        return dependencies.stream()
            .filter( dep -> dep.getGroupId().equals( dependency.getGroupId() ) )
            .anyMatch( dep -> dep.getArtifactId().equals( dependency.getArtifactId() ) );
    }

    private boolean isExcluded( Dependency dependency, List<Exclusion> exclusions ) {
        if ( exclusions == null || exclusions.isEmpty() ) {
            return false;
        }
        boolean excluded = exclusions.stream()
            .filter( exclusion -> exclusion.getGroupId().equals( dependency.getGroupId() ) )
            .anyMatch( exclusion -> exclusion.getArtifactId().equals( dependency.getArtifactId() ) );
        return excluded;
    }

    private boolean isValidScope( String scope, boolean includeTestScope ) {
        if ( StringUtils.isEmpty( scope ) ) {
            return true;
        }
        if ( "compile".equals( scope ) ) {
            return true;
        }
        if ( "test".equals( scope ) && includeTestScope ) {
            return true;
        }
        return false;
    }

    private Set<Dependency> getTransitiveDependencies( Dependency dependency ) {
        LOGGER.trace( "Following transitive dependencies of: {}", GAV.fromDependency( dependency ) );
        return repository.readPom( GAV.fromDependency( dependency ) )
            .map( model -> processPomStream( model, dependency.getExclusions(), false ) )
            .orElse( new HashSet<>() );
    }

    private void resolveVersionsFromDependencyManagement( Model model ) {
        // model.getDependencyManagement()
        // .getDependencies()
        // .stream()
        // .forEach(dependency -> LOGGER.debug("managed dependency: " + GAV.fromDependency(dependency) + "
        // scope=" + dependency.getScope()));
        // LOGGER.debug("properties: " + model.getProperties());

        model.getDependencies()
            .stream()
            .filter( dependency -> StringUtils.isEmpty( dependency.getVersion() ) || pomUtils.isPropertyValue( dependency.getVersion() ) )
            .forEach( dependency -> resolveVersion( dependency, model ) );

        model.getDependencies()
            .stream()
            .forEach(
                dependency -> LOGGER
                    .debug( "dependency: " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + " scope:" + dependency.getScope() ) );
    }

    private void resolveVersion( Dependency resolveVersion, Model model ) {
        copyVersionFromDependencyManagement( resolveVersion, model );

        if ( StringUtils.isEmpty( resolveVersion.getVersion() ) ) {
            LOGGER.error(
                "After copying the versions from the dependency management the version for " + resolveVersion.getGroupId() + ":" + resolveVersion.getArtifactId() + " in pom " +
                          GAV.fromModel( model ) + " is still empty, model: " + new ModelIO().writeModelToString( model ) );
            throw new IllegalArgumentException( "After copying the versions from the dependency management the version for " + resolveVersion.getGroupId() + ":" +
                                                resolveVersion.getArtifactId() + " in pom " + GAV.fromModel( model ) + " is still empty" );
        }

        resolveVersion.setVersion( pomUtils.resolveProperty( resolveVersion.getVersion(), model ) );
    }

    private void copyVersionFromDependencyManagement( Dependency resolveVersion, Model model ) {
        model.getDependencyManagement()
            .getDependencies()
            .stream()
            .filter( dependency -> dependency.getGroupId().equals( resolveVersion.getGroupId() ) )
            .filter( dependency -> dependency.getArtifactId().equals( resolveVersion.getArtifactId() ) )
            .findFirst()
            .ifPresent( dependency -> resolveVersion.setVersion( dependency.getVersion() ) );
    }

    private void getPropertiesAndDependencyManagementFromParents( Model model ) {
        if ( model.getDependencyManagement() == null ) {
            model.setDependencyManagement( new DependencyManagement() );
        }

        Parent parent = model.getParent();
        while ( parent != null ) {
            LOGGER.debug( "Reading from parent with gav: " + GAV.fromParent( parent ) );
            Optional<Model> modelForParent = repository.readPom( GAV.fromParent( parent ) );
            if ( !modelForParent.isPresent() ) {
                return;
            }
            Model parentModel = modelForParent.get();
            if ( parentModel.getDependencyManagement() != null &&
                 parentModel.getDependencyManagement().getDependencies() != null ) {
                parentModel.getDependencyManagement()
                    .getDependencies()
                    .stream()
                    .map( dependency -> resolveProperties( dependency, parentModel ) )
                    .forEach( dependency -> model.getDependencyManagement().addDependency( dependency ) );
            }
            if ( parentModel.getDependencies() != null ) {
                parentModel.getDependencies().forEach( dependency -> model.addDependency( dependency ) );
            }
            if ( model.getProperties() != null ) {
                model.getProperties().putAll( parentModel.getProperties() );
            }
            parent = parentModel.getParent();
        }
    }

    private Dependency resolveProperties( Dependency dependency, Model parentModel ) {
        if ( pomUtils.isPropertyValue( dependency.getGroupId() ) ) {
            dependency.setGroupId( pomUtils.resolveProperty( dependency.getGroupId(), parentModel ) );
        }
        if ( pomUtils.isPropertyValue( dependency.getArtifactId() ) ) {
            dependency.setArtifactId( pomUtils.resolveProperty( dependency.getArtifactId(), parentModel ) );
        }
        if ( pomUtils.isPropertyValue( dependency.getVersion() ) ) {
            dependency.setVersion( pomUtils.resolveProperty( dependency.getVersion(), parentModel ) );
        }
        return dependency;
    }

}
