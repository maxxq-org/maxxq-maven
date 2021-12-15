package org.chabernac.maven.repository;

import java.util.Optional;
import org.apache.maven.model.Model;
import org.chabernac.dependency.GAV;

public interface IRepository {
  public Optional<Model> readPom(GAV gav);
}
