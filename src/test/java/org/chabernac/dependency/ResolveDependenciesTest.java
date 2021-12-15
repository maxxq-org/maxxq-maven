package org.chabernac.dependency;

import java.util.Set;
import org.apache.maven.model.Dependency;
import org.junit.Assert;
import org.junit.Test;

public class ResolveDependenciesTest {
  private ResolveDependencies resolveDependencies = new ResolveDependencies();

  @Test
  public void resolveDependencies() {
    Set<Dependency> dependencies = resolveDependencies.getDependencies(getClass().getResourceAsStream("/pom.xml"));

    Assert.assertEquals(16, dependencies.size());
  }
}
