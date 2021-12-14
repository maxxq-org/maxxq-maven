package org.chabernac.maven.repository;

import org.apache.maven.model.Model;
import org.chabernac.dependency.GAV;

public interface IRepository {
	public Model readPom(GAV gav);
}
