package org.chabernac.dependency;

import java.util.Set;
import org.apache.maven.model.Dependency;

public interface IDependencyResolver {
    public Set<Dependency> getDependencies(GAV gav) throws DepencyResolvingException;
}
