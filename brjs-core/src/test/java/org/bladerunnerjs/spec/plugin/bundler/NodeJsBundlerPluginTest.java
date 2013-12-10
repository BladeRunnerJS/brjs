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
		given(aspect).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1")
			.and(aspect).classRequires("mypkg.Class1", "mypkg.Class2")
			.and(aspect).indexPageHasContent("<@node-js@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("node-js/module/mypkg/Class1.js", "node-js/module/mypkg/Class2.js");
	}
	
	@Test
	public void inProdASingleBundleRequestIsGenerated() throws Exception {
		given(aspect).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1")
			.and(aspect).classRequires("mypkg.Class1", "mypkg.Class2")
			.and(aspect).indexPageHasContent("<@node-js@/>");
		when(aspect).indexPageLoadedInProd(pageResponse, "en_GB");
		then(pageResponse).containsRequests("node-js/bundle.js");
	}

	@Test
	public void appendsCommentToTheTopOfRequiredClassesWhenNodeJsStyleIsRequested() throws Exception {
		given(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1")
			.and(aspect).classRequires("novox.Class1", "novox.Class2")
			.and(aspect).indexPageHasContent("<@node-js@/>");
		when(app).requestReceived("/default-aspect/node-js/bundle.js", requestResponse);
		then(requestResponse).containsText("// /novox/Class2\n" + "define('/novox/Class2', function(")
			.and(requestResponse).containsText("// /novox/Class1\n" + "define('/novox/Class1', function(");
	}
	
	@Test
	public void theBundleIsEmptyIfWeDontReferToAnyOfTheClasses() throws Exception {
		given(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1")
			.and(aspect).classRequires("novox.Class1", "novox.Class2")
			.and(aspect).indexPageHasContent("<@node-js@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(requestResponse).isEmpty();
	}
	
	@Test
	public void classesAreAutomaticallyWrappedInAClosure() throws Exception {
		given(aspect).hasClasses("Class1")
			.and(aspect).indexPageRefersTo("Class1");
		when(app).requestReceived("/default-aspect/node-js/module/Class1.js", requestResponse);
		then(requestResponse).containsLines(
			"define('/Class1', function(require, exports, module) {",
			"Class1 = function() {",
			"};",
			"module.exports = Class1;",
			"});");
	}
}
