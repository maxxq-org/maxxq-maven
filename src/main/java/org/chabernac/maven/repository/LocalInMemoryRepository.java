package org.chabernac.maven.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.maven.model.Model;
import org.chabernac.dependency.GAV;

public class LocalInMemoryRepository implements IRepository {
    private final Map<GAV, Model> store = new HashMap<>();

    @Override
    public Optional<Model> readPom(GAV gav) {
        if (store.containsKey(gav)) {
            return Optional.of(store.get(gav));
        }
        return Optional.empty();
    }

    public GAV addModel(Model model) {
        GAV gav = GAV.fromModel(model);
        store.put(gav, model);
        return gav;
    }

}
