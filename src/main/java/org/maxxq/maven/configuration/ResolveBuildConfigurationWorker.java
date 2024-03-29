package org.maxxq.maven.configuration;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.maxxq.maven.dependency.GAV;
import org.maxxq.maven.dependency.POMUtils;
import org.maxxq.maven.repository.IRepository;

public class ResolveBuildConfigurationWorker implements Runnable {
    private static final Logger LOGGER   = LogManager.getLogger( ResolveBuildConfigurationWorker.class );
    private final Model         project;
    private final IRepository   repository;
    private POMUtils            pomUtils = new POMUtils();

    public ResolveBuildConfigurationWorker( Model project,
                                            IRepository repository ) {
        super();
        if ( project == null ) {
            throw new IllegalArgumentException( "input project must not be null" );
        }
        this.project = project;
        this.repository = repository;
    }

    @Override
    public void run() {
        Parent parent = project.getParent();
        while ( parent != null ) {
            LOGGER.debug( "Reading from parent with gav: {}", GAV.fromParent( parent ) );
            Optional<Model> modelForParent = repository.readPom( GAV.fromParent( parent ) );
            if ( !modelForParent.isPresent() ) {
                LOGGER.warn( "Parent model with gav {} could not be loaded", GAV.fromParent( parent ) );
                return;
            }
            Model parentModel = modelForParent.get();
            copyNonExistingBuildPlugins( parentModel, project );
            copyConfigurationOfExistingPlugins( parentModel, project );
            copyNonExistingBuildManagementPlugins( parentModel, project );
            copyConfigurationOfExistingBuildManagementPlugins( parentModel, project );
            copyNonExistingProperties( parentModel, project );
            parent = parentModel.getParent();
        }

        applyDependencyManagement( project );
        resolveProperties();
        resolvePropertiesForBuildPlugins();
    }

    private void resolveProperties() {
        project.getProperties().entrySet().stream().forEach( propertyEntry -> resolveProperties( propertyEntry ) );
    }

    private void resolveProperties( Entry<Object, Object> propertyEntry ) {
        if ( pomUtils.isPropertyValue( propertyEntry.getValue().toString() ) ) {
            propertyEntry.setValue( pomUtils.resolveProperty( propertyEntry.getValue().toString(), project ) );
        }
    }

    private void resolvePropertiesForBuildPlugins() {
        if ( project.getBuild() == null || project.getBuild().getPlugins() == null ) {
            return;
        }

        project.getBuild()
            .getPlugins()
            .stream()
            .forEach( plugin -> resolveProperties( plugin ) );
    }

    private void resolveProperties( Plugin plugin ) {
        int times = 0;
        while ( hasPropertyValue( plugin ) ) {
            resolvePropertiesInPlugin( plugin, project );
            if ( times++ == 10 ) {
                return;
            }
        }
    }

