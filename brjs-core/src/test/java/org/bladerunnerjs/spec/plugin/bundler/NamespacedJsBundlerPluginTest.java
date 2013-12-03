package org.bladerunnerjs.spec.plugin.bundler;

import org.bladerunnerjs.core.plugin.bundlesource.js.NamespacedJsBundlerPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class NamespacedJsBundlerPluginTest extends SpecTest {
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
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "mypkg.Class2")
			.and(aspect).indexPageHasContent("<@caplin-js@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("caplin-js/package-definitions.js", "caplin-js/module/mypkg/Class1.js", "caplin-js/module/mypkg/Class2.js");
	}
	
	@Test
	public void inProdASingleBundleRequestIsGenerated() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "mypkg.Class2")
			.and(aspect).indexPageHasContent("<@caplin-js@/>");
		when(aspect).indexPageLoadedInProd(pageResponse, "en_GB");
		then(pageResponse).containsRequests("caplin-js/bundle.js");
	}
	
	@Test
	public void theBundleIsEmptyIfWeDontReferToAnyOfTheClasses() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).classRefersTo("mypkg.Class1", "mypkg.Class2");
		when(app).requestReceived("/default-aspect/caplin-js/bundle.js", requestResponse);
		then(requestResponse).isEmpty();
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldContainSinglePackageIfThereIsOneTopLevelClass() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1");
		when(app).requestReceived("/default-aspect/caplin-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("window.mypkg = {};");
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldContainSinglePackageIfThereAreTwoTopLevelClasses() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "mypkg.Class2");
		when(app).requestReceived("/default-aspect/caplin-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("window.mypkg = {};");
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldBeEmptyIfNoneOfTheClassesAreUsed() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).classRefersTo("mypkg.Class1", "mypkg.Class2");
		when(app).requestReceived("/default-aspect/caplin-js/package-definitions.js", requestResponse);
		then(requestResponse).isEmpty();
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldContainTwoPackagesIfThereAreClassesAtDifferentLevels() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1", "mypkg.pkg.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "mypkg.pkg.Class2");
		when(app).requestReceived("/default-aspect/caplin-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("window.mypkg = {\"pkg\":{}};");
	}
	
	@Test
	public void eachClassShouldBeReturnedUnchagned() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1");
		when(app).requestReceived("/default-aspect/caplin-js/module/mypkg/Class1.js", requestResponse);
		then(requestResponse).textEquals("mypkg.Class1 = function() {\n};\n");
	}
	
	@Test
	public void caplinStyleClassesThatReferToNonCaplinStyleClassesWillHaveRequiresAutomaticallyAdded() throws Exception {
		given(aspect).hasPackageStyle("src/mypkg/caplin", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.caplin.Class", "mypkg.nodejs.Class")
			.and(aspect).classRefersTo("mypkg.caplin.Class", "mypkg.nodejs.Class");
		when(app).requestReceived("/default-aspect/caplin-js/module/mypkg/caplin/Class.js", requestResponse);
		then(requestResponse).containsText("mypkg.caplin.Class = function() {\n};")
			.and(requestResponse).containsText("mypkg.nodejs.Class = require('mypkg/nodejs/Class');");
	}
	
	@Test
	public void requiresAreAlsoAutomaticallyAddedWithinTheBundledResponse() throws Exception {
		given(aspect).hasPackageStyle("src/mypkg/caplin", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.caplin.Class", "mypkg.nodejs.Class")
			.and(aspect).indexPageRefersTo("mypkg.caplin.Class")
			.and(aspect).classRefersTo("mypkg.caplin.Class", "mypkg.nodejs.Class");
		when(app).requestReceived("/default-aspect/caplin-js/bundle.js", requestResponse);
		then(requestResponse).containsText("mypkg.caplin.Class = function() {\n};")
			.and(requestResponse).containsText("mypkg.nodejs.Class = require('mypkg/nodejs/Class');");
	}
}
