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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxxq.maven.dependency.GAV;
import org.maxxq.maven.dependency.IModelIO;
import org.maxxq.maven.model.MavenModel;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class LocalFileRepositoryTest {
    private LocalFileRepository localFileRepository;

    @Mock
    private IModelIO            modelIO;

    @Before
    public void setUp() {
        localFileRepository = new LocalFileRepository( Paths.get( System.getProperty( "java.io.tmpdir" ), "pomcache" ) );
    }

    @Test
    public void storeRead() {
        Model model = createModel();

        GAV gav = localFileRepository.store( model );
        Optional<Model> result = localFileRepository.readPom( gav );

        Assert.assertTrue( result.isPresent() );
        Assert.assertEquals( "groupid", result.get().getGroupId() );
        Assert.assertEquals( "artifactid", result.get().getArtifactId() );
        Assert.assertEquals( "version", result.get().getVersion() );
    }

    @Test
    public void storeReadMavenModel() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat( "dd/MM/yyyy" );
        Date aDateInThePast = format.parse( "22/11/2021" );
        Model mavenModel = new MavenModel( createModel(), aDateInThePast );

        GAV gav = localFileRepository.store( mavenModel );
        Optional<Model> result = localFileRepository.readPom( gav );

        Assert.assertTrue( result.isPresent() );
        Assert.assertEquals( "groupid", result.get().getGroupId() );
        Assert.assertEquals( "artifactid", result.get().getArtifactId() );
        Assert.assertEquals( "version", result.get().getVersion() );
        Assert.assertTrue( result.get() instanceof MavenModel );
        Assert.assertEquals( "22/11/2021", format.format( ( (MavenModel) result.get() ).getCreationDate() ) );
    }

    @Test( expected = RepositoryException.class )
    public void storeThrowsIOException() {
        localFileRepository = new LocalFileRepository( Paths.get( System.getProperty( "java.io.tmpdir" ), "pomcache" ), modelIO );
        Mockito.doThrow( RuntimeException.class ).when( modelIO ).writeModelToStream( Mockito.any( Model.class ), Mockito.any( OutputStream.class ) );

        localFileRepository.store( createModel() );
    }

    @Test( expected = RepositoryException.class )
    public void readThrowsIOException() {
        Model model = createModel();
        localFileRepository.store( model );
        localFileRepository = new LocalFileRepository( Paths.get( System.getProperty( "java.io.tmpdir" ), "pomcache" ), modelIO );
        Mockito.doThrow( RuntimeException.class ).when( modelIO ).getModelFromInputStream( Mockito.any( InputStream.class ) );

        localFileRepository.readPom( GAV.fromModel( model ) );
    }

    @Test
    public void readNotExisting() {
        Optional<Model> result = localFileRepository.readPom( new GAV( "group", "artifact", "notexisting" ) );

        Assert.assertFalse( result.isPresent() );
    }

    @Test
    public void isWritable() {
        Assert.assertTrue( localFileRepository.isWritable() );
    }

    @Test
    public void getRealMetaData() {
        localFileRepository = new LocalFileRepository( Paths.get( "src/test/resources/pomcache" ) );

        Optional<Metadata> result = localFileRepository.getMetaData( "org.apache.maven", "maven-model" );

        Assert.assertTrue( result.isPresent() );
    }

    @Test
    public void getMetaDataDoesNotExists() {
        localFileRepository = new LocalFileRepository( Paths.get( "src/test/resources/pomcache" ) );

        Optional<Metadata> result = localFileRepository.getMetaData( "groupid", "artifactid" );

        Assert.assertFalse( result.isPresent() );
    }

    private Model createModel() {
        Model model = new Model();
        model.setGroupId( "groupid" );
        model.setArtifactId( "artifactid" );
        model.setVersion( "version" );
        return model;
    }
}
