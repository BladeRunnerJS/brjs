package org.bladerunnerjs.spec.bundling;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;

public class WorkbenchBundlingTest extends SpecTest {
	private App app;
	@SuppressWarnings("unused")
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	@SuppressWarnings("unused")
	private Workbench workbench;
	private NamedDirNode workbenchTemplate;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();

		app = brjs.app("app1");
		workbenchTemplate = brjs.template("workbench");
		bladeset = app.bladeset("bladeset");
		blade = bladeset.blade("b1");
		workbench = blade.workbench();

		given(workbenchTemplate).containsFileWithContents("index.html", "'<html>hello world</html>'")
			.and(workbenchTemplate).containsFolder("resources")
			.and(workbenchTemplate).containsFolder("src");
	}

	// TODO add aspect-level css test 
}