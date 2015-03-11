package org.bladerunnerjs.spec.model;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.api.BladeWorkbench;
import org.junit.Before;
import org.junit.Test;

public class BladeWorkbenchTest extends SpecTest {
	private App app;
	private Bladeset bladeset;
	private Blade blade;
	private BladeWorkbench workbench;
	private NamedDirNode workbenchTemplate;
	private Bladeset defaultBladeset;
	private Blade bladeInDefaultBladeset;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			workbenchTemplate = brjs.sdkTemplateGroup("default").template("bladeworkbench");
			bladeset = app.bladeset("bladeset");
			blade = bladeset.blade("b1");
			workbench = blade.workbench();
			defaultBladeset = app.defaultBladeset();
			bladeInDefaultBladeset = defaultBladeset.blade("b1");
	}
	
	@Test
	public void parentGivesTheCorrectNode() throws Exception {
		given(blade).hasBeenCreated();
		then(workbench.parent()).isSameAs(blade);
	}
	
	@Test
	public void workbenchTemplateIsPopulatedAsExpected() throws Exception {
		given(brjs.sdkTemplateGroup("default")).templateGroupCreated()
			.and(workbenchTemplate).containsFolder("resources")
			.and(workbenchTemplate).containsFolder("src")
			.and(workbenchTemplate).containsFileWithContents("index.html", "'<html>hello world</html>'");
		when(blade).populate("default");
		then(workbench).hasDir("resources")
			.and(workbench).hasDir("src")
			.and(workbench).fileHasContents("index.html", "'<html>hello world</html>'");
	}
	
	@Test
	public void bundleCanBeGeneratedForABladeInADefaultBladeset() throws Exception {
		given(bladeInDefaultBladeset).hasClasses("Class1")
    		.and( bladeInDefaultBladeset.workbench() ).indexPageRequires("appns/b1/Class1");
		when( bladeInDefaultBladeset.workbench() ).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(response).containsCommonJsClasses("appns/b1/Class1");
	}
	
	@Test
	public void bundleCanBeGeneratedForABladeInADefaultBladesetThatAlsoHasADefaultAspect() throws Exception {
		given(bladeInDefaultBladeset).hasClasses("Class1")
			.and( app.defaultAspect() ).indexPageHasContent("")
    		.and( bladeInDefaultBladeset.workbench() ).indexPageRequires("appns/b1/Class1");
		when( bladeInDefaultBladeset.workbench() ).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(response).containsCommonJsClasses("appns/b1/Class1");
	}
	
}