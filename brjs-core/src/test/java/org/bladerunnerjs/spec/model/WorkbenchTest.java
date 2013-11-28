package org.bladerunnerjs.spec.model;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class WorkbenchTest extends SpecTest {
	private App app;
	private Bladeset bladeset;
	private Blade blade;
	private Workbench workbench;
	private NamedDirNode workbenchTemplate;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			app = brjs.app("app1");
			workbenchTemplate = brjs.template("workbench");
			bladeset = app.bladeset("bladeset");
			blade = bladeset.blade("b1");
			workbench = blade.workbench();
	}
	
	@Test
	public void parentGivesTheCorrectNode() throws Exception {
		given(blade).hasBeenCreated();
		then(workbench.parent()).isSameAs(blade);
	}
	
	@Test
	public void workbenchTemplateIsPopulatedAsExpected() throws Exception {
		given(workbenchTemplate).containsFileWithContents("index.html", "'<html>hello world</html>'")
			.and(workbenchTemplate).containsFolder("resources")
			.and(workbenchTemplate).containsFolder("src");
		when(blade).populate();
		then(workbench).hasDir("resources")
			.and(workbench).hasDir("src")
			.and(workbench).fileHasContents("index.html", "'<html>hello world</html>'");
	}
}