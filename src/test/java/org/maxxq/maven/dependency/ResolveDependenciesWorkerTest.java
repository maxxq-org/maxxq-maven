
package org.maxxq.maven.dependency;

import java.nio.file.Paths;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxxq.maven.repository.FileCachingRepository;
import org.maxxq.maven.repository.InMemoryCachingRepository;
import org.maxxq.maven.repository.RemoteRepository;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class ResolveDependenciesWorkerTest {

    private ResolveDependenciesWorker worker;

    @Before
    public void setUp() {
        worker = new ResolveDependenciesWorker(
            new ModelIO().getModelFromResource( "/maven-dependencies.pom.xml" ),
            new InMemoryCachingRepository(
                new FileCachingRepository(
                    Paths.get( "src/test/resources/pomcache" ),
                    new RemoteRepository() ) ),
            false );
    }

    @Test
    public void resolveDependencies() {
        Set<Dependency> dependencies = worker.get();

        Assert.assertEquals( 10, dependencies.size() );
    }
}
