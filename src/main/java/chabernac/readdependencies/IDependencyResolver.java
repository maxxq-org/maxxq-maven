package chabernac.readdependencies;

import java.io.InputStream;
import java.util.Set;

import org.apache.maven.model.Dependency;

public interface IDependencyResolver {
	public Set<Dependency> getDependencies(InputStream pomStream) throws DepencyResolvingException;
}
