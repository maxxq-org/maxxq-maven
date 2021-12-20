package org.chabernac.maven.repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Model;
import org.chabernac.dependency.GAV;
import org.chabernac.dependency.GetMavenRepoURL;
import org.chabernac.dependency.IModelIO;
import org.chabernac.dependency.ModelIO;

public class FileCachingRepository implements IRepository {
    private Logger LOGGER = LogManager.getLogger(FileCachingRepository.class);
    private final IRepository repository;
    private final Function<GAV, String> getMavenRepoPath;
    private final IModelIO modelIO;

    public FileCachingRepository(Path basePath, IRepository repository) {
        this(repository, basePath, new ModelIO());
    }

    FileCachingRepository(IRepository repository, Path basePath, IModelIO modelIO) {
        super();
        this.repository = repository;
        this.getMavenRepoPath = new GetMavenRepoURL(basePath.toString());
        this.modelIO = modelIO;
    }

    @Override
    public Optional<Model> readPom(GAV gav) {
        Path path = Paths.get(getMavenRepoPath.apply(gav));
        try {
            if (Files.exists(path)) {
                try (FileInputStream input = new FileInputStream(path.toFile())) {
                    return Optional.of(modelIO.getModelFromInputStream(input));
                }
            }

            Optional<Model> modelOptional = repository.readPom(gav);

            if (modelOptional.isPresent()) {
                Files.createDirectories(path.getParent());
                try (FileOutputStream output = new FileOutputStream(path.toFile())) {
                    modelIO.writeModelToStream(modelOptional.get(), output);
                }
            }

            return modelOptional;
        } catch (IOException e) {
            LOGGER.error("Could not read/write to {}", path, e);
            return Optional.empty();
        }

    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public GAV store(Model model) {
        throw new UnsupportedOperationException("Store not supported in this repository");
    }

}
