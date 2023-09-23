package org.maxxq.maven.dependency;

import org.apache.maven.model.Dependency;

@FunctionalInterface
public interface IDependencyFilter {
    public boolean keepDependency( Dependency dependency, int depth );
}
