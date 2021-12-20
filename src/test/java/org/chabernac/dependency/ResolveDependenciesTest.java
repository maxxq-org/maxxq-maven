package org.chabernac.dependency;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.maven.model.Dependency;
import org.chabernac.maven.repository.FileCachingRepository;
import org.chabernac.maven.repository.InMemoryCachingRepository;
import org.chabernac.maven.repository.LocalInMemoryRepository;
import org.chabernac.maven.repository.RemoteRepository;
import org.chabernac.maven.repository.VirtualRepository;
import org.junit.Assert;
import org.junit.Test;

public class ResolveDependenciesTest {
    private ResolveDependencies resolveDependencies = new ResolveDependencies(
            new VirtualRepository()
                    .addRepository(new InMemoryCachingRepository(new FileCachingRepository(Paths.get("c:/data/pomcache/"), new RemoteRepository(RemoteRepository.MAVEN_CENTRAL))))
                    .addRepository(new LocalInMemoryRepository()));

    @Test
    public void resolveDependencies() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies(getClass().getResourceAsStream("/pom.xml"));

        dependencies.stream().forEach(dependency -> System.out.println("result: " + GAV.fromDependency(dependency)));

        Assert.assertEquals(10, dependencies.size());
    }

    @Test
    public void resolveDependenciesWithImport() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies(getClass().getResourceAsStream("/commons-io-2.11.0.pom.xml"));

        dependencies.stream().forEach(dependency -> System.out.println("result: " + GAV.fromDependency(dependency)));

        List<Dependency> sortedDependencies = new ArrayList<>(dependencies);
        Collections.sort(sortedDependencies, (dep1, dep2) -> dep1.getArtifactId().compareTo(dep2.getArtifactId()));
        System.out.println("sorted");
        sortedDependencies.stream().forEach(dependency -> System.out.println("result: " + GAV.fromDependency(dependency)));

        Assert.assertEquals(28, dependencies.size());
    }

    @Test
    public void resolveDependenciesMultiModule() {
        Set<Dependency> dependencies = resolveDependencies.getDependencies(getClass().getResourceAsStream("/parent/pom.xml"), getClass().getResourceAsStream("/module1/pom.xml"),
                getClass().getResourceAsStream("/module2/pom.xml"));

        dependencies.stream().forEach(dependency -> System.out.println("result: " + GAV.fromDependency(dependency)));
        Assert.assertEquals(16, dependencies.size());
    }
}
