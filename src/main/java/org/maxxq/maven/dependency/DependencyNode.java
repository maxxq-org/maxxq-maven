package org.maxxq.maven.dependency;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.maven.model.Dependency;

public class DependencyNode {
    private final Dependency          dependency;
    private final Set<DependencyNode> children = new LinkedHashSet<DependencyNode>();

    public DependencyNode( Dependency dependency ) {
        super();
        this.dependency = dependency;
    }

    public DependencyNode addChild( DependencyNode dependency ) {
        children.add( dependency );
        return this;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public Set<DependencyNode> getChildren() {
        return Collections.unmodifiableSet( children );
    }
}
