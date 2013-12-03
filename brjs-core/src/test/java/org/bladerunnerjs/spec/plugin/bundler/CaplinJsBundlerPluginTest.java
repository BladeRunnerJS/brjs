package org.bladerunnerjs.spec.plugin.bundler;

import org.bladerunnerjs.core.plugin.bundlesource.js.CaplinJsBundlerPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class CaplinJsBundlerPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer pageResponse = new StringBuffer();
	
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
		given(aspect).hasPackageStyle(CaplinJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1")
			.and(aspect).classRefersTo("novox.Class1", "novox.Class2")
			.and(aspect).indexPageHasContent("<@caplin-js@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("caplin-js/package-definitions.js", "caplin-js/module/novox/Class1.js", "caplin-js/module/novox/Class2.js");
	}
	
	@Test
	public void inProdASingleBundleRequestIsGenerated() throws Exception {
		given(aspect).hasPackageStyle(CaplinJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1")
			.and(aspect).classRefersTo("novox.Class1", "novox.Class2")
			.and(aspect).indexPageHasContent("<@caplin-js@/>");
		when(aspect).indexPageLoadedInProd(pageResponse, "en_GB");
		then(pageResponse).containsRequests("caplin-js/bundle.js");
	}
}
