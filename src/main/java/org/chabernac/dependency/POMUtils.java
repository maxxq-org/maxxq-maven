package org.chabernac.dependency;

public class POMUtils {
	private final String centralRepoURL;

	public POMUtils(String centralRepoURL) {
		super();
		this.centralRepoURL = centralRepoURL;
	}

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
}
