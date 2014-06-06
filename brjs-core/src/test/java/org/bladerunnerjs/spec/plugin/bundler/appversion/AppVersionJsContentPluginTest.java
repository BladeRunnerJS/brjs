package org.bladerunnerjs.spec.plugin.bundler.appversion;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AppVersionJsContentPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer requestResponse = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	@Test @Ignore
	public void appVersionContentIsIncluded() throws Exception {
		//TODO: set app version
		when(aspect).requestReceived("app-version.js", requestResponse);
		then(requestResponse).containsOrderedTextFragmentsAnyNumberOfTimes(
				"var ServiceRegistry = require( 'br/ServiceRegistry' );",
				"ServiceRegistry.getService('br.app-version.service').setVersion('123');" );
	}
	
	@Test @Ignore
	public void appVersionContentIsIncludedAtTheTopOfTheCompositeBundle() throws Exception {
		//TODO: set app version
		given(aspect).indexPageRequires("appns/Class")
			.and(aspect).hasClass("appns/Class");
		when(aspect).requestReceived("js/dev/combined/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"ServiceRegistry.getService('br.app-version.service').setVersion('123');",
				"appns/Class" );
	}
	
}
