package org.chabernac.dependency;

import java.util.Properties;
import org.apache.maven.model.Model;
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

  @Mock
  private Model model;

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

  @Test
  public void isPropertyValue() {
    Assert.assertTrue(pomUtils.isPropertyValue("${library.version}"));
    Assert.assertFalse(pomUtils.isPropertyValue("1.2.3"));
  }

  @Test
  public void resolveProperties() {
    Properties properties = new Properties();
    properties.put("library.version", "1.2.3");
    Mockito.when(model.getProperties()).thenReturn(properties);
    Assert.assertEquals("1.2.3", pomUtils.resolveProperty("${library.version}", model));
    Assert.assertEquals("1.2.3", pomUtils.resolveProperty("1.2.3", model));
  }

  @Test
  public void resolveProjectVersion() {
    Mockito.when(model.getVersion()).thenReturn("1.2.3");
    Mockito.when(model.getGroupId()).thenReturn("groupId");
    Mockito.when(model.getArtifactId()).thenReturn("artefactId");
    Assert.assertEquals("1.2.3", pomUtils.resolveProperty("${project.version}", model));
  }
}
