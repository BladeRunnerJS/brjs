package org.bladerunnerjs.spec.plugin.bundler.nodejs;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class NodeJsTagHandlerPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer pageResponse = new StringBuffer();
	
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
		when(app).requestReceived("/default-aspect/node-js/bundle.js", pageResponse);
		then(pageResponse).containsText("// appns/Class2\n" + "define('appns/Class2', function(")
			.and(pageResponse).containsText("// appns/Class1\n" + "define('appns/Class1', function(");
	}
	
	@Test
	public void theBundleIsEmptyIfWeDontReferToAnyOfTheClasses() throws Exception {
		given(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).classRequires("appns.Class1", "appns.Class2")
			.and(aspect).indexPageHasContent("<@node-js@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).isEmpty();
	}
	
}
