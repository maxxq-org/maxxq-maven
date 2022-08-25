package org.javaa.maven.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.maven.model.Model;
import org.javaa.maven.dependency.GAV;
import org.javaa.maven.dependency.IModelIO;
import org.javaa.maven.repository.LocalFileRepository;
import org.javaa.maven.repository.RepositoryException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

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

    @Test( expected = RepositoryException.class )
    public void storeThrowsIOException() {
        localFileRepository = new LocalFileRepository( Paths.get( System.getProperty( "java.io.tmpdir" ), "pomcache" ), modelIO );
        Mockito.doThrow( IOException.class ).when( modelIO ).writeModelToStream( Mockito.any( Model.class ), Mockito.any( OutputStream.class ) );

        localFileRepository.store( createModel() );
    }

    @Test( expected = RepositoryException.class )
    public void readThrowsIOException() {
        Model model = createModel();
        localFileRepository.store( model );
        localFileRepository = new LocalFileRepository( Paths.get( System.getProperty( "java.io.tmpdir" ), "pomcache" ), modelIO );
        Mockito.doThrow( IOException.class ).when( modelIO ).getModelFromInputStream( Mockito.any( InputStream.class ) );

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

    private Model createModel() {
        Model model = new Model();
        model.setGroupId( "groupid" );
        model.setArtifactId( "artifactid" );
        model.setVersion( "version" );
        return model;
    }
}
