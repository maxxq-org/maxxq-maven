package org.javaa.maven.dependency;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;

public class GAV {
	private final String groupId;
	private final String artifactId;
	private final String version;

	public static GAV fromDependency(Dependency dependency) {
		return new GAV(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
	}

	public static GAV fromModel(Model model) {
		String groupId = model.getGroupId();
		String artifactId = model.getArtifactId();
		String version = model.getVersion();
		if (StringUtils.isEmpty(groupId) && model.getParent() != null) {
			groupId = model.getParent().getGroupId();
		}
		if (StringUtils.isEmpty(version) && model.getParent() != null) {
			version = model.getParent().getVersion();
		}
		return new GAV(groupId, artifactId, version);
	}

	public static GAV fromParent(Parent parent) {
		return new GAV(parent.getGroupId(), parent.getArtifactId(), parent.getVersion());
	}

	public GAV(String groupId, String artifactId, String version) {
		super();
		if (StringUtils.isEmpty(groupId)) {
			throw new IllegalArgumentException("GroupId must not be null");
		}
		if (StringUtils.isEmpty(artifactId)) {
			throw new IllegalArgumentException("ArtifactId must not be null");
		}
		if (StringUtils.isEmpty(version)) {
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

	@Override
	public String toString() {
		return "GAV [groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GAV other = (GAV) obj;
		if (artifactId == null) {
			if (other.artifactId != null)
				return false;
		} else if (!artifactId.equals(other.artifactId))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

}
