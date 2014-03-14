

import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class HostedAppTests extends SpecTest
{
	
	private ApplicationServer appServer;

	@Before
	public void initTestObjects() throws Exception {
		given(brjs).hasBeenAuthenticallyCreated()
			.and(brjs).usesProductionTemplates();
		
		// generate the app structure
		App app = brjs.app("app");
		Aspect aspect = app.aspect("default");
		Bladeset bs = app.bladeset("bs");
		Blade b1 = bs.blade("b1");
		Workbench workbench = b1.workbench();
		
		given(app).hasBeenPopulated()
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(b1).hasClass("appns/bs/b1/Class")
			.and(workbench).containsFileWithContents("index.html", "require('appns/bs/b1/Class');");
		
		appServer = brjs.applicationServer(appServerPort);
		appServer.start();
	}
	
	@After
	public void stopServer() throws Exception
	{
		given(brjs.applicationServer(appServerPort)).stopped()
			.and(brjs.applicationServer(appServerPort)).requestTimesOutFor("/");
	}
	
	@Ignore // TODO figure out why this only fails in CI linux but passes on Windows, Mac and Fedora (linux)
	@Test
	public void jsBundleHasCorrectContentType() throws Exception
	{
		String jsBundleUrlPath = "/app/default-aspect/js/dev/en_GB/combined/bundle.js";
		
		then(appServer).requestCanBeMadeFor(jsBundleUrlPath)
			.and(appServer).contentTypeForRequestIs(jsBundleUrlPath, "application/javascript");
	}
	
	@Ignore // TODO figure out why this only fails in CI linux but passes on Windows, Mac and Fedora (linux)
	@Test
	public void weCanMakeARequestForAspectBundles() throws Exception
	{
		String jsBundleUrlPath = "/app/default-aspect/js/dev/en_GB/combined/bundle.js";
		
		then(appServer).requestCanBeMadeFor(jsBundleUrlPath)
			.and(appServer).requestForUrlContains(jsBundleUrlPath, "appns/Class1");
	}
	
	@Ignore // TODO figure out why this only fails in CI linux but passes on Windows, Mac and Fedora (linux)
	@Test
	public void weCanMakeARequestForWorkbenchBundles() throws Exception
	{
		String jsBundleUrlPath = "/app/bs-bladeset/blades/b1/workbench/js/dev/en_GB/combined/bundle.js";
		
		then(appServer).requestCanBeMadeFor(jsBundleUrlPath)
			.and(appServer).requestForUrlContains(jsBundleUrlPath, "appns/bs/b1/Class");
	}
}
