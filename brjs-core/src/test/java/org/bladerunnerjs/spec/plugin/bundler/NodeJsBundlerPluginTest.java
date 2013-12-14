package org.bladerunnerjs.spec.plugin.bundler;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class NodeJsBundlerPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer pageResponse = new StringBuffer();
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
	public void inDevSeparateJsFileRequestsAreGenerated() throws Exception {
		given(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).classRequires("appns.Class1", "appns.Class2")
			.and(aspect).indexPageHasContent("<@node-js@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("node-js/module/appns/Class1.js", "node-js/module/appns/Class2.js");
	}
	
	@Test
	public void inProdASingleBundleRequestIsGenerated() throws Exception {
		given(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).classRequires("appns.Class1", "appns.Class2")
			.and(aspect).indexPageHasContent("<@node-js@/>");
		when(aspect).indexPageLoadedInProd(pageResponse, "en_GB");
		then(pageResponse).containsRequests("node-js/bundle.js");
	}

	@Test
	public void appendsCommentToTheTopOfRequiredClassesWhenNodeJsStyleIsRequested() throws Exception {
		given(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).classRequires("appns.Class1", "appns.Class2")
			.and(aspect).indexPageHasContent("<@node-js@/>");
		when(app).requestReceived("/default-aspect/node-js/bundle.js", requestResponse);
		then(requestResponse).containsText("// appns/Class2\n" + "define('appns/Class2', function(")
			.and(requestResponse).containsText("// appns/Class1\n" + "define('appns/Class1', function(");
	}
	
	@Test
	public void theBundleIsEmptyIfWeDontReferToAnyOfTheClasses() throws Exception {
		given(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).classRequires("appns.Class1", "appns.Class2")
			.and(aspect).indexPageHasContent("<@node-js@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(requestResponse).isEmpty();
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
			"});");
	}
}
