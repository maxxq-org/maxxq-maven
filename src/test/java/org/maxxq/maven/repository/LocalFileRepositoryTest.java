package org.maxxq.maven.repository;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maxxq.maven.dependency.GAV;
import org.maxxq.maven.dependency.IModelIO;
import org.maxxq.maven.model.MavenModel;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class LocalFileRepositoryTest {
    private LocalFileRepository localFileRepository;

    @Mock
    private IModelIO            modelIO;

    @BeforeEach
    void setUp() {
        localFileRepository = new LocalFileRepository( Paths.get( System.getProperty( "java.io.tmpdir" ), "pomcache" ) );
    }

    @Test
    void storeRead() {
        Model model = createModel();

        GAV gav = localFileRepository.store( model );
        Optional<Model> result = localFileRepository.readPom( gav );

        assertTrue( result.isPresent() );
        assertEquals( "groupid", result.get().getGroupId() );
        assertEquals( "artifactid", result.get().getArtifactId() );
        assertEquals( "version", result.get().getVersion() );
    }

    @Test
    void storeReadMavenModel() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat( "dd/MM/yyyy" );
        Date aDateInThePast = format.parse( "22/11/2021" );
        Model mavenModel = new MavenModel( createModel(), aDateInThePast );

        GAV gav = localFileRepository.store( mavenModel );
        Optional<Model> result = localFileRepository.readPom( gav );

        assertTrue( result.isPresent() );
        assertEquals( "groupid", result.get().getGroupId() );
        assertEquals( "artifactid", result.get().getArtifactId() );
        assertEquals( "version", result.get().getVersion() );
        assertTrue( result.get() instanceof MavenModel );
        assertEquals( "22/11/2021", format.format( ( (MavenModel) result.get() ).getCreationDate() ) );
    }

    @Test
    void storeThrowsIOException() {
        assertThrows( RepositoryException.class, () -> {
            localFileRepository = new LocalFileRepository( Paths.get( System.getProperty( "java.io.tmpdir" ), "pomcache" ), modelIO );
            Mockito.doThrow( RuntimeException.class ).when( modelIO ).writeModelToStream( Mockito.any( Model.class ), Mockito.any( OutputStream.class ) );

            localFileRepository.store( createModel() );
        } );
    }

    @Test
    void readThrowsIOException() {
        assertThrows( RepositoryException.class, () -> {
            Model model = createModel();
            localFileRepository.store( model );
            localFileRepository = new LocalFileRepository( Paths.get( System.getProperty( "java.io.tmpdir" ), "pomcache" ), modelIO );
            Mockito.doThrow( RuntimeException.class ).when( modelIO ).getModelFromInputStream( Mockito.any( InputStream.class ) );

            localFileRepository.readPom( GAV.fromModel( model ) );
        } );
    }

    @Test
    void readNotExisting() {
        Optional<Model> result = localFileRepository.readPom( new GAV( "group", "artifact", "notexisting" ) );

        assertFalse( result.isPresent() );
    }

    @Test
    void isWritable() {
        assertTrue( localFileRepository.isWritable() );
    }

    @Test
    void getRealMetaData() {
        localFileRepository = new LocalFileRepository( Paths.get( "src/test/resources/pomcache" ) );

        Optional<Metadata> result = localFileRepository.getMetaData( "org.apache.maven", "maven-model" );

        assertTrue( result.isPresent() );
    }

    @Test
    void getMetaDataDoesNotExists() {
        localFileRepository = new LocalFileRepository( Paths.get( "src/test/resources/pomcache" ) );

        Optional<Metadata> result = localFileRepository.getMetaData( "groupid", "artifactid" );

        assertFalse( result.isPresent() );
    }

    private Model createModel() {
        Model model = new Model();
        model.setGroupId( "groupid" );
        model.setArtifactId( "artifactid" );
        model.setVersion( "version" );
        return model;
    }
}
