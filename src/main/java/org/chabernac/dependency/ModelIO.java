package org.chabernac.dependency;

import java.io.InputStream;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.chabernac.maven.repository.RepositoryException;

public class ModelIO implements IModelIO {
    @Override
    public Model getModelFromInputStream(InputStream inputStream) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try {
            return reader.read(inputStream);
        } catch (Exception e) {
            throw new RepositoryException("Could not read model from inputstream", e);
        }
    }

    @Override
    public Model getModelFromResource(String resource) {
        return getModelFromInputStream(getClass().getResourceAsStream(resource));
    }
}
