package org.bladerunnerjs.spec.model;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.BladesetWorkbench;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class BladesetWorkbenchTest extends SpecTest {
	private App app;
	private Bladeset bladeset;
	private BladesetWorkbench workbench;
	private NamedDirNode workbenchTemplate;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			workbenchTemplate = brjs.template("bladesetworkbench");
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
		given(workbenchTemplate).containsFileWithContents("index.html", "'<html>hello world</html>'")
			.and(workbenchTemplate).containsFolder("resources")
			.and(workbenchTemplate).containsFolder("src");
		when(bladeset).populate();
		then(workbench).hasDir("resources")
			.and(workbench).hasDir("src")
			.and(workbench).fileHasContents("index.html", "'<html>hello world</html>'");
	}
}