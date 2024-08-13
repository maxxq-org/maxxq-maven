package org.maxxq.maven.repository;

import java.util.Optional;

import org.apache.maven.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maxxq.maven.dependency.GAV;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InMemoryCachingRepositoryTest {
    private InMemoryCachingRepository repository;

    @Mock
    private IRepository               repo;

    @Mock
    private GAV                       gav;

    @Mock
    private Model                     model;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCachingRepository( repo );
    }

    @Test
    void readPom() {
        Mockito.when( repo.readPom( gav ) ).thenReturn( Optional.of( model ) );

        Optional<Model> result = repository.readPom( gav );
        result = repository.readPom( gav );

        assertTrue( result.isPresent() );
        assertSame( model, result.get() );
        Mockito.verify( repo, Mockito.times( 1 ) ).readPom( gav );
    }

    @Test
    void readPomNull() {
        Mockito.when( repo.readPom( gav ) ).thenReturn( Optional.empty() );

        Optional<Model> result = repository.readPom( gav );
        result = repository.readPom( gav );

        assertFalse( result.isPresent() );
        Mockito.verify( repo, Mockito.times( 1 ) ).readPom( gav );
    }
}
