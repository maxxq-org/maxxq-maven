package org.maxxq.maven.dependency;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.maven.model.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maxxq.maven.repository.RepositoryException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith( MockitoExtension.class )
class ModelIOTest {
    private ModelIO      modelIO = new ModelIO();

    @Mock
    private OutputStream outputStream;

    @Mock
    private Model        model;

    @Test
    void getModelFromInputStream() {
        InputStream inputStream = getClass().getResourceAsStream( "/maven-dependencies.pom.xml" );

        Model model = modelIO.getModelFromInputStream( inputStream );

        assertNotNull( model );
        assertEquals( "chabernac", model.getGroupId() );
        assertEquals( "maven-dependencies", model.getArtifactId() );
        assertEquals( "0.1.0-SNAPSHOT", model.getVersion() );
    }

    @Test
    void getModelFromNullInputStream() {
        assertThrows( RepositoryException.class, () -> {
            modelIO.getModelFromInputStream( null );
        } );
    }

    @Test
    void getModelFromResource() {
        Model model = modelIO.getModelFromResource( "/maven-dependencies.pom.xml" );

        assertNotNull( model );
        assertEquals( "chabernac", model.getGroupId() );
        assertEquals( "maven-dependencies", model.getArtifactId() );
        assertEquals( "0.1.0-SNAPSHOT", model.getVersion() );
    }

    @Test
    void writeModelToStream() throws IOException {
        Model model = modelIO.getModelFromInputStream( getClass().getResourceAsStream( "/maven-dependencies.pom.xml" ) );
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        modelIO.writeModelToStream( model, out );

        assertEquals( 1369, out.toByteArray().length );
    }

    @Test
    void writeModelToNullStream() {
        assertThrows( RepositoryException.class, () -> {
            modelIO.writeModelToStream( model, null );
        } );
    }

    @Test
    void writeModelToString() throws IOException {
        Model model = modelIO.getModelFromInputStream( getClass().getResourceAsStream( "/maven-dependencies.pom.xml" ) );

        String result = modelIO.writeModelToString( model );

        assertEquals( 1369, result.length() );
    }

    @Test
    void writeNullModelToString() {
        assertThrows( RepositoryException.class, () -> {
            modelIO.writeModelToString( null );
        } );
    }

}
