package org.bladerunnerjs.spec.model;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.BladesetWorkbench;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.TemplateGroup;
import org.junit.Before;
import org.junit.Test;

public class BladesetWorkbenchTest extends SpecTest {
	private App app;
	private Bladeset bladeset;
	private BladesetWorkbench workbench;
	private TemplateGroup templates;
	private NamedDirNode workbenchTemplate;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			templates = brjs.sdkTemplateGroup("default");
			workbenchTemplate = templates.template("bladesetworkbench");
			bladeset = app.bladeset("bladeset");
			workbench = bladeset.workbench();
	}
	
	@Test
	public void parentGivesTheCorrectNode() throws Exception {
		given(bladeset).hasBeenCreated();
		then(workbench.parent()).isSameAs(bladeset);
	}
	
	@Test
	public void workbenchTemplateIsPopulatedAsExpected() throws Exception {
		given(templates).templateGroupCreated()
			.and(workbenchTemplate).containsFileWithContents("index.html", "'<html>hello world</html>'")
			.and(workbenchTemplate).containsFolder("resources")
			.and(workbenchTemplate).containsFolder("src");
		when(bladeset).populate("default");
		then(workbench).hasDir("resources")
			.and(workbench).hasDir("src")
			.and(workbench).fileHasContents("index.html", "'<html>hello world</html>'");
	}
}