package org.maxxq.maven.dependency;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.model.Model;

public interface IModelIO {

    public Model getModelFromInputStream( InputStream inputStream );

    public Model getModelFromResource( String resource );

    public Model getModelFromString( String modelContent );

    public void writeModelToStream( Model model, OutputStream outputStream );

    public String writeModelToString( Model model );

    public Metadata getMetaDataFromString( String metaContent );

    public Metadata getMetaDataFromString( InputStream inputStream );

    public void writeMetadataToStream( Metadata metadata, OutputStream output );

}
