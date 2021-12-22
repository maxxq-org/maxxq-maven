package org.chabernac.dependency;

import java.io.InputStream;
import java.util.Set;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

public interface IDependencyResolver {
    public Set<Dependency> getDependencies(GAV gav) throws DepencyResolvingException;

    public GAV store(Model model);

    public GAV store(InputStream inputStream);

    public Set<Dependency> getDependencies(InputStream... pomStreams);

    public Set<Dependency> getDependencies(InputStream pomStream);

    public Set<Dependency> getDependencies(GAV... gavs);
}
