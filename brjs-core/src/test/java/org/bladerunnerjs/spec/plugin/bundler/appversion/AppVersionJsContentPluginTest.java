package org.bladerunnerjs.spec.plugin.bundler.appversion;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AppVersionJsContentPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer requestResponse = new StringBuffer();
	private SdkJsLib bootstrapLib;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bootstrapLib = brjs.sdkLib("br-bootstrap");
	}
	
	@Test
	public void appVersionContentIsIncluded() throws Exception {
		given(brjs).hasDevVersion("dev");
		when(aspect).requestReceived("app-version/version.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"try {\n",
				"  var ServiceRegistry = require( 'br/ServiceRegistry' );\n",
				"  ServiceRegistry.getService('br.app-version.service').setVersion('dev');\n",
				"} catch(e) {" );
	}
	
	@Test
	public void appVersionContentIsIncludedAtTheTopOfTheCompositeBundle() throws Exception {
		given(aspect).indexPageRequires("appns/Class")
			.and(brjs).hasDevVersion("dev")
			.and(aspect).hasClass("appns/Class")
			.and(bootstrapLib).hasBeenCreated()
			.and(bootstrapLib).containsFileWithContents("thirdparty-lib.manifest", "js: bootstrap.js\n"+"exports: lib")
			.and(bootstrapLib).containsFileWithContents("bootstrap.js", "// this is bootstrap");
		when(aspect).requestReceived("js/dev/combined/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"// br-bootstrap",
				"ServiceRegistry.getService('br.app-version.service').setVersion('dev');",
				"appns/Class" );
	}
	
}
