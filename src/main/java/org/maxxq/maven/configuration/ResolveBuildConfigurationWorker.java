package org.maxxq.maven.configuration;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
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
            copyNonExistingProperties( parentModel, project );
            parent = parentModel.getParent();
        }
        resolveProperties();
    }

    private void resolveProperties() {
        project.getBuild()
            .getPlugins()
            .stream()
            .forEach( plugin -> resolveProperties( plugin ) );
    }

    private void resolveProperties( Plugin plugin ) {
        int times = 0;
        while ( hasPropertyValue( plugin ) ) {
            resolveGAV( plugin, project );
            if ( times++ == 10 ) {
                return;
            }
        }
    }

    private void resolveGAV( Plugin plugin, Model parentModel ) {
        if ( pomUtils.isPropertyValue( plugin.getGroupId() ) ) {
            plugin.setGroupId( pomUtils.resolveProperty( plugin.getGroupId(), parentModel ) );
        }
        if ( pomUtils.isPropertyValue( plugin.getArtifactId() ) ) {
            plugin.setArtifactId( pomUtils.resolveProperty( plugin.getArtifactId(), parentModel ) );
        }
        if ( pomUtils.isPropertyValue( plugin.getVersion() ) ) {
            plugin.setVersion( pomUtils.resolveProperty( plugin.getVersion(), parentModel ) );
        }
        if ( pomUtils.isPropertyValue( plugin.getConfiguration().toString() ) ) {
            try {
                plugin.setConfiguration( Xpp3DomBuilder.build( new StringReader( pomUtils.resolveProperty( plugin.getConfiguration().toString(), parentModel ) ) ) );
            } catch ( XmlPullParserException | IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private boolean hasPropertyValue( Plugin plugin ) {
        return pomUtils.isPropertyValue( plugin.getGroupId() ) ||
               pomUtils.isPropertyValue( plugin.getArtifactId() ) ||
               pomUtils.isPropertyValue( plugin.getVersion() );
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
        parentModel.getBuild()
            .getPlugins()
            .stream()
            .forEach( plugin -> copyProperties( plugin, getPlugin( model, plugin ) ) );
    }

    private void copyProperties( Plugin fromPlugin, Plugin toPlugin ) {
        toPlugin.setConfiguration( Xpp3DomUtils.mergeXpp3Dom( (Xpp3Dom) toPlugin.getConfiguration(), (Xpp3Dom) fromPlugin.getConfiguration() ) );
    }

    private Plugin getPlugin( Model model, Plugin plugin ) {
        return model.getBuild()
            .getPlugins()
            .stream()
            .filter( p -> p.getGroupId().equals( plugin.getGroupId() ) )
            .filter( p -> p.getArtifactId().equals( plugin.getArtifactId() ) )
            .findFirst()
            .get();
    }

    private void copyNonExistingBuildPlugins( Model parentModel, Model model ) {
        parentModel.getBuild()
            .getPlugins()
            .stream()
            .filter( plugin -> !modelHasPlugin( model, plugin ) )
            .forEach( plugin -> model.getBuild().addPlugin( plugin ) );

    }

    private boolean modelHasPlugin( Model model, Plugin plugin ) {
        return model.getBuild()
            .getPlugins()
            .stream()
            .filter( p -> p.getGroupId().equals( plugin.getGroupId() ) )
            .anyMatch( p -> p.getArtifactId().equals( plugin.getArtifactId() ) );
    }

}
