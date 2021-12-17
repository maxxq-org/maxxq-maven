package org.chabernac.dependency;

import java.util.Set;
import org.apache.maven.model.Dependency;
import org.junit.Assert;
import org.junit.Test;

public class ResolveDependenciesTest {
  private ResolveDependencies resolveDependencies = new ResolveDependencies(POMUtils.MAVEN_CENTRAL);

  @Test
  public void resolveDependencies() {
    Set<Dependency> dependencies = resolveDependencies.getDependencies(getClass().getResourceAsStream("/pom.xml"));

    dependencies.stream().forEach(dependency -> System.out.println("result: " + GAV.fromDependency(dependency)));
    Assert.assertEquals(16, dependencies.size());
  }
}
