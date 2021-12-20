package org.chabernac.dependency;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
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
    public void writeModelToStream(Model model, OutputStream outputStream) {
        MavenXpp3Writer reader = new MavenXpp3Writer();
        try {
            reader.write(outputStream, model);
        } catch (Exception e) {
            throw new RepositoryException("Could not write model to outputstream", e);
        }
    }
    
    @Override
    public String writeModelToString(Model model) {
        MavenXpp3Writer reader = new MavenXpp3Writer();
        try {
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            reader.write(byteArrayOut, model);
            return byteArrayOut.toString();
        } catch (Exception e) {
            throw new RepositoryException("Could not write model to outputstream", e);
        }
    }

    @Override
    public Model getModelFromResource(String resource) {
        return getModelFromInputStream(getClass().getResourceAsStream(resource));
    }
}
