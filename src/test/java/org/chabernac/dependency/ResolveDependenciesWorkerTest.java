
package org.chabernac.dependency;

import java.util.Set;

import org.apache.maven.model.Dependency;
import org.chabernac.maven.repository.RemoteRepository;
import org.junit.Assert;
import org.junit.Test;

public class ResolveDependenciesWorkerTest {
	ResolveDependenciesWorker worker = new ResolveDependenciesWorker(getClass().getResourceAsStream("/pom.xml"), new RemoteRepository("https://repo1.maven.org/maven2"));

	@Test
	public void resolveDependencies() {
		Set<Dependency> dependencies = worker.get();

		Assert.assertEquals(16, dependencies.size());
	}

}
