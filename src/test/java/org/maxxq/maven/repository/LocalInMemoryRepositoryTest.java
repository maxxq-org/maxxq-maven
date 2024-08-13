package org.maxxq.maven.repository;

import java.util.Optional;

import org.apache.maven.model.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maxxq.maven.dependency.GAV;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class LocalInMemoryRepositoryTest {
    private LocalInMemoryRepository repository = new LocalInMemoryRepository();

    @Mock
    private Model                   model;

    @Test
    void addModel() {
        Mockito.when( model.getGroupId() ).thenReturn( "groupId" );
        Mockito.when( model.getArtifactId() ).thenReturn( "artifactId" );
        Mockito.when( model.getVersion() ).thenReturn( "version" );

        repository.store( model );

        Optional<Model> result = repository.readPom( new GAV( "groupId", "artifactId", "version" ) );

        assertTrue( result.isPresent() );
        assertSame( model, result.get() );
    }
}
