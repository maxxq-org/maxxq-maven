package org.chabernac.dependency;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.maven.model.Model;
import org.chabernac.maven.repository.RepositoryException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class ModelIOTest {
    private ModelIO      modelIO = new ModelIO();

    @Mock
    private OutputStream outputStream;

    @Mock
    private Model        model;

    @Test
    public void getModelFromInputStream() {
        Model model = modelIO.getModelFromInputStream( getClass().getResourceAsStream( "/pom.xml" ) );

        Assert.assertNotNull( model );
        Assert.assertEquals( "chabernac", model.getGroupId() );
        Assert.assertEquals( "readdependencies", model.getArtifactId() );
        Assert.assertEquals( "0.0.1-SNAPSHOT", model.getVersion() );
    }

    @Test( expected = RepositoryException.class )
    public void getModelFromNullInputStream() {
        modelIO.getModelFromInputStream( null );
    }

    @Test
    public void getModelFromResource() {
        Model model = modelIO.getModelFromResource( "/pom.xml" );

        Assert.assertNotNull( model );
        Assert.assertEquals( "chabernac", model.getGroupId() );
        Assert.assertEquals( "readdependencies", model.getArtifactId() );
        Assert.assertEquals( "0.0.1-SNAPSHOT", model.getVersion() );
    }

    @Test
    public void writeModelToStream() throws IOException {
        Model model = modelIO.getModelFromInputStream( getClass().getResourceAsStream( "/pom.xml" ) );
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        modelIO.writeModelToStream( model, out );

        Assert.assertEquals( 1367, out.toByteArray().length );
    }

    @Test( expected = RepositoryException.class )
    public void writeModelToNullStream() throws IOException {
        modelIO.writeModelToStream( model, null );
    }

    @Test
    public void writeModelToString() throws IOException {
        Model model = modelIO.getModelFromInputStream( getClass().getResourceAsStream( "/pom.xml" ) );

        String result = modelIO.writeModelToString( model );

        Assert.assertEquals( 1367, result.length() );
    }

    @Test( expected = RepositoryException.class )
    public void writeNullModelToString() throws IOException {
        modelIO.writeModelToString( null );
    }

}
