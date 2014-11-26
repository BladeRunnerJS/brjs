package org.bladerunnerjs.spec.brjs.appserver;


import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ServedAppBundleTest extends SpecTest
{
	private ApplicationServer appServer;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated()
			.and(brjs).usesProductionTemplates()
			.and(brjs).usedForServletModel();
		
		// generate the app structure
		App app = brjs.app("app");
		Aspect aspect = app.defaultAspect();
		Bladeset bs = app.bladeset("bs");
		Blade b1 = bs.blade("b1");
		Workbench workbench = b1.workbench();
		
		given(app).hasBeenPopulated("default")
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(b1).hasClass("appns/bs/b1/Class")
			.and(workbench).containsFileWithContents("index.html", "require('appns/bs/b1/Class');");
		
		brjs.appJars().create();
		
		appServer = brjs.applicationServer(appServerPort);
		appServer.start();
	}
	
	@After
	public void stopServer() throws Exception
	{
		given(appServer).stopped()
			.and(appServer).requestTimesOutFor("/");
	}
	
	@Test
	public void weCanMakeARequestForAspectBundles() throws Exception
	{
		String jsBundleUrlPath = "/app/v/dev/js/dev/combined/bundle.js";
		
		then(appServer).requestCanBeMadeFor(jsBundleUrlPath)
			.and(appServer).requestForUrlContains(jsBundleUrlPath, "appns/Class1");
	}
	
	@Test
	public void weCanMakeARequestForWorkbenchBundles() throws Exception
	{
		String jsBundleUrlPath = "/app/bs/b1/workbench/v/dev/js/dev/combined/bundle.js";
		
		then(appServer).requestCanBeMadeFor(jsBundleUrlPath)
			.and(appServer).requestForUrlContains(jsBundleUrlPath, "appns/bs/b1/Class");
	}
}
