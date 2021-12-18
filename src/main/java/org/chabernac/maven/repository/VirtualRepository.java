package org.chabernac.maven.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.maven.model.Model;
import org.chabernac.dependency.GAV;

public class VirtualRepository implements IRepository {
    private final Set<IRepository> repositories = new HashSet<>();

    @Override
    public Optional<Model> readPom(GAV gav) {
        return repositories
                .stream()
                .map(repository -> repository.readPom(gav))
                .filter(repository -> repository.isPresent())
                .findFirst()
                .orElse(Optional.empty());
    }

    public VirtualRepository addRepository(IRepository repository) {
        this.repositories.add(repository);
        return this;
    }

    @Override
    public boolean isWritable() {
        return repositories.stream()
                .map(repository -> repository.isWritable())
                .findFirst()
                .orElse(Boolean.FALSE);
    }

    @Override
    public GAV store(Model model) {
        return repositories.stream()
                .filter(repository -> repository.isWritable())
                .map(repository -> repository.store(model))
                .findFirst()
                .orElse(null);

    }

}
