package org.chabernac.maven.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.maven.model.Model;
import org.chabernac.dependency.GAV;

public class InMemoryCachingRepository implements IRepository {
  private final IRepository repository;

  private final Map<GAV, Optional<Model>> cache = new HashMap<>();

  public InMemoryCachingRepository(IRepository repository) {
    super();
    this.repository = repository;
  }


  @Override
  public Optional<Model> readPom(GAV gav) {
    if (cache.containsKey(gav)) {
      return cache.get(gav);
    }
    Optional<Model> model = repository.readPom(gav);
    cache.put(gav, model);
    return model;
  }

}
