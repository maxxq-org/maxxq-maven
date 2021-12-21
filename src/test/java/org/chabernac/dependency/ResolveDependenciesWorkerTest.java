
package org.chabernac.dependency;

import java.util.Set;

import org.apache.maven.model.Dependency;
import org.chabernac.maven.repository.RemoteRepository;
import org.chabernac.maven.repository.RemoteRepositoryForTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class ResolveDependenciesWorkerTest {

    private ResolveDependenciesWorker worker;

    @Before
    public void setUp() {
        worker = new ResolveDependenciesWorker( new ModelIO().getModelFromResource( "/pom.xml" ), new RemoteRepository( RemoteRepositoryForTest.REPO ) );
    }

    @Test
    public void resolveDependencies() {
        Set<Dependency> dependencies = worker.get();

        Assert.assertEquals( 10, dependencies.size() );
    }
}
