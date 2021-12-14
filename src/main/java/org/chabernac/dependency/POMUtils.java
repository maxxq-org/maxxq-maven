package org.chabernac.dependency;

import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.chabernac.maven.repository.RepositoryException;

public class POMUtils implements IPOMUtils {
	public static final String MAVEN_CENTRAL = "https://repo1.maven.org/maven2";
	private static final Pattern VERSION_PATTERN = Pattern.compile("\\$\\{(.*)\\}");

	private final String centralRepoURL;

	public POMUtils() {
		this(MAVEN_CENTRAL);
	}

	public POMUtils(String centralRepoURL) {
		super();
		this.centralRepoURL = centralRepoURL;
	}

	@Override
	public String getPOMUrl(GAV dependency) {
		StringBuilder builder = new StringBuilder();
		builder.append(centralRepoURL);
		if (!centralRepoURL.endsWith("/")) {
			builder.append("/");
		}
		builder.append(dependency.getGroupId().replace(".", "/"));
		builder.append("/");
		builder.append(dependency.getArtifactId());
		builder.append("/");
		builder.append(dependency.getVersion());
		builder.append("/");
		builder.append(dependency.getArtifactId());
		builder.append("-");
		builder.append(dependency.getVersion());
		builder.append(".pom");
		return builder.toString();
	}

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

	@Override
	public boolean isPropertyValue(String value) {
		return VERSION_PATTERN.matcher(value).matches();
	}

	@Override
	public String resolveProperty(String propertyValue, Properties properties) {
		Matcher matcher = VERSION_PATTERN.matcher(propertyValue);
		if (matcher.matches()) {
			return properties.getProperty(matcher.group(1));
		}
		return propertyValue;
	}
}
