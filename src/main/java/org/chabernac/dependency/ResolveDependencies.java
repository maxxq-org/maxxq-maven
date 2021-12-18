package org.chabernac.dependency;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.chabernac.maven.repository.IRepository;

public class ResolveDependencies implements IDependencyResolver {

    private final IRepository repository;

    public ResolveDependencies(IRepository repository) {
        this.repository = repository;
    }

    public GAV store(Model model) {
        return repository.store(model);
    }
    
    public GAV store(InputStream inputStream) {
        return store(new ModelIO().getModelFromInputStream(inputStream));
    }
    
    public Set<Dependency> getDependencies(InputStream pomStream){
        GAV gav = store(pomStream);
        return getDependencies(gav);
    }

    public Set<Dependency> getDependencies(GAV gav) {
        try {
            Optional<Model> model = repository.readPom(gav);
            if (model.isEmpty()) {
                return new HashSet<>();
            }
            return new ResolveDependenciesWorker(model.get(), repository).get();
        } catch (Exception e) {
            throw new DepencyResolvingException("Could not resolve dependencies", e);
        }
    }
}
