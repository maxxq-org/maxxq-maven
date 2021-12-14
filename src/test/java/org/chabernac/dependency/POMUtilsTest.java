package org.chabernac.dependency;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class POMUtilsTest {
	private POMUtils pomUtils = new POMUtils("https://repo1.maven.org/maven2");

	@Mock
	private GAV dependency;

	@Test
	public void getPomURL() {
		Mockito.when(dependency.getArtifactId()).thenReturn("maven-model");
		Mockito.when(dependency.getGroupId()).thenReturn("org.apache.maven");
		Mockito.when(dependency.getVersion()).thenReturn("3.8.4");

		Assert
				.assertEquals(
						"https://repo1.maven.org/maven2/org/apache/maven/maven-model/3.8.4/maven-model-3.8.4.pom",
						pomUtils.getPOMUrl(dependency));
	}
}
