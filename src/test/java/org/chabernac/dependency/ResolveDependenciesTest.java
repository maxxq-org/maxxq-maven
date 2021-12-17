package org.chabernac.dependency;

import java.util.Set;
import org.apache.maven.model.Dependency;
import org.chabernac.maven.repository.LocalInMemoryRepository;
import org.chabernac.maven.repository.RemoteRepository;
import org.chabernac.maven.repository.VirtualRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ResolveDependenciesTest {
    private ResolveDependencies resolveDependencies;
    private LocalInMemoryRepository localRepository = new LocalInMemoryRepository();


    @Before
    public void setUp() {
        resolveDependencies = new ResolveDependencies(new VirtualRepository()
                .addRepository(new RemoteRepository(RemoteRepository.MAVEN_CENTRAL))
                .addRepository(localRepository));
    }

    @Test
    public void resolveDependencies() {
        GAV gav = localRepository.addModel(new ModelIO().getModelFromInputStream(getClass().getResourceAsStream("/pom.xml")));

        Set<Dependency> dependencies = resolveDependencies.getDependencies(gav);

        dependencies.stream().forEach(dependency -> System.out.println("result: " + GAV.fromDependency(dependency)));
        Assert.assertEquals(16, dependencies.size());
    }
}
