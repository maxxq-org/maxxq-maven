package org.maxxq.maven.repository;

import java.util.Optional;

import org.apache.maven.model.Model;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxxq.maven.dependency.GAV;
import org.maxxq.maven.repository.IRepository;
import org.maxxq.maven.repository.RepositoryException;
import org.maxxq.maven.repository.VirtualRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class VirtualRepositoryTest {
    private VirtualRepository virtualRepository = new VirtualRepository();

    @Mock
    private IRepository       repository1;

    @Mock
    private IRepository       repository2;

    @Mock
    private GAV               gav;

    @Mock
    private Model             model;

    @Before
    public void setUp() {
        virtualRepository.addRepository( repository1 );
        virtualRepository.addRepository( repository2 );
    }

    @Test
    public void readPomRepo1HasModel() {
        Mockito.when( repository1.readPom( gav ) ).thenReturn( Optional.of( model ) );
        Mockito.when( repository2.readPom( gav ) ).thenReturn( Optional.empty() );

        Optional<Model> result = virtualRepository.readPom( gav );

        Assert.assertTrue( result.isPresent() );
        Assert.assertSame( model, result.get() );
    }

    @Test
    public void readPomRepo2HasModel() {
        Mockito.when( repository1.readPom( gav ) ).thenReturn( Optional.empty() );
        Mockito.when( repository2.readPom( gav ) ).thenReturn( Optional.of( model ) );

        Optional<Model> result = virtualRepository.readPom( gav );

        Assert.assertTrue( result.isPresent() );
        Assert.assertSame( model, result.get() );
    }

    @Test
    public void readPomNoneHaveModel() {
        Mockito.when( repository1.readPom( gav ) ).thenReturn( Optional.empty() );
        Mockito.when( repository2.readPom( gav ) ).thenReturn( Optional.empty() );

        Optional<Model> result = virtualRepository.readPom( gav );

        Assert.assertFalse( result.isPresent() );
    }

    @Test
    public void isWritableNotWritable() {
        Mockito.when( repository1.isWritable() ).thenReturn( Boolean.FALSE );
        Mockito.when( repository2.isWritable() ).thenReturn( Boolean.FALSE );

        Assert.assertFalse( virtualRepository.isWritable() );
    }

    @Test
    public void isWritableRepo1() {
        Mockito.when( repository1.isWritable() ).thenReturn( Boolean.TRUE );
        Mockito.when( repository2.isWritable() ).thenReturn( Boolean.FALSE );

        Assert.assertTrue( virtualRepository.isWritable() );
    }

    @Test
    public void isWritableRepo2() {
        Mockito.when( repository1.isWritable() ).thenReturn( Boolean.FALSE );
        Mockito.when( repository2.isWritable() ).thenReturn( Boolean.TRUE );

        Assert.assertTrue( virtualRepository.isWritable() );
    }

    @Test
    public void isWritableRepo1and2() {
        Mockito.when( repository1.isWritable() ).thenReturn( Boolean.TRUE );
        Mockito.when( repository2.isWritable() ).thenReturn( Boolean.TRUE );

        Assert.assertTrue( virtualRepository.isWritable() );
    }

    @Test
    public void storeRepo2isWritable() {
        Mockito.when( repository1.isWritable() ).thenReturn( Boolean.FALSE );
        Mockito.when( repository2.isWritable() ).thenReturn( Boolean.TRUE );
        Mockito.when( repository2.store( model ) ).thenReturn( gav );

        GAV result = virtualRepository.store( model );

        Assert.assertSame( gav, result );
        Mockito.verify( repository1, Mockito.times( 0 ) ).store( model );
        Mockito.verify( repository2, Mockito.times( 1 ) ).store( model );
    }

    @Test( expected = RepositoryException.class )
    public void storeNoRepoIsWritable() {
        Mockito.when( repository1.isWritable() ).thenReturn( Boolean.FALSE );
        Mockito.when( repository2.isWritable() ).thenReturn( Boolean.FALSE );

        virtualRepository.store( model );
    }
}
