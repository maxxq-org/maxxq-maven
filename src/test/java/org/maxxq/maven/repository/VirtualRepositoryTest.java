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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class VirtualRepositoryTest {
    private VirtualRepository virtualRepository = new VirtualRepository();

    @Mock
    private IRepository       repository1;

    @Mock
    private IRepository       repository2;

    @Mock
    private GAV               gav;

    @Mock
    private Model             model;

    @BeforeEach
    void setUp() {
        virtualRepository.addRepository( repository1 );
        virtualRepository.addRepository( repository2 );
    }

    @Test
    void readPomRepo1HasModel() {
        Mockito.when( repository1.readPom( gav ) ).thenReturn( Optional.of( model ) );

        Optional<Model> result = virtualRepository.readPom( gav );

        assertTrue( result.isPresent() );
        assertSame( model, result.get() );
    }

    @Test
    void readPomRepo2HasModel() {
        Mockito.when( repository1.readPom( gav ) ).thenReturn( Optional.empty() );
        Mockito.when( repository2.readPom( gav ) ).thenReturn( Optional.of( model ) );

        Optional<Model> result = virtualRepository.readPom( gav );

        assertTrue( result.isPresent() );
        assertSame( model, result.get() );
    }

    @Test
    void readPomNoneHaveModel() {
        Mockito.when( repository1.readPom( gav ) ).thenReturn( Optional.empty() );
        Mockito.when( repository2.readPom( gav ) ).thenReturn( Optional.empty() );

        Optional<Model> result = virtualRepository.readPom( gav );

        assertFalse( result.isPresent() );
    }

    @Test
    void isWritableNotWritable() {
        Mockito.when( repository1.isWritable() ).thenReturn( Boolean.FALSE );
        Mockito.when( repository2.isWritable() ).thenReturn( Boolean.FALSE );

        assertFalse( virtualRepository.isWritable() );
    }

    @Test
    void isWritableRepo1() {
        Mockito.when( repository1.isWritable() ).thenReturn( Boolean.TRUE );

        assertTrue( virtualRepository.isWritable() );
    }

    @Test
    void isWritableRepo2() {
        Mockito.when( repository1.isWritable() ).thenReturn( Boolean.FALSE );
        Mockito.when( repository2.isWritable() ).thenReturn( Boolean.TRUE );

        assertTrue( virtualRepository.isWritable() );
    }

    @Test
    void isWritableRepo1and2() {
        Mockito.when( repository1.isWritable() ).thenReturn( Boolean.TRUE );

        assertTrue( virtualRepository.isWritable() );
    }

    @Test
    void storeRepo2isWritable() {
        Mockito.when( repository1.isWritable() ).thenReturn( Boolean.FALSE );
        Mockito.when( repository2.isWritable() ).thenReturn( Boolean.TRUE );
        Mockito.when( repository2.store( model ) ).thenReturn( gav );

        GAV result = virtualRepository.store( model );

        assertSame( gav, result );
        Mockito.verify( repository1, Mockito.times( 0 ) ).store( model );
        Mockito.verify( repository2, Mockito.times( 1 ) ).store( model );
    }

    @Test
    void storeNoRepoIsWritable() {
        assertThrows( RepositoryException.class, () -> {
            Mockito.when( repository1.isWritable() ).thenReturn( Boolean.FALSE );
            Mockito.when( repository2.isWritable() ).thenReturn( Boolean.FALSE );

            virtualRepository.store( model );
        } );
    }
}
