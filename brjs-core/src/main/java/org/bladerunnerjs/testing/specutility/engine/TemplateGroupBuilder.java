package org.bladerunnerjs.testing.specutility.engine;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.TemplateGroup;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;

public class TemplateGroupBuilder {
	@SuppressWarnings("unused")
	private final SpecTest specTest;
	private final BuilderChainer builderChainer;
	private final TemplateGroup templateGroup;

	public TemplateGroupBuilder(SpecTest specTest, TemplateGroup templateGroup) {
		this.specTest = specTest;
		builderChainer = new BuilderChainer(specTest);
		this.templateGroup = templateGroup;
	}

	public BuilderChainer templateGroupCreated() throws InvalidNameException, ModelUpdateException {
		templateGroup.template("app").create();
		templateGroup.template("aspect").create();
		templateGroup.template("bladeset").create();
		templateGroup.template("blade").create();
		return builderChainer;
	}
}
