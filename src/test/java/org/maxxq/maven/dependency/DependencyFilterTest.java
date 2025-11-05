package org.maxxq.maven.dependency;

import org.apache.maven.model.Dependency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DependencyFilterTest {

    private DependencyFilter dependencyFilter;

    @BeforeEach
    void setUp() {
        dependencyFilter = new DependencyFilter();
    }

    @Test
    void keepOptional() {
        // Arrange
        dependencyFilter.setKeepOptional(true);
        Dependency dependency = new Dependency();
        dependency.setOptional(true);

        // Act
        boolean result = dependencyFilter.keepDependency(dependency, 0);

        // Assert
        assertTrue(result);
    }

    @Test
    void notKeepOptional() {
        // Arrange
        Dependency dependency = new Dependency();
        dependency.setOptional(true);
        dependency.setScope("compile");

        // Act
        boolean result = dependencyFilter.keepDependency(dependency, 0);

        // Assert
        assertFalse(result);
    }

    @Test
    void keepTest() {
        // Arrange
        dependencyFilter.setKeepTest(true);
        Dependency dependency = new Dependency();
        dependency.setScope("test");

        // Act
        boolean result = dependencyFilter.keepDependency(dependency, 0);

        // Assert
        assertTrue(result);
    }

    @Test
    void notKeepTest() {
        // Arrange
        dependencyFilter.setKeepTest(false);
        Dependency dependency = new Dependency();
        dependency.setScope("test");

        // Act
        boolean result = dependencyFilter.keepDependency(dependency, 1);

        // Assert
        assertFalse(result);
    }

    @Test
    void keepCompile() {
        // Arrange
        dependencyFilter.setKeepCompile(true);
        Dependency dependency = new Dependency();
        dependency.setScope("compile");

        // Act
        boolean result = dependencyFilter.keepDependency(dependency, 0);

        // Assert
        assertTrue(result);
    }

    @Test
    void notKeepCompile() {
        // Arrange
        dependencyFilter.setKeepCompile(false);
        Dependency dependency = new Dependency();
        dependency.setScope("compile");

        // Act
        boolean result = dependencyFilter.keepDependency(dependency, 0);

        // Assert
        assertFalse(result);
    }

    @Test
    void keepRuntime() {
        // Arrange
        dependencyFilter.setKeepRuntime(true);
        Dependency dependency = new Dependency();
        dependency.setScope("runtime");

        // Act
        boolean result = dependencyFilter.keepDependency(dependency, 0);

        // Assert
        assertTrue(result);
    }

    @Test
    void notKeepRuntime() {
        // Arrange
        dependencyFilter.setKeepRuntime(false);
        Dependency dependency = new Dependency();
        dependency.setScope("runtime");

        // Act
        boolean result = dependencyFilter.keepDependency(dependency, 0);

        // Assert
        assertFalse(result);
    }

    @Test
    void keepProvided() {
        // Arrange
        dependencyFilter.setKeepProvided(true);
        Dependency dependency = new Dependency();
        dependency.setScope("provided");

        // Act
        boolean result = dependencyFilter.keepDependency(dependency, 0);

        // Assert
        assertTrue(result);
    }

    @Test
    void notKeepProvided() {
        // Arrange
        dependencyFilter.setKeepProvided(false);
        Dependency dependency = new Dependency();
        dependency.setScope("provided");

        // Act
        boolean result = dependencyFilter.keepDependency(dependency, 0);

        // Assert
        assertFalse(result);
    }

    @Test
    void keepEmptyScope() {
        // Arrange
        Dependency dependency = new Dependency();

        // Act
        boolean result = dependencyFilter.keepDependency(dependency, 0);

        // Assert
        assertTrue(result);
    }

    @Test
    void keepTestRootOnly() {
        // Arrange
        dependencyFilter.setKeepTest(false);
        dependencyFilter.setKeepTestRoot(true);
        Dependency dependency = new Dependency();
        dependency.setScope("test");

        // Act
        boolean result = dependencyFilter.keepDependency(dependency, 0);

        // Assert
        assertTrue(result);
    }

    @Test
    void notKeepTestRootOnly() {
        // Arrange
        dependencyFilter.setKeepTest(false);
        dependencyFilter.setKeepTestRoot(true);
        Dependency dependency = new Dependency();
        dependency.setScope("test");

        // Act
        boolean result = dependencyFilter.keepDependency(dependency, 1);

        // Assert
        assertFalse(result);
    }

    @Test
    void isKeepCompile() {
        assertTrue(dependencyFilter.isKeepCompile());
        dependencyFilter.setKeepCompile(false);
        assertFalse(dependencyFilter.isKeepCompile());
    }

    @Test
    void isKeepRuntime() {
        assertTrue(dependencyFilter.isKeepRuntime());
        dependencyFilter.setKeepRuntime(false);
        assertFalse(dependencyFilter.isKeepRuntime());
    }

    @Test
    void isKeepProvided() {
        assertFalse(dependencyFilter.isKeepProvided());
        dependencyFilter.setKeepProvided(true);
        assertTrue(dependencyFilter.isKeepProvided());
    }

    @Test
    void isKeepTest() {
        assertFalse(dependencyFilter.isKeepTest());
        dependencyFilter.setKeepTest(true);
        assertTrue(dependencyFilter.isKeepTest());
    }

    @Test
    void isKeepOptional() {
        assertFalse(dependencyFilter.isKeepOptional());
        dependencyFilter.setKeepOptional(true);
        assertTrue(dependencyFilter.isKeepOptional());
    }

    @Test
    void keepNothing() {
        dependencyFilter.keepNothing();
        assertFalse(dependencyFilter.isKeepCompile());
        assertFalse(dependencyFilter.isKeepRuntime());
        assertFalse(dependencyFilter.isKeepProvided());
        assertFalse(dependencyFilter.isKeepTest());
        assertFalse(dependencyFilter.isKeepOptional());
    }

    @Test
    void keepAll() {
        dependencyFilter.keepAll();
        assertTrue(dependencyFilter.isKeepCompile());
        assertTrue(dependencyFilter.isKeepRuntime());
        assertTrue(dependencyFilter.isKeepProvided());
        assertTrue(dependencyFilter.isKeepTest());
        assertTrue(dependencyFilter.isKeepOptional());
    }

    @Test
    void keepMavenDefault() {
        dependencyFilter.keepMavenDefault();
        assertTrue(dependencyFilter.isKeepCompile());
        assertTrue(dependencyFilter.isKeepRuntime());
        assertFalse(dependencyFilter.isKeepProvided());
        assertFalse(dependencyFilter.isKeepTest());
        assertFalse(dependencyFilter.isKeepOptional());
    }
}
