package org.maxxq.maven.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.model.Model;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxxq.maven.dependency.GAV;
import org.maxxq.maven.dependency.IModelIO;
import org.maxxq.maven.model.MavenModel;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class FileCachingRepositoryTest {
    private FileCachingRepository repo;

    @Mock
    private IRepository           repository;

    @Mock
    private IModelIO              modelIO;

    private Path                  tempDir;

    @Mock
    private Model                 model;

    @Mock
    private Metadata              metadata;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory( "pomcache" );
        repo = new FileCachingRepository( repository, tempDir, modelIO );
    }

    @After
    public void tearDown() throws IOException {
        try (Stream<Path> walk = Files.walk( tempDir )) {
            walk.sorted( Comparator.reverseOrder() )
                .map( Path::toFile )
                .peek( System.out::println )
                .forEach( File::delete );
        }
    }

    @Test
    public void readPom() throws ParseException {
        Model model = new Model();
        model.setGroupId( "groupid" );
        model.setArtifactId( "artifactid" );
        model.setVersion( "version" );
        SimpleDateFormat format = new SimpleDateFormat( "dd/MM/yyyy" );
        Date aDateInThePast = format.parse( "22/11/2021" );
        Model mavenModel = new MavenModel( model, aDateInThePast );

        GAV gav = GAV.fromModel( model );
        Mockito.when( repository.readPom( gav ) ).thenReturn( Optional.of( mavenModel ) );
        Mockito.when( modelIO.getModelFromInputStream( Mockito.any( InputStream.class ) ) ).thenReturn( model );

        Optional<Model> result = repo.readPom( gav );
        result = repo.readPom( gav );

        Assert.assertTrue( result.isPresent() );
        Assert.assertEquals( GAV.fromModel( model ), GAV.fromModel( result.get() ) );
        Assert.assertTrue( result.get() instanceof MavenModel );
        Assert.assertEquals( "22/11/2021", format.format( ( (MavenModel) result.get() ).getCreationDate() ) );
        Mockito.verify( repository, Mockito.times( 1 ) ).readPom( gav );
        ArgumentCaptor<Model> writtenModelCaptor = ArgumentCaptor.forClass( Model.class );
        Mockito.verify( modelIO, Mockito.times( 1 ) ).writeModelToStream( writtenModelCaptor.capture(), Mockito.any( OutputStream.class ) );
        Model writtenModel = writtenModelCaptor.getValue();
        Assert.assertTrue( writtenModel instanceof MavenModel );
        Assert.assertSame( model, ( (MavenModel) writtenModel ).getModel() );
    }

    @Test
    public void readPomNotPresent() {
        Model model = new Model();
        model.setGroupId( "groupid" );
        model.setArtifactId( "artifactid" );
        model.setVersion( "version" );
        GAV gav = GAV.fromModel( model );
        Mockito.when( repository.readPom( gav ) ).thenReturn( Optional.empty() );

        Optional<Model> result = repo.readPom( gav );

        Assert.assertFalse( result.isPresent() );
        Mockito.verify( repository, Mockito.times( 1 ) ).readPom( gav );
    }

    @Test
    public void getMetaData() {
        Mockito.when( repository.getMetaData( "groupid", "artifactid" ) ).thenReturn( Optional.of( metadata ) );
        Mockito.when( modelIO.getMetaDataFromString( Mockito.any( InputStream.class ) ) ).thenReturn( metadata );

        Optional<Metadata> result = repo.getMetaData( "groupid", "artifactid" );
        result = repo.getMetaData( "groupid", "artifactid" );

        Assert.assertTrue( result.isPresent() );
        Assert.assertEquals( metadata, result.get() );
        Mockito.verify( repository, Mockito.times( 1 ) ).getMetaData( "groupid", "artifactid" );
        Mockito.verify( modelIO, Mockito.times( 1 ) ).writeMetadataToStream( Mockito.same( metadata ), Mockito.any( OutputStream.class ) );
    }

    @Test
    public void getMetaDataNotPresent() {
        Mockito.when( repository.getMetaData( "groupid", "artifactid" ) ).thenReturn( Optional.empty() );

        Optional<Metadata> result = repo.getMetaData( "groupid", "artifactid" );

        Assert.assertFalse( result.isPresent() );
        Mockito.verify( repository, Mockito.times( 1 ) ).getMetaData( "groupid", "artifactid" );
    }

    @Test
    public void isWritable() {
        Assert.assertFalse( repo.isWritable() );
    }

    @Test( expected = UnsupportedOperationException.class )
    public void store() {
        repo.store( model );
    }
}
