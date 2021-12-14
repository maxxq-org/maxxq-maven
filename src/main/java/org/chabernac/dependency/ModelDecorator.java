package org.chabernac.dependency;

import java.util.Properties;

import org.apache.maven.model.Model;
import org.chabernac.maven.repository.IRepository;

public class ModelDecorator {
	private final Model model;
	private final IRepository repository;

	public ModelDecorator(Model model, IRepository repository) {
		super();
		this.model = model;
		this.repository = repository;
	}

	public Properties getAggregatedProperties() {
		Properties properties = new Properties();
		properties.putAll(model.getProperties());
		if (model.getParent() != null) {

		}
		return model.getProperties();
	}

	private void addProperties(Model parent) {
		repository.readPom(GAV.fromModel(parent));
	}

}