    private void resolvePropertiesInPlugin( Plugin plugin, Model model ) {
        if ( pomUtils.isPropertyValue( plugin.getGroupId() ) ) {
            plugin.setGroupId( pomUtils.resolveProperty( plugin.getGroupId(), model ) );
        }
        if ( pomUtils.isPropertyValue( plugin.getArtifactId() ) ) {
            plugin.setArtifactId( pomUtils.resolveProperty( plugin.getArtifactId(), model ) );
        }
        if ( pomUtils.isPropertyValue( plugin.getVersion() ) ) {
            plugin.setVersion( pomUtils.resolveProperty( plugin.getVersion(), model ) );
        }
        if ( plugin.getConfiguration() != null && pomUtils.isPropertyValue( plugin.getConfiguration().toString() ) ) {
            try {
                plugin.setConfiguration( Xpp3DomBuilder.build( new StringReader( pomUtils.resolveProperty( plugin.getConfiguration().toString(), model ) ) ) );
            } catch ( XmlPullParserException | IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private boolean hasPropertyValue( Plugin plugin ) {
        return pomUtils.isPropertyValue( plugin.getGroupId() ) ||
               pomUtils.isPropertyValue( plugin.getArtifactId() ) ||
               pomUtils.isPropertyValue( plugin.getVersion() ) ||
               ( plugin.getConfiguration() != null && pomUtils.isPropertyValue( plugin.getConfiguration().toString() ) );
    }

    private void copyNonExistingProperties( Model parentModel, Model model ) {
        if ( model.getProperties() != null ) {
            parentModel.getProperties()
                .entrySet()
                .stream()
                .filter( entry -> !model.getProperties().containsKey( entry.getKey() ) )
                .forEach( entry -> model.getProperties().put( entry.getKey(), entry.getValue() ) );
        }
    }

    private void copyConfigurationOfExistingPlugins( Model parentModel, Model model ) {
        if ( parentModel.getBuild() == null || parentModel.getBuild().getPlugins() == null ) {
            return;
        }

        parentModel.getBuild()
            .getPlugins()
            .stream()
            .forEach( plugin -> copyProperties( plugin, getPlugin( model, plugin ) ) );
    }

    private void copyConfigurationOfExistingBuildManagementPlugins( Model parentModel, Model model ) {
        if ( parentModel.getBuild() == null || parentModel.getBuild().getPluginManagement() == null || parentModel.getBuild().getPluginManagement().getPlugins() == null ) {
            return;
        }

        parentModel.getBuild()
            .getPluginManagement()
            .getPlugins()
            .stream()
            .forEach( plugin -> copyProperties( plugin, getBuildManagementPlugin( model, plugin ) ) );
    }

    private void copyProperties( Plugin fromPlugin, Optional<Plugin> toPlugin ) {
        toPlugin.ifPresent( plugin -> plugin.setConfiguration( Xpp3DomUtils.mergeXpp3Dom( (Xpp3Dom) plugin.getConfiguration(), (Xpp3Dom) fromPlugin.getConfiguration() ) ) );
    }

    private Optional<Plugin> getPlugin( Model model, Plugin plugin ) {
        if ( model.getBuild() == null || model.getBuild().getPlugins() == null ) {
            return Optional.empty();
        }

        return model.getBuild()
            .getPlugins()
            .stream()
            .filter( p -> p.getGroupId().equals( plugin.getGroupId() ) )
            .filter( p -> p.getArtifactId().equals( plugin.getArtifactId() ) )
            .findFirst();
    }

    private Optional<Plugin> getBuildManagementPlugin( Model model, Plugin plugin ) {
        if ( model.getBuild() == null || model.getBuild().getPluginManagement() == null || model.getBuild().getPluginManagement().getPlugins() == null ) {
            return Optional.empty();
        }

        return model.getBuild()
            .getPluginManagement()
            .getPlugins()
            .stream()
            .filter( p -> p.getGroupId().equals( plugin.getGroupId() ) )
            .filter( p -> p.getArtifactId().equals( plugin.getArtifactId() ) )
            .findFirst();
    }

    private void copyNonExistingBuildPlugins( Model parentModel, Model model ) {
        if ( parentModel.getBuild() == null || parentModel.getBuild().getPlugins() == null ) {
            return;
        }

        parentModel.getBuild()
            .getPlugins()
            .stream()
            .filter( plugin -> !modelHasPlugin( model, plugin ) )
            .forEach( plugin -> addPluginToModel( model, plugin ) );
    }

    private void addPluginToModel( Model model, Plugin plugin ) {
        if ( model.getBuild() == null ) {
            model.setBuild( new Build() );
        }
        model.getBuild().addPlugin( plugin );
    }

    private void addManagedPluginToModel( Model model, Plugin plugin ) {
        if ( model.getBuild() == null ) {
            model.setBuild( new Build() );
        }
        if ( model.getBuild().getPluginManagement() == null ) {
            model.getBuild().setPluginManagement( new PluginManagement() );

        }
        model.getBuild().getPluginManagement().addPlugin( plugin );
    }

    private void copyNonExistingBuildManagementPlugins( Model parentModel, Model model ) {
        if ( parentModel.getBuild() == null || parentModel.getBuild().getPluginManagement() == null || parentModel.getBuild().getPluginManagement().getPlugins() == null ) {
            return;
        }

        parentModel.getBuild()
            .getPluginManagement()
            .getPlugins()
            .stream()
            .filter( plugin -> !modelHasBuildManagementPlugin( model, plugin ) )
            .forEach( plugin -> addManagedPluginToModel( model, plugin ) );

    }

    private boolean modelHasPlugin( Model model, Plugin plugin ) {
        if ( model.getBuild() == null || model.getBuild().getPlugins() == null ) {
            return false;
        }

        return model.getBuild()
            .getPlugins()
            .stream()
            .filter( p -> p.getGroupId().equals( plugin.getGroupId() ) )
            .anyMatch( p -> p.getArtifactId().equals( plugin.getArtifactId() ) );
    }

    private boolean modelHasBuildManagementPlugin( Model model, Plugin plugin ) {
        if ( model.getBuild() == null || model.getBuild().getPluginManagement() == null || model.getBuild().getPluginManagement().getPlugins() == null ) {
            return false;
        }

        return model.getBuild()
            .getPluginManagement()
            .getPlugins()
            .stream()
            .filter( p -> p.getGroupId().equals( plugin.getGroupId() ) )
            .anyMatch( p -> p.getArtifactId().equals( plugin.getArtifactId() ) );
    }

    private void applyDependencyManagement( Model model ) {
        if ( model.getBuild() == null || model.getBuild().getPluginManagement() == null || model.getBuild().getPluginManagement().getPlugins() == null ) {
            return;
        }

        model.getBuild()
            .getPlugins()
            .stream()
            .forEach( plugin -> applyDependencyManagement( plugin, model ) );

    }

    private void applyDependencyManagement( Plugin plugin, Model model ) {
        if ( model.getBuild() == null || model.getBuild().getPluginManagement() == null || model.getBuild().getPluginManagement().getPlugins() == null ) {
            return;
        }

        model.getBuild()
            .getPluginManagement()
            .getPlugins()
            .stream()
            .filter( managedPlugin -> plugin.getArtifactId().equals( managedPlugin.getArtifactId() ) )
            .filter( managedPlugin -> plugin.getGroupId().equals( managedPlugin.getGroupId() ) )
            .findFirst()
            .ifPresent( managedPlugin -> plugin.setVersion( managedPlugin.getVersion() ) );
    }
}
