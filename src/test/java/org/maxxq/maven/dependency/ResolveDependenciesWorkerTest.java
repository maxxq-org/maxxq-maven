
package org.maxxq.maven.dependency;

import java.nio.file.Paths;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maxxq.maven.repository.FileCachingRepository;
import org.maxxq.maven.repository.InMemoryCachingRepository;
import org.maxxq.maven.repository.RemoteRepository;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ResolveDependenciesWorkerTest {

    private ResolveDependenciesWorker worker;

    @BeforeEach
    void setUp() {
        worker = new ResolveDependenciesWorker(
            new ModelIO().getModelFromResource( "/maven-dependencies.pom.xml" ),
            new InMemoryCachingRepository(
                new FileCachingRepository(
                    Paths.get( "src/test/resources/pomcache" ),
                    new RemoteRepository() ) ),
            false,
            null);
    }

    @Test
    void resolveDependencies() {
        Set<Dependency> dependencies = worker.get();

        assertEquals( 10, dependencies.size() );
    }
}
