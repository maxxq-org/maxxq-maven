package org.chabernac.dependency;

import java.io.InputStream;
import org.apache.maven.model.Model;

public interface IPOMUtils {

	public Model getModelFromInputStream(InputStream inputStream);

	public String getPOMUrl(GAV dependency);

	public Model getModelFromResource(String resource);

	public String resolveProperty(String propertyValue, Model properties);

	public boolean isPropertyValue(String value);

}
