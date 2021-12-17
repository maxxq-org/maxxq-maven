package org.chabernac.dependency;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.chabernac.maven.repository.InMemoryCachingRepository;
import org.chabernac.maven.repository.RemoteRepository;

public class ResolveDependencies implements IDependencyResolver {

  private final POMUtils pomUtils;

  public ResolveDependencies(String centralRepoUrl) {
    this.pomUtils = new POMUtils(centralRepoUrl);
  }

  public Set<Dependency> getDependencies(InputStream pomStream) {
    if (pomStream == null) {
      return new HashSet<>();
    }
    try {
      return new ResolveDependenciesWorker(pomUtils.getModelFromInputStream(pomStream), new InMemoryCachingRepository(new RemoteRepository(pomUtils))).get();
    } catch (Exception e) {
      throw new DepencyResolvingException("Could not resolve dependencies", e);
    }
  }
}
