package org.bladerunnerjs.spec.brjs.appserver;

import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class HostedAppTests extends SpecTest
{
	
	private ApplicationServer appServer;

	@Before
	public void initTestObjects() throws Exception {
		given(brjs).hasBeenAuthenticallyCreated();
    	
		App app = brjs.app("app");
    	Aspect aspect = app.aspect("default");	
    	
    	given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
    		
		appServer = brjs.applicationServer(appServerPort);
		appServer.start();
	}
	
	@After
	public void stopServer() throws Exception
	{
		given(brjs.applicationServer(appServerPort)).stopped()
			.and(brjs.applicationServer(appServerPort)).requestTimesOutFor("/");
	}
	
	@Test
	public void jsBundleHasCorrectContentType() throws Exception
	{
		String jsBundleUrlPath = "/app/default-aspect/js/dev/en_GB/combined/bundle.js";
		
		then(appServer).requestCanBeMadeFor(jsBundleUrlPath)
			.and(appServer).contentTypeForRequestIs(jsBundleUrlPath, "application/javascript");
	}
	
}
