package org.maxxq.maven.dependency;

import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class POMUtilsTest {
    private POMUtils pomUtils = new POMUtils();

    @Mock
    private GAV dependency;

    @Mock
    private Model model;

    @Mock
    private Parent parent;

    @Test
    void isPropertyValue() {
        assertTrue(pomUtils.isPropertyValue("${library.version}"));
        assertTrue(pomUtils.isPropertyValue("[${mongo.min.version},${mongo.max.version}]"));
        assertFalse(pomUtils.isPropertyValue("1.2.3"));
    }

    @Test
    void resolveProperties() {
        Properties properties = new Properties();
        properties.put("library.version", "1.2.3");
        Mockito.when(model.getProperties()).thenReturn(properties);
        
        assertEquals("1.2.3", pomUtils.resolveProperty("${library.version}", model));
        assertEquals("1.2.3", pomUtils.resolveProperty("1.2.3", model));
    }

    @Test
    void resolvePropertiesWithRange() {
        Properties properties = new Properties();
        properties.put("mongo.min.version", "1.2.0");
        properties.put("mongo.max.version", "1.3.0");
        Mockito.when(model.getProperties()).thenReturn(properties);
        
        assertEquals("[1.2.0,1.3.0]", pomUtils.resolveProperty("[${mongo.min.version},${mongo.max.version}]", model));
    }

    @Test
    void resolvePropertyNotFound() {
        Properties properties = new Properties();
        Mockito.when(model.getProperties()).thenReturn(properties);
        
        assertEquals("${library.version}", pomUtils.resolveProperty("${library.version}", model));
    }

    @Test
    void resolveProjectPropeties() {
        Mockito.when(model.getVersion()).thenReturn("1.2.3");
        Mockito.when(model.getGroupId()).thenReturn("groupId");
        Mockito.when(model.getArtifactId()).thenReturn("artifactId");
        
        assertEquals("1.2.3", pomUtils.resolveProperty("${project.version}", model));
        assertEquals("groupId", pomUtils.resolveProperty("${project.groupId}", model));
        assertEquals("artifactId", pomUtils.resolveProperty("${project.artifactId}", model));
        assertEquals("1.2.3", pomUtils.resolveProperty("${pom.version}", model));
        assertEquals("groupId", pomUtils.resolveProperty("${pom.groupId}", model));
        assertEquals("artifactId", pomUtils.resolveProperty("${pom.artifactId}", model));
    }

    @Test
    void resolveParentProperties() {
        Mockito.when(model.getParent()).thenReturn( parent );
        Mockito.when(parent.getVersion()).thenReturn( "1.2.3" );
        Mockito.when(parent.getArtifactId()).thenReturn( "artifactId" );
        Mockito.when(parent.getGroupId()).thenReturn( "groupId" );
        
        assertEquals("1.2.3", pomUtils.resolveProperty("${project.parent.version}", model));
        assertEquals("groupId", pomUtils.resolveProperty("${project.parent.groupId}", model));
        assertEquals("artifactId", pomUtils.resolveProperty("${project.parent.artifactId}", model));
        assertEquals("1.2.3", pomUtils.resolveProperty("${pom.parent.version}", model));
        assertEquals("groupId", pomUtils.resolveProperty("${pom.parent.groupId}", model));
        assertEquals("artifactId", pomUtils.resolveProperty("${pom.parent.artifactId}", model));
    }
}
