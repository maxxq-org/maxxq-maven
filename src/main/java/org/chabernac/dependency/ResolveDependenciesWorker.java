package org.chabernac.dependency;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.chabernac.maven.repository.IRepository;

public class ResolveDependenciesWorker implements Supplier<Set<Dependency>> {
	private final InputStream pomInputStream;
	private final Set<Dependency> dependencies = new HashSet<>();
	private final IRepository repository;

	public ResolveDependenciesWorker(InputStream pomInputStream, IRepository repository) {
		super();
		if (pomInputStream == null) {
			throw new IllegalArgumentException("pom input stream must not be null");
		}
		this.pomInputStream = pomInputStream;
		this.repository = repository;
	}

	@Override
	public Set<Dependency> get() {
		dependencies.clear();
		processPomStream(pomInputStream);
		return dependencies;
	}

	private void processPomStream(InputStream pomStream) {
		try {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			Model model = reader.read(pomStream);
			List<Dependency> dependencies = model.getDependencies();
			dependencies.stream().forEach(dependency -> getTransitiveDependencies(dependency));
			dependencies.addAll(dependencies);
		} catch (Exception e) {
			throw new DepencyResolvingException("Could not resolve dependencies", e);
		}
	}

	private void getTransitiveDependencies(Dependency dependency) {
		processPomStream(repository.readPom(GAV.fromDependency(dependency)));
	}
}
