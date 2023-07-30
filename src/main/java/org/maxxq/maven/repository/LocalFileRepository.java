package org.maxxq.maven.repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.model.Model;
import org.maxxq.maven.dependency.GAV;
import org.maxxq.maven.dependency.GetMavenRepoURL;
import org.maxxq.maven.dependency.GetVersionsURL;
import org.maxxq.maven.dependency.IModelIO;
import org.maxxq.maven.dependency.ModelIO;
import org.maxxq.maven.model.MavenModel;

public class LocalFileRepository implements IRepository {
    private static final Logger                      LOGGER = LogManager.getLogger( LocalFileRepository.class );

    private final Function<GAV, String>              getMavenRepoPath;
    private final BiFunction<String, String, String> getMavenMetaRepoPath;
    private final IModelIO                           modelIO;

    public LocalFileRepository( Path basePath ) {
        this( basePath, new ModelIO() );
    }

    LocalFileRepository( Path basePath,
                         IModelIO modelIO ) {
        super();
        this.getMavenRepoPath = new GetMavenRepoURL( basePath.toString() );
        this.getMavenMetaRepoPath = new GetVersionsURL( basePath.toString() );
        this.modelIO = modelIO;
    }

    @Override
    public Optional<Model> readPom( GAV gav ) {
        Path path = Paths.get( getMavenRepoPath.apply( gav ) );
        try {
            if ( Files.exists( path ) ) {
                BasicFileAttributes attributes = Files.readAttributes( path, BasicFileAttributes.class );
                LOGGER.trace( "Reading pom from '{}'", path.toFile() );
                try (FileInputStream input = new FileInputStream( path.toFile() )) {
                    return Optional.of( new MavenModel( modelIO.getModelFromInputStream( input ), new Date( attributes.lastModifiedTime().toMillis() ) ) );
                }
            }

            return Optional.empty();
        } catch ( IOException e ) {
            throw new RepositoryException( "Could not read from path: '" + path + "'", e );
        }
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public GAV store( Model model ) {
        GAV gav = GAV.fromModel( model );
        Path path = Paths.get( getMavenRepoPath.apply( gav ) );
        try {
            Files.createDirectories( path.getParent() );
            LOGGER.trace( "Writing pom to '{}'", path.toFile() );
            try (FileOutputStream output = new FileOutputStream( path.toFile() )) {
                modelIO.writeModelToStream( model, output );
            }

            if ( model instanceof MavenModel ) {
                Files.setLastModifiedTime( path, FileTime.fromMillis( ( (MavenModel) model ).getCreationDate().getTime() ) );
            }

        } catch ( IOException e ) {
            throw new RepositoryException( "Could not write to '" + path + "'", e );
        }
        return gav;
    }

    @Override
    public Optional<Metadata> getMetaData( String groupId, String artifactId ) {
        Path path = Paths.get( getMavenMetaRepoPath.apply( groupId, artifactId ) );
        try {
            if ( Files.exists( path ) ) {
                LOGGER.trace( "Reading metadata from '{}'", path.toFile() );
                try (FileInputStream input = new FileInputStream( path.toFile() )) {
                    return Optional.of( modelIO.getMetaDataFromString( input ) );
                }
            }
        } catch ( IOException e ) {
            throw new RepositoryException( "Could not read/write to path: '" + path + "'", e );
        }
        return Optional.empty();
    }
}
