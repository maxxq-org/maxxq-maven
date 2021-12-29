package org.chabernac.maven.repository;

import java.util.Optional;
import org.apache.maven.model.Model;
import org.chabernac.dependency.GAV;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class InMemoryCachingRepositoryTest {
    private InMemoryCachingRepository repository;

    @Mock
    private IRepository               repo;

    @Mock
    private GAV                       gav;

    @Mock
    private Model                     model;

    @Before
    public void setUp() {
        repository = new InMemoryCachingRepository( repo );
    }

    @Test
    public void readPom() {
        Mockito.when( repo.readPom( gav ) ).thenReturn( Optional.of( model ) );

        Optional<Model> result = repository.readPom( gav );
        result = repository.readPom( gav );

        Assert.assertTrue( result.isPresent() );
        Assert.assertSame( model, result.get() );
        Mockito.verify( repo, Mockito.times( 1 ) ).readPom( gav );
    }

    @Test
    public void readPomNull() {
        Mockito.when( repo.readPom( gav ) ).thenReturn( Optional.empty() );

        Optional<Model> result = repository.readPom( gav );
        result = repository.readPom( gav );

        Assert.assertFalse( result.isPresent() );
        Mockito.verify( repo, Mockito.times( 1 ) ).readPom( gav );
    }
}
