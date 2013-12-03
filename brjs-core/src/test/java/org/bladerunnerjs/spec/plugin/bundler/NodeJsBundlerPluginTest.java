package org.bladerunnerjs.spec.plugin.bundler;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.specutil.engine.SpecTest;
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
			.and(brjs).automaticallyFindsTagHandlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	@Test
	public void inDevSeparateJsFileRequestsAreGenerated() throws Exception {
		given(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1")
			.and(aspect).classRequires("novox.Class1", "novox.Class2")
			.and(aspect).indexPageHasContent("<@node-js@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("node-js/module/novox/Class1.js", "node-js/module/novox/Class2.js");
	}
	
	@Test
	public void inProdASingleBundleRequestIsGenerated() throws Exception {
		given(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1")
			.and(aspect).classRequires("novox.Class1", "novox.Class2")
			.and(aspect).indexPageHasContent("<@node-js@/>");
		when(aspect).indexPageLoadedInProd(pageResponse, "en_GB");
		then(pageResponse).containsRequests("node-js/bundle.js");
	}

	@Test
	public void requiredDependenciesAreAddedToTheResponseWhenNodeJsStyleIsRequested() throws Exception {
		given(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1")
			.and(aspect).classRequires("novox.Class1", "novox.Class2")
			.and(aspect).indexPageHasContent("<@node-js@/>");
		when(app).requestReceived("/default-aspect/node-js/bundle.js", requestResponse);
		then(requestResponse).textEquals(
				"// novox/Class2\n" +
				"novox.Class2 = function() {\n};\n\n\n" +
				"// novox/Class1\n" +
				"var Class1 = require('novox/Class2');\n" +
				"novox.Class1 = function() {\n};\n\n\n");
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
}
