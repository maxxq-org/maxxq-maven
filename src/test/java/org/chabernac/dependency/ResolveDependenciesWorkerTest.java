
package org.chabernac.dependency;

import java.util.Set;

import org.apache.maven.model.Dependency;
import org.chabernac.maven.repository.RemoteRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ResolveDependenciesWorkerTest {

	private IPOMUtils pomUtils = new POMUtils();
	private ResolveDependenciesWorker worker;

	@Before
	public void setUp() {
		worker = new ResolveDependenciesWorker(
				pomUtils.getModelFromResource("/pom.xml"),
				new RemoteRepository(pomUtils));
	}

	@Test
	public void resolveDependencies() {
		Set<Dependency> dependencies = worker.get();

		dependencies.stream().forEach(dependency -> System.out.println("result: " + GAV.fromDependency(dependency)));
		Assert.assertEquals(16, dependencies.size());
	}

}
