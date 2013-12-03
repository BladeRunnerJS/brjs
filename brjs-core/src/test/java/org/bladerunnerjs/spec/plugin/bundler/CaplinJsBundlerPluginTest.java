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
	
	@Test
	public void thePackageDefinitionsBlockShouldContainSinglePackageIfThereIsOneTopLevelClass() throws Exception {
		given(aspect).hasPackageStyle(CaplinJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("novox.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1");
		when(app).requestReceived("/default-aspect/caplin-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("window.novox = {};");
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldContainSinglePackageIfThereAreTwoTopLevelClasses() throws Exception {
		given(aspect).hasPackageStyle(CaplinJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1")
			.and(aspect).classRefersTo("novox.Class1", "novox.Class2");
		when(app).requestReceived("/default-aspect/caplin-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("window.novox = {};");
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldBeEmptyIfNoneOfTheClassesAreUsed() throws Exception {
		given(aspect).hasPackageStyle(CaplinJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).classRefersTo("novox.Class1", "novox.Class2");
		when(app).requestReceived("/default-aspect/caplin-js/package-definitions.js", requestResponse);
		then(requestResponse).isEmpty();
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldContainTwoPackagesIfThereAreClassesAtDifferentLevels() throws Exception {
		given(aspect).hasPackageStyle(CaplinJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("novox.Class1", "novox.pkg.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1")
			.and(aspect).classRefersTo("novox.Class1", "novox.pkg.Class2");
		when(app).requestReceived("/default-aspect/caplin-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("window.novox = {\"pkg\":{}};");
	}
	
	@Test
	public void eachClassShouldBeReturnedUnchagned() throws Exception {
		given(aspect).hasPackageStyle(CaplinJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("novox.Class1");
		when(app).requestReceived("/default-aspect/caplin-js/module/novox/Class1.js", requestResponse);
		then(requestResponse).textEquals("novox.Class1 = function() {\n};\n");
	}
	
	@Test
	public void caplinStyleClassesThatReferToNonCaplinStyleClassesWillHaveRequiresAutomaticallyAdded() throws Exception {
		given(aspect).hasPackageStyle("src/novox/caplin", CaplinJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("novox.caplin.Class", "novox.nodejs.Class")
			.and(aspect).classRefersTo("novox.caplin.Class", "novox.nodejs.Class");
		when(app).requestReceived("/default-aspect/caplin-js/module/novox/caplin/Class.js", requestResponse);
		then(requestResponse).containsText("novox.caplin.Class = function() {\n};")
			.and(requestResponse).containsText("novox.nodejs.Class = require('novox/nodejs/Class');");
	}
}
