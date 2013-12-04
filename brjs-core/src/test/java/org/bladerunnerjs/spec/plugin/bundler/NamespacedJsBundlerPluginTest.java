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
			.and(aspect).indexPageHasContent("<@namespaced-js@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("namespaced-js/package-definitions.js", "namespaced-js/module/mypkg/Class1.js", "namespaced-js/module/mypkg/Class2.js");
	}
	
	@Test
	public void inProdASingleBundleRequestIsGenerated() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "mypkg.Class2")
			.and(aspect).indexPageHasContent("<@namespaced-js@/>");
		when(aspect).indexPageLoadedInProd(pageResponse, "en_GB");
		then(pageResponse).containsRequests("namespaced-js/bundle.js");
	}
	
	@Test
	public void theBundleIsEmptyIfWeDontReferToAnyOfTheClasses() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).classRefersTo("mypkg.Class1", "mypkg.Class2");
		when(app).requestReceived("/default-aspect/namespaced-js/bundle.js", requestResponse);
		then(requestResponse).isEmpty();
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldContainSinglePackageIfThereIsOneTopLevelClass() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1");
		when(app).requestReceived("/default-aspect/namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("window.mypkg = {};");
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldContainSinglePackageIfThereAreTwoTopLevelClasses() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "mypkg.Class2");
		when(app).requestReceived("/default-aspect/namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("window.mypkg = {};");
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldBeEmptyIfNoneOfTheClassesAreUsed() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).classRefersTo("mypkg.Class1", "mypkg.Class2");
		when(app).requestReceived("/default-aspect/namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).isEmpty();
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldContainTwoPackagesIfThereAreClassesAtDifferentLevels() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1", "mypkg.pkg.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "mypkg.pkg.Class2");
		when(app).requestReceived("/default-aspect/namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("window.mypkg = {\"pkg\":{}};");
	}
	
	@Test
	public void eachClassShouldBeReturnedUnchagned() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1");
		when(app).requestReceived("/default-aspect/namespaced-js/module/mypkg/Class1.js", requestResponse);
		then(requestResponse).textEquals("mypkg.Class1 = function() {\n};\n");
	}
	
	@Test
	public void caplinStyleClassesThatReferToNonCaplinStyleClassesWillHaveRequiresAutomaticallyAdded() throws Exception {
		given(aspect).hasPackageStyle("src/mypkg/namespaced", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.namespaced.Class", "mypkg.nodejs.Class")
			.and(aspect).classRefersTo("mypkg.namespaced.Class", "mypkg.nodejs.Class");
		when(app).requestReceived("/default-aspect/namespaced-js/module/mypkg/namespaced/Class.js", requestResponse);
		then(requestResponse).containsText("mypkg.namespaced.Class = function() {\n};")
			.and(requestResponse).containsText("mypkg.nodejs.Class = require('mypkg/nodejs/Class');");
	}
	
	@Test
	public void requiresAreAlsoAutomaticallyAddedWithinTheBundledResponse() throws Exception {
		given(aspect).hasPackageStyle("src/mypkg/namespaced", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.namespaced.Class", "mypkg.nodejs.Class")
			.and(aspect).indexPageRefersTo("mypkg.namespaced.Class")
			.and(aspect).classRefersTo("mypkg.namespaced.Class", "mypkg.nodejs.Class");
		when(app).requestReceived("/default-aspect/namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsText("mypkg.namespaced.Class = function() {\n};")
			.and(requestResponse).containsText("mypkg.nodejs.Class = require('mypkg/nodejs/Class');");
	}
	
	@Test
	public void staticDependenciesAppearFirstEvenWhenTheyAreDiscoveredLast() throws Exception {
		given(aspect).hasPackageStyle(NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1")
			.and(aspect).classDependsOn("mypkg.Class1", "mypkg.Class2") // TODO: switch to classDependsOn()
			.and(aspect).indexPageHasContent("<@namespaced-js@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("namespaced-js/package-definitions.js", "namespaced-js/module/mypkg/Class2.js", "namespaced-js/module/mypkg/Class1.js"); // TODO: enforce ordering
	}
}
