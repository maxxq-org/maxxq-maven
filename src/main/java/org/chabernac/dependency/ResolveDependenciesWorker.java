package org.chabernac.dependency;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.chabernac.maven.repository.IRepository;

public class ResolveDependenciesWorker implements Supplier<Set<Dependency>> {
	private final static Logger LOGGER = LogManager.getLogger(ResolveDependenciesWorker.class);
	private final Model project;
	private final IRepository repository;
	private final IPOMUtils pomUtils = new POMUtils();

	private Map<GAV, Set<Dependency>> cachedDependencies = new HashMap<>();

	public ResolveDependenciesWorker(Model project, IRepository repository) {
		super();
		if (project == null) {
			throw new IllegalArgumentException("input project must not be null");
		}
		this.project = project;
		this.repository = repository;
	}

	@Override
	public Set<Dependency> get() {
		return processPomStream(project);
	}

	private Set<Dependency> processPomStream(Model model) {
		GAV gav = GAV.fromModel(model);
		if (cachedDependencies.containsKey(gav)) {
			LOGGER.debug("Returning cached dependencies for: " + gav);
			return cachedDependencies.get(gav);
		}

		try {
			Set<Dependency> dependencies = new HashSet<>();
			cachedDependencies.put(gav, dependencies);
			LOGGER.debug("Retrieving dependencies for: " + gav);
			getPropertiesAndDependencyManagementFromParents(model);
			resolveVersionsFromDependencyManagement(model);
			dependencies.addAll(model.getDependencies());
			model
					.getDependencies()
					.stream()
					.forEach(dependency -> dependencies.addAll(getTransitiveDependencies(dependency)));
			return dependencies;
		} catch (Exception e) {
			throw new DepencyResolvingException("Could not resolve dependencies", e);
		}
	}

	private Set<Dependency> getTransitiveDependencies(Dependency dependency) {
		return processPomStream(repository.readPom(GAV.fromDependency(dependency)));
	}

	private void resolveVersionsFromDependencyManagement(Model model) {
		model
				.getDependencyManagement()
				.getDependencies()
				.stream()
				.forEach(dependency -> LOGGER.debug("managed dependency: " + GAV.fromDependency(dependency)));
		LOGGER.debug("properties: " + model.getProperties());
		model
				.getDependencies()
				.stream()
				.forEach(
						dependency -> LOGGER
								.debug(
										"dependency: " + dependency.getGroupId() + ":" + dependency.getArtifactId()
												+ ":" + dependency.getVersion()));

		model
				.getDependencies()
				.stream()
				.filter(
						dependency -> StringUtils.isEmpty(dependency.getVersion())
								|| pomUtils.isPropertyValue(dependency.getVersion()))
				.forEach(dependency -> resolveVersion(dependency, model));
	}

	private void resolveVersion(Dependency resolveVersion, Model model) {
		model
				.getDependencyManagement()
				.getDependencies()
				.stream()
				.filter(dependency -> dependency.getGroupId().equals(resolveVersion.getGroupId()))
				.filter(dependency -> dependency.getArtifactId().equals(resolveVersion.getArtifactId()))
				.findFirst()
				.ifPresent(
						dependency -> resolveVersion
								.setVersion(pomUtils.resolveProperty(dependency.getVersion(), model.getProperties())));
	}

	private void getPropertiesAndDependencyManagementFromParents(Model model) {
		if (model.getDependencyManagement() == null) {
			model.setDependencyManagement(new DependencyManagement());
		}

		Parent parent = model.getParent();
		while (parent != null) {
			LOGGER.debug("Reading from parent with gav: " + GAV.fromParent(parent));
			Model parentModel = repository.readPom(GAV.fromParent(parent));
			if (parentModel.getDependencyManagement() != null
					&& parentModel.getDependencyManagement().getDependencies() != null) {
				parentModel
						.getDependencyManagement()
						.getDependencies()
						.forEach(dependency -> model.getDependencyManagement().addDependency(dependency));
			}
			if (parentModel.getDependencies() != null) {
				parentModel.getDependencies().forEach(dependency -> model.addDependency(dependency));
			}
			if (model.getProperties() != null) {
				model.getProperties().putAll(parentModel.getProperties());
			}
			parent = parentModel.getParent();
		}
	}

}
