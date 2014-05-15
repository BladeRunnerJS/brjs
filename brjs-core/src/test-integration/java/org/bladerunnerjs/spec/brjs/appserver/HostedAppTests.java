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


public class HostedAppTests extends SpecTest
{
	
	private ApplicationServer appServer;

	@Before
	public void initTestObjects() throws Exception {
		given(brjs).hasBeenAuthenticallyCreated()
			.and(brjs).usesProductionTemplates();
		
		// TODO: find out why we need to sleep at least as long as the FileModificationService (it polls every 100ms) or only
		// the first test in the test in the suite reliably works -- it's something to do with the flurry of directory activity
		// caused by copying the production templates in the line above, that must happen prior to the server being started
		// at the end of this method
		Thread.sleep(125);
		
		// generate the app structure
		App app = brjs.app("app");
		Aspect aspect = app.aspect("default");
		Bladeset bs = app.bladeset("bs");
		Blade b1 = bs.blade("b1");
		Workbench workbench = b1.workbench();
		
		given(app).hasBeenPopulated()
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(b1).hasClass("appns/bs/b1/Class")
			.and(workbench).containsFileWithContents("index.html", "require('appns/bs/b1/Class');");
		
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
	public void jsBundleHasCorrectContentType() throws Exception
	{
		String jsBundleUrlPath = "/app/v/dev/js/dev/combined/bundle.js";
		
		then(appServer).requestCanBeMadeFor(jsBundleUrlPath)
			.and(appServer).contentTypeForRequestIs(jsBundleUrlPath, "application/javascript");
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
		String jsBundleUrlPath = "/app/workbench/bs/b1/v/dev/js/dev/combined/bundle.js";
		
		then(appServer).requestCanBeMadeFor(jsBundleUrlPath)
			.and(appServer).requestForUrlContains(jsBundleUrlPath, "appns/bs/b1/Class");
	}
}
