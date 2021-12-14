package org.chabernac.dependency;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

public class GAV {
	private final String groupId;
	private final String artifactId;
	private final String version;
	
	public static GAV fromDependency(Dependency dependency) {
		return new GAV(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
	}
	
	public static GAV fromModel(Model model) {
		return new GAV(model.getGroupId(), model.getArtifactId(), model.getVersion());
	}

	public GAV(String groupId, String artifactId, String version) {
		super();
		if(StringUtils.isEmpty(groupId)) {
			throw new IllegalArgumentException("GroupId must not be null");
		}
		if(StringUtils.isEmpty(artifactId)) {
			throw new IllegalArgumentException("ArtifactId must not be null");
		}
		if(StringUtils.isEmpty(version)) {
			throw new IllegalArgumentException("Version must not be null");
		}
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

}
