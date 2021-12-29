package org.chabernac.dependency;

import java.io.InputStream;
import java.io.OutputStream;
import org.apache.maven.model.Model;

public interface IModelIO {

    public Model getModelFromInputStream(InputStream inputStream);

    public Model getModelFromResource(String resource);
    
    public Model getModelFromString(String modelContent);

    public void writeModelToStream(Model model, OutputStream outputStream);

    public String writeModelToString(Model model);

}
