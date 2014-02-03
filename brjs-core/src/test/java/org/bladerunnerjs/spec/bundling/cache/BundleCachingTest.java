package org.bladerunnerjs.spec.bundling.cache;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class BundleCachingTest extends SpecTest 
{
	private App app;
	private Aspect aspect;
	private JsLib thirdpartyLib;

	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			
			thirdpartyLib = brjs.sdkNonBladeRunnerLib("thirdpartyLib");
	}
	
	// Cache tests should be irrespective of JS style (namespace/node)
	
	@Test
	public void weDoNotCacheIndexPageReferencesToSourceClasses() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).hasClass("appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(app).hasReceivedRequst("/default-aspect/js/dev/en_GB/combined/bundle.js");
		when(aspect).indexPageRefersTo("appns.Class2")
			.and(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class2")
			.and(response).doesNotContainText("appns.Class1");
	}
	
	@Test
	public void weDoNotCacheAspectSourceDependencies() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(app).hasReceivedRequst("/default-aspect/js/dev/en_GB/combined/bundle.js");
		when(thirdpartyLib).populate()
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "js: file1.js")
			.and(thirdpartyLib).containsFileWithContents("file1.js", "thirdpartyLib content")
			.and(aspect).classRefersToThirdpartyLib("appns.Class1", thirdpartyLib)
			.and(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText(
				"// thirdpartyLib", 
				"thirdpartyLib content", 
				"window.appns = {}", 
				"appns.Class1 = function()",
				"module.exports = appns.Class1",
				"define('appns/Class1', function(require, exports, module)");
	}
	
}
