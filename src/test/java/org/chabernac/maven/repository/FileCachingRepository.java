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
    private final IModelIO modelIO = new ModelIO();

    public FileCachingRepository(IRepository repository, Path basePath) {
        super();
        this.repository = repository;
        this.getMavenRepoPath = new GetMavenRepoURL(basePath.toString());
    }

    @Override
    public Optional<Model> readPom(GAV gav) {
        Path path = Paths.get(getMavenRepoPath.apply(gav));
        try {
            if (Files.exists(path)) {
                return Optional.of(modelIO.getModelFromInputStream(new FileInputStream(path.toFile())));
            }

            Optional<Model> modelOptional = repository.readPom(gav);

            if (modelOptional.isPresent()) {
                Files.createDirectories(path);
                modelIO.writeModelToStream(modelOptional.get(), new FileOutputStream(path.toFile()));
            }

            return modelOptional;
        } catch (IOException e) {
            LOGGER.error("Could not read/write to {}", path);
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
