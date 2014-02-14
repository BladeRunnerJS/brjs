package org.bladerunnerjs.spec.plugin.bundler.nodejs;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class NodeJsBundlerPluginTest extends SpecTest {
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
	
	@Test
	public void classesAreAutomaticallyWrappedInAClosure() throws Exception {
		given(aspect).hasClasses("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/node-js/module/appns/Class1.js", requestResponse);
		then(requestResponse).containsLines(
			"define('appns/Class1', function(require, exports, module) {",
			"appns.Class1 = function() {",
			"};",
			"module.exports = appns.Class1;",
			"\n});");
	}
	
	@Test
	public void jsPatchesAreIncludedInTheClosure() throws Exception {
		given(aspect).hasClasses("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(brjs).containsFileWithContents("js-patches/appns/Class1.js", "appns.Class1.patch = function() {}");
		when(app).requestReceived("/default-aspect/node-js/module/appns/Class1.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
			"define('appns/Class1', function(require, exports, module) {",
			"appns.Class1 = function() {",
			"};",
			"module.exports = appns.Class1;",
			"appns.Class1.patch = function() {}",
			"\n});");
	}
	
}
