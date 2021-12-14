package org.chabernac.dependency;

import java.io.InputStream;
import java.util.Properties;

import org.apache.maven.model.Model;

public interface IPOMUtils {

	public Model getModelFromInputStream(InputStream inputStream);

	public String getPOMUrl(GAV dependency);

	public Model getModelFromResource(String resource);

	public String resolveProperty(String propertyValue, Properties properties);

	public boolean isPropertyValue(String value);

}
