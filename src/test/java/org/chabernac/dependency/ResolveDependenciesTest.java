package org.chabernac.dependency;

import java.util.Set;

import org.apache.maven.model.Dependency;
import org.chabernac.dependency.ResolveDependencies;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResolveDependenciesTest {
	private ResolveDependencies resolveDependencies = new ResolveDependencies();

	@Test
	public void resolveDependencies() {
		Set<Dependency> dependencies = resolveDependencies.getDependencies(getClass().getResourceAsStream("/pom.xml"));

		Assertions.assertEquals(16, dependencies.size());
	}
}
