package org.chabernac.dependency;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

public class ResolveDependencies implements IDependencyResolver {
	private static final String MAVEN_CENTRAL = "https://repo1.maven.org/maven2/";

	private final String centralRepoUrl;

	public ResolveDependencies() {
		this(MAVEN_CENTRAL);
	}

	public ResolveDependencies(String centralRepoUrl) {
		super();
		this.centralRepoUrl = centralRepoUrl;
	}

	public Set<Dependency> getDependencies(InputStream pomStream) {
		if (pomStream == null) {
			return new HashSet<>();
		}
		try {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			Model model = reader.read(pomStream);
			return new HashSet<Dependency>(model.getDependencies());
		} catch (Exception e) {
			throw new DepencyResolvingException("Could not resolve dependencies", e);
		}
	}
}
