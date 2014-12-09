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
		templateGroup.template("aspect-test-unit-default").create();
		templateGroup.template("aspect-test-acceptance-default").create();
		templateGroup.template("bladeset").create();
		templateGroup.template("bladeset-test-unit-default").create();
		templateGroup.template("blade").create();
		templateGroup.template("workbench").create();
		templateGroup.template("blade-test-unit-default").create();
		templateGroup.template("blade-test-acceptance-default").create();
		return builderChainer;
	}
}
