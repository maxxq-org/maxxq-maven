package org.chabernac.dependency;

import java.util.Set;
import org.apache.maven.model.Dependency;
import org.chabernac.maven.repository.LocalInMemoryRepository;
import org.chabernac.maven.repository.RemoteRepository;
import org.chabernac.maven.repository.VirtualRepository;
import org.junit.Assert;
import org.junit.Test;

public class ResolveDependenciesTest {
    private ResolveDependencies resolveDependencies = new ResolveDependencies(
            new VirtualRepository()
                    .addRepository(new RemoteRepository(RemoteRepository.MAVEN_CENTRAL))
                    .addRepository(new LocalInMemoryRepository()));

    @Test
    public void resolveDependencies() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies(getClass().getResourceAsStream("/pom.xml"));

        dependencies.stream().forEach(dependency -> System.out.println("result: " + GAV.fromDependency(dependency)));
        Assert.assertEquals(16, dependencies.size());
    }
}
