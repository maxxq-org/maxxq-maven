package org.chabernac.dependency;

import java.io.InputStream;
import org.apache.maven.model.Model;

public interface IModelIO {

    public Model getModelFromInputStream(InputStream inputStream);

    public Model getModelFromResource(String resource);

}
