package org.chabernac.maven.repository;

import java.io.InputStream;

import org.chabernac.dependency.GAV;

public interface IRepository {
	public InputStream readPom(GAV gav);
}
