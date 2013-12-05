package org.bladerunnerjs.spec.bundling;

import static org.bladerunnerjs.model.utility.LogicalRequestHandler.Messages.*;
import static org.bladerunnerjs.model.BundleSetCreator.Messages.*;

import org.bladerunnerjs.core.plugin.bundlesource.js.NamespacedJsBundlerPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'mypkg' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class BundlingTest extends SpecTest {
	private App app;
	private AppConf appConf;
	private Aspect aspect;
	private Theme standardAspectTheme, standardBladesetTheme, standardBladeTheme;
	private AliasesFile aspectAliasesFile;
	private Bladeset bladeset;
	private Blade blade;
	private JsLib sdkJsLib, userLib, appLegacyThirdparty, sdkLegacyThirdparty, sdkLegacyThirdparty2;
	private AliasDefinitionsFile bladeAliasDefinitionsFile;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			appConf = app.appConf();
			aspect = app.aspect("default");
			standardAspectTheme = aspect.theme("standard");
			aspectAliasesFile = aspect.aliasesFile();
			bladeset = app.bladeset("bs");
			standardBladesetTheme = bladeset.theme("standard");
			blade = bladeset.blade("b1");
			standardBladeTheme = blade.theme("theme");
			
			appLegacyThirdparty = app.nonBladeRunnerLib("app-legacy-thirdparty");
			userLib = app.jsLib("userLib");
			sdkJsLib = brjs.sdkLib();
			sdkLegacyThirdparty = brjs.sdkNonBladeRunnerLib("legacy-thirdparty");
			sdkLegacyThirdparty2 = brjs.sdkNonBladeRunnerLib("legacy-thirdparty2");
			
			bladeAliasDefinitionsFile = blade.src().aliasDefinitionsFile();
	}
	
	// -------------------------------- A S P E C T --------------------------------------
	@Test
	public void weBundleAnAspectClassIfItIsReferredToInTheIndexPage() throws Exception {
		given(aspect).hasClass("mypkg.Class1")
			.and(aspect).indexPageRefersTo("mypkg.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.Class1");
	}
	
	@Test
	public void weBundleAClassIfItsAliasIsReferredToInTheIndexPage() throws Exception {
		given(aspect).hasClass("mypkg.Class1")
			.and(aspectAliasesFile).hasAlias("the-alias", "mypkg.Class1")
			.and(aspect).indexPageRefersTo("the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.Class1");
	}
	
	@Test
	public void weAlsoBundleAClassIfTheAliasIsDefinedInABladeAliasDefinitionsXml() throws Exception {
		given(appConf).hasNamespace("mypkg")
			.and(aspect).hasClass("mypkg.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("mypkg.bs.b1.the-alias", "mypkg.Class1")
			.and(aspect).indexPageRefersTo("mypkg.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.Class1");
	}
	
	@Test
	public void requirePathsCanBeRelative() throws Exception {
		given(aspect).hasClass("mypkg.Class1")
			.and(aspect).hasClass("mypkg.Class2")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).classRequires("mypkg.Class1", "./Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.Class1", "mypkg.Class2");
	}
	
	@Test
	public void requireCallCanHaveSingleQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/pkg/Class1.js", "pkg.Class1 = function(){}; require('pkg/Class2')")
			.and(aspect).containsFileWithContents("src/pkg/Class2.js", "pkg.Class2 = function(){};")
			.and(aspect).indexPageRefersTo("pkg/Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("pkg.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveDoubleQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/pkg/Class1.js", "pkg.Class1 = function(){}; require(\"pkg/Class2\")")
		.and(aspect).containsFileWithContents("src/pkg/Class2.js", "pkg.Class2 = function(){};")
		.and(aspect).indexPageRefersTo("pkg/Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("pkg.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveSpacesBeforeQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/pkg/Class1.js", "pkg.Class1 = function(){}; require( \"pkg/Class2\")")
		.and(aspect).containsFileWithContents("src/pkg/Class2.js", "pkg.Class2 = function(){};")
		.and(aspect).indexPageRefersTo("pkg/Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("pkg.Class2 = function(){};");
	}
	
	@Test
	public void weBundleFilesRequiredFromAnAspect() throws Exception {
		given(aspect).containsFileWithContents("src/pkg/App.js", "var App = function() {};  module.exports = App;")
			.and(aspect).indexPageHasContent("var App = require('pkg/App')");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("var App = function() {};  module.exports = App;");
	}
	
	//  ----------------------------- B L A D E S E T  -----------------------------------
	@Test
	public void weBundleABladesetClassIfItIsReferredToInTheIndexPage() throws Exception {
		given(bladeset).hasClass("mypkg.bs.Class1")
			.and(aspect).indexPageRefersTo("mypkg.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.bs.Class1");
	}
	
	@Test
	public void weBundleImplicitTransitiveDependenciesFromABladeset() throws Exception {
		given(bladeset).hasPackageStyle("src/mypkg", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(bladeset).hasClasses("mypkg.bs.Class1", "mypkg.bs.Class2")
			.and(bladeset).classRefersTo("mypkg.bs.Class1", "mypkg.bs.Class2")
			.and(aspect).indexPageRefersTo("mypkg.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.bs.Class1", "mypkg.bs.Class2");
	}
	
	@Test
	public void weBundleExplicitTransitiveDependenciesForFromABladeset() throws Exception {
		given(bladeset).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(bladeset).classRequires("mypkg.Class1", "mypkg.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.Class1", "mypkg.Class2");
	}
	
	@Test	// bladeset unhappy paths
	public void weDontBundleABladesetClassIfItIsNotReferredToByAnAspect() throws Exception {
		given(bladeset).hasPackageStyle("src/mypkg/bs", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(bladeset).hasClasses("mypkg.bs.Class1", "mypkg.bs.Class2")
			.and(bladeset).classRefersTo("mypkg.bs.Class1", "mypkg.bs.Class2")
			.and(aspect).indexPageRefersTo("mypkg.bs.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.bs.Class2")
			.and(response).doesNotContainClasses("mypkg.bs.Class1");
	}
	
	@Test
	public void bladesetClassesCanOnlyDependOnExistentClasses() throws Exception {
		given(bladeset).hasClass("mypkg.Class1")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(bladeset).classRequires("mypkg.Class1", "mypkg.NonExistentClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "mypkg/NonExistentClass")
			.whereTopLevelExceptionIs(BundlerProcessingException.class);
	}
	
	@Test
	public void bladesetClassesThatReferToNonExistentClassesWontCauseAnExceptionWhenAspectIsRequested() throws Exception {
		given(bladeset).hasPackageStyle("src/mypkg/bs", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(bladeset).hasClass("mypkg.bs.Class1")
			.and(aspect).indexPageRefersTo("mypkg.bs.Class1")
			.and(bladeset).classRefersTo("mypkg.bs.Class1", "mypkg.bs.NonExistentClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	// ----------------------------------- B L A D E -------------------------------------------
	@Test
	public void weBundleABladeClassIfItIsReferredToInTheIndexPage() throws Exception {
		given(blade).hasClass("mypkg.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("mypkg.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.bs.b1.Class1");
	}
	
	@Test
	public void weBundleImplicitTransitiveDependenciesFromABlade() throws Exception {
		given(blade).hasPackageStyle("src/mypkg/bs", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(blade).hasClasses("mypkg.bs.Class1", "mypkg.bs.Class2")
			.and(blade).classRefersTo("mypkg.bs.Class1", "mypkg.bs.Class2")
			.and(aspect).indexPageRefersTo("mypkg.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.bs.Class1", "mypkg.bs.Class2");
	}
	
	@Test
	public void weBundleExplicitTransitiveDependenciesForFromABlade() throws Exception {
		given(blade).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(blade).classRequires("mypkg.Class1", "mypkg.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.Class1", "mypkg.Class2");
	}
	
	@Test
	public void weBundleImplicitTransitiveDependenciesFromABladeIncludingBladesetDependencies() throws Exception {	
		given(bladeset).hasPackageStyle("src/mypkg/bs", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(bladeset).hasClasses("mypkg.bs.Class1", "mypkg.bs.Class2")
			.and(bladeset).classRefersTo("mypkg.bs.Class1", "mypkg.bs.Class2")
			.and(blade).hasClass("mypkg.bs.b1.Class1")
			.and(blade).hasPackageStyle("src/mypkg/bs/b1", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(blade).classRefersTo("mypkg.bs.b1.Class1", "mypkg.bs.Class1")
			.and(aspect).indexPageRefersTo("mypkg.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.bs.Class1", "mypkg.bs.b1.Class1");
	}
	
	@Test
	public void weBundleExplicitTransitiveDependenciesFromABladeIncludingBladesetDependencies() throws Exception {	
		given(bladeset).hasClasses("mypkg.bs.Class1", "mypkg.bs.Class2")
			.and(bladeset).classRequires("mypkg.bs.Class1", "mypkg.bs.Class2")
			.and(blade).hasClass("mypkg.bs.b1.Class1")
			.and(blade).hasPackageStyle("src/mypkg/bs/b1", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(blade).classRefersTo("mypkg.bs.b1.Class1", "mypkg.bs.Class1")
			.and(aspect).indexPageRefersTo("mypkg.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.bs.Class1", "mypkg.bs.b1.Class1");
	}
	
	@Test
	public void devRequestsContainThePackageDefinitionsAtTheTop() throws Exception {
		given(bladeset).hasClasses("mypkg.bs.Class1", "mypkg.bs.Class2")
    		.and(bladeset).classRequires("mypkg.bs.Class1", "mypkg.bs.Class2")
    		.and(blade).hasClass("mypkg.bs.b1.Class1")
    		.and(blade).hasPackageStyle("src/mypkg/bs/b1", NamespacedJsBundlerPlugin.JS_STYLE)
    		.and(blade).classRefersTo("mypkg.bs.b1.Class1", "mypkg.bs.Class1")
    		.and(aspect).indexPageRefersTo("mypkg.bs.b1.Class1");
    	when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.mypkg = {\"bs\":{\"b1\":{}}};");
	}
	
	@Test
	public void prodRequestsContainThePackageDefinitionsAtTheTop() throws Exception {
		given(bladeset).hasClasses("mypkg.bs.Class1", "mypkg.bs.Class2")
    		.and(bladeset).classRequires("mypkg.bs.Class1", "mypkg.bs.Class2")
    		.and(blade).hasClass("mypkg.bs.b1.Class1")
    		.and(blade).hasPackageStyle("src/mypkg/bs/b1", NamespacedJsBundlerPlugin.JS_STYLE)
    		.and(blade).classRefersTo("mypkg.bs.b1.Class1", "mypkg.bs.Class1")
    		.and(aspect).indexPageRefersTo("mypkg.bs.b1.Class1");
    	when(app).requestReceived("/default-aspect/js/prod/en_GB/combined/bundle.js", response);
		then(response).containsText("window.mypkg = {\"bs\":{\"b1\":{}}};");
	}
	
	@Test
	public void packageDefinitionsAreDefinedInASingleRequest() throws Exception {	
		given(bladeset).hasClasses("mypkg.bs.Class1", "mypkg.bs.Class2")
    		.and(bladeset).classRequires("mypkg.bs.Class1", "mypkg.bs.Class2")
    		.and(blade).hasClass("mypkg.bs.b1.Class1")
    		.and(blade).hasPackageStyle("src/mypkg/bs/b1", NamespacedJsBundlerPlugin.JS_STYLE)
    		.and(blade).classRefersTo("mypkg.bs.b1.Class1", "mypkg.bs.Class1")
    		.and(aspect).indexPageRefersTo("mypkg.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/namespaced-js/package-definitions.js", response);
		then(response).textEquals("// package definition block\n" + "window.mypkg = {\"bs\":{\"b1\":{}}};\n");
	}
	
	@Test	// blade unhappy paths
	public void weDontBundleABladeIfItIsNotReferredToAnAspect() throws Exception {
		given(blade).hasPackageStyle("src/mypkg/bs/b1", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(blade).hasClasses("mypkg.bs.b1.Class1", "mypkg.bs.b1.Class2")
			.and(blade).classRefersTo("mypkg.bs.b1.Class1", "mypkg.bs.b1.Class2")
			.and(aspect).indexPageRefersTo("mypkg.bs.b1.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.bs.b1.Class2")
			.and(response).doesNotContainClasses("mypkg.bs.b1.Class1");
	}
	
	@Test
	public void bladeClassesCanOnlyDependOnExistentClassesWhenAspectIsRequested() throws Exception {
		given(blade).hasClass("mypkg.Class1")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(blade).classRequires("mypkg.Class1", "mypkg.NonExistentClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "mypkg/NonExistentClass")
			.whereTopLevelExceptionIs(BundlerProcessingException.class);
	}
	
	@Test
	public void bladeClassesThatReferToNonExistentClassesWontCauseAnExceptionWhenAspectIsRequested() throws Exception {
		given(blade).hasPackageStyle("src/mypkg/bs/b1", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(blade).hasClass("mypkg.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("mypkg.bs.b1.Class1")
			.and(blade).classRefersTo("mypkg.bs.b1.Class1", "mypkg.bs.b1.NonExistentClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	// ----------------------------------- X M L -------------------------------------- 
	@Test
	public void classesReferringToABladesetInAspectXMlFilesAreBundled() throws Exception {
		given(bladeset).hasClasses("mypkg.bs.Class1", "mypkg.bs.Class2")
    		.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.bs.Class1");
	}
	
	@Test
	public void classesReferringToABladeInAspectXMlFilesAreBundled() throws Exception {
		given(blade).hasClasses("mypkg.bs.b1.Class1", "mypkg.bs.b1.Class2")
    		.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.bs.b1.Class1");
	}

	// ---------------------------------  H T M L -------------------------------------
	@Test
	public void classesReferringToABladesetInAspectHTMlFilesAreBundled() throws Exception {
		given(blade).hasClasses("mypkg.bs.Class1", "mypkg.bs.Class2")
		.and(aspect).resourceFileRefersTo("html/view.html", "mypkg.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.bs.Class1");
	}
	
	@Test
	public void classesReferringToABladeInAspectHTMlFilesAreBundled() throws Exception {
		given(blade).hasClasses("mypkg.bs.b1.Class1", "mypkg.bs.b1.Class2")
			.and(aspect).resourceFileRefersTo("html/view.html", "mypkg.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.bs.b1.Class1");
	}
	
	// ----------------------------------- C S S  -------------------------------------
	// TODO enable when we work on CSS Bundler
	@Ignore 
 	@Test
 	public void aspectCssFilesAreBundled() throws Exception {
		given(aspect).hasPackageStyle("src/mypkg", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(standardAspectTheme).containsFileWithContents("style.css", "ASPECT theme content");
 		when(app).requestReceived("/default-aspect/css/standard_css.bundle", response);
 		then(response).containsText("ASPECT theme content");
 	}
	
	@Ignore 
 	@Test
 	public void bladesetCssFilesAreBundledWhenReferencedInTheAspect() throws Exception {
		given(aspect).hasPackageStyle("src/mypkg/bs", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(bladeset).hasClass("mypkg.bs.Class1")
			.and(standardBladesetTheme).containsFileWithContents("style.css", "BLADESET theme content")
			.and(aspect).indexPageRefersTo("mypkg.bs.Class1");
 		when(app).requestReceived("/default-aspect/css/standard_css.bundle", response);
 		then(response).containsText("BLADESET theme content");
 	}
	
	@Ignore 
 	@Test
 	public void bladeCssFilesAreBundledWhenReferencedInTheAspect() throws Exception {
		given(aspect).hasPackageStyle("src/mypkg/bs/b1", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(blade).hasClass("mypkg.bs.b1.Class1")
			.and(standardBladeTheme).containsFileWithContents("style.css", "BLADE theme content")
			.and(aspect).indexPageRefersTo("mypkg.bs.b1.Class1");
 		when(app).requestReceived("/default-aspect/css/standard_css.bundle", response);
 		then(response).containsText("BLADE theme content");
 	}
	
	// ------------------------------- L O G G I N G ----------------------------------
	@Test
	public void helpfulLoggingMessagesAreEmitted() throws Exception {
		given(logging).enabled()
			.and(blade).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1")
			.and(blade).classRequires("mypkg.Class1", "mypkg.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(logging).debugMessageReceived(REQUEST_HANDLED_MSG, "js/dev/en_GB/combined/bundle.js", "app1")
			.and(logging).debugMessageReceived(CONTEXT_IDENTIFIED_MSG, "Aspect", "default", "js/dev/en_GB/combined/bundle.js")
			.and(logging).debugMessageReceived(BUNDLER_IDENTIFIED_MSG, "CompositeJsBundlerPlugin", "js/dev/en_GB/combined/bundle.js")
			.and(logging).debugMessageReceived(BUNDLABLE_NODE_SEED_FILES_MSG, unquoted("Aspect"), "default", unquoted("'index.html', 'resources/xml/config.xml'"))
			.and(logging).debugMessageReceived(APP_SOURCE_LOCATIONS_MSG, "app1", "'default-aspect/', 'bs-bladeset/', 'bs-bladeset/blades/b1/', 'sdk/libs/javascript/caplin'")
			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "index.html", "'src/mypkg/Class1.js'")
			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "src/mypkg/Class1.js", "'src/mypkg/Class2.js'")
			.and(logging).debugMessageReceived(FILE_HAS_NO_DEPENDENCIES_MSG, "src/mypkg/Class2.js")
			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "resources/xml/config.xml", "'src/mypkg/Class1.js'");
	}
	
	@Test
	public void helpfulLoggingMessagesAreEmittedWhenThereAreNoSeedFiles() throws Exception {
		given(logging).enabled()
			.and(blade).hasClasses("mypkg.Class1", "mypkg.Class2")
			.and(blade).classRequires("mypkg.Class1", "mypkg.Class2")
			.and(aspect).hasBeenCreated();
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(logging).debugMessageReceived(REQUEST_HANDLED_MSG, "js/dev/en_GB/combined/bundle.js", "app1")
			.and(logging).debugMessageReceived(CONTEXT_IDENTIFIED_MSG, unquoted("Aspect"), "default", "js/dev/en_GB/combined/bundle.js")
			.and(logging).debugMessageReceived(BUNDLER_IDENTIFIED_MSG, "CompositeJsBundlerPlugin", "js/dev/en_GB/combined/bundle.js")
			.and(logging).debugMessageReceived(BUNDLABLE_NODE_HAS_NO_SEED_FILES_MSG, unquoted("Aspect"), "default")
			.and(logging).debugMessageReceived(APP_SOURCE_LOCATIONS_MSG, "app1", unquoted("'default-aspect/', 'bs-bladeset/', 'bs-bladeset/blades/b1/', 'sdk/libs/javascript/caplin'"));
	}
	
	// ------------------------ A P P   T H I R D P A R T Y   L I B ------------------------
	@Test
	public void aspectBundlesAppLegacyThirdpartyLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(appLegacyThirdparty).hasClasses("appThirdparty.Class1", "appThirdparty.Class2")
			.and(aspect).indexPageRefersTo("appThirdparty.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appThirdparty.Class1")
			.and(response).doesNotContainClasses("appThirdparty.Class2");
	}
	
	@Test
	public void aspectBundlesAppLegacyThirdpartyLibsIfTheyAreReferencedInAClass() throws Exception {
		given(appLegacyThirdparty).hasBeenCreated()
			.and(appLegacyThirdparty).hasPackageStyle("appThirdpartyLibName", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(appLegacyThirdparty).hasClass("appThirdparty.Class1")
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).hasPackageStyle("src/mypkg", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "appThirdparty.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appThirdparty.Class1");
	}
	
	@Test
	public void aspectBundlesContainAppLegacyThirdpartyLibsIfTheyAreRequiredInAClass() throws Exception {
		given(appLegacyThirdparty).hasClass("appThirdpartyLibName.Class1")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).classRequires("mypkg.Class1", "appThirdpartyLibName.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appThirdpartyLibName.Class1");
	}
	
	// ------------------------------- J S   P A T C H E S ----------------------------------
//	TODO uncomment when jsPatches work is properly implemented
	@Ignore
	@Test
	public void weDoNotBundleNamespacedJsPatchesForLibraryClassesWhichAreNotReferenced() throws Exception {
		given(sdkJsLib).hasBeenCreated()
			.and(sdkJsLib).hasPackageStyle("src/sdkJsLib", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(sdkJsLib).hasClass("sdkJsLib.Class1")
//			.and(jsPatchesLib).hasPackageStyle("br/sdkJsLib", NamespacedJsBundlerPlugin.JS_STYLE)
//			.and(jsPatchesLib).hasClass("sdkJsLib.Class2");	
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).hasPackageStyle("src/mypkg", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "sdkJsLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("sdkJsLib.Class1")
			.and(response).doesNotContainClasses("sdkJsLib.Class2");
	}
	
	// ----------------------------- U S E R   J S   L I B S --------------------------------
	@Test
	public void aspectBundlesContainUserLibrLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(userLib).hasPackageStyle("src/userLib", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(userLib).hasClass("userLib.Class1")
			.and(aspect).indexPageRefersTo("userLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("userLib.Class1");
	}
	
	@Test
	public void aspectBundlesContainUserLibsIfTheyAreReferencedInAClass() throws Exception {
		given(userLib).hasBeenCreated()
			.and(userLib).hasPackageStyle("src/userLib", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(userLib).hasClass("userLib.Class1")
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).hasPackageStyle("src/mypkg", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "userLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("userLib.Class1");
	}
	
	@Test
	public void aspectBundlesContainUserLibsIfTheyAreRequiredInAClass() throws Exception {
		given(userLib).hasClass("userLib.Class1")
		.and(aspect).indexPageRefersTo("mypkg.Class1")
		.and(aspect).hasClass("mypkg.Class1")
		.and(aspect).classRequires("mypkg.Class1", "userLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("userLib.Class1");
	}
	
	// ---------------------------- S D K   J S   L I B S ----------------------------------
	@Test
	public void aspectBundlesContainSdkLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(sdkJsLib).hasClass("sdkJsLib.Class1")
			.and(aspect).indexPageRefersTo("sdkJsLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("sdkJsLib.Class1");
	}
	
	@Test
	public void aspectBundlesContainSdkLibsIfTheyAreReferencedInAClass() throws Exception {
		given(sdkJsLib).hasBeenCreated()
			.and(sdkJsLib).hasPackageStyle("src/sdkJsLib", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(sdkJsLib).hasClass("sdkJsLib.Class1")
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).hasPackageStyle("src/mypkg", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "sdkJsLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("sdkJsLib.Class1")
			.and(response).doesNotContainText("require");
	}
	
	@Test
	public void aspectBundlesContainSdkLibsIfTheyAreRequiredInAClass() throws Exception {
		given(sdkJsLib).hasClass("sdkJsLib.Class1")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).classRequires("mypkg.Class1", "sdkJsLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("sdkJsLib.Class1");
	}
	
	
	// --------------------- S D K   T H I R D P A R T Y   L I B S --------------------------
	@Test
	public void aspectBundlesContainLegacyThirdpartyLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(sdkLegacyThirdparty).hasBeenCreated()
			.and(sdkLegacyThirdparty).containsFileWithContents("library.manifest", "depends:")
			.and(sdkLegacyThirdparty).containsFileWithContents("src.js", "window.lib = { }")
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).indexPageRefersTo(sdkLegacyThirdparty);
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }");
	}
	
	@Test
	public void aspectBundlesContainLegacyThirdpartyLibsIfTheyAreReferencedInAnAspectClass() throws Exception {		
		given(sdkLegacyThirdparty).hasBeenCreated()
			.and(sdkLegacyThirdparty).containsFileWithContents("library.manifest", "depends:")
    		.and(sdkLegacyThirdparty).containsFileWithContents("src.js", "window.lib = { }")
    		.and(aspect).hasClass("mypkg.Class1")
    		.and(aspect).classRequiresThirdpartyLib("mypkg.Class1", sdkLegacyThirdparty)
    		.and(aspect).indexPageRefersTo("mypkg.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }");
	}
	
	@Test
	public void canBundleLegacyThirdpartyLibsIfTheyAreReferencedInABladeset() throws Exception {		
		given(sdkLegacyThirdparty).hasBeenCreated()
			.and(sdkLegacyThirdparty).containsFileWithContents("library.manifest", "depends:")
    		.and(sdkLegacyThirdparty).containsFileWithContents("src.js", "window.lib = { }")
    		.and(bladeset).hasClasses("mypkg.bs.Class1", "mypkg.bs.Class2")
    		.and(bladeset).classRequiresThirdpartyLib("mypkg.bs.Class1", sdkLegacyThirdparty)
    		.and(aspect).indexPageRefersTo("mypkg.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }");
	}
	
	@Test
	public void canBundleLegacyThirdpartyLibsIfTheyAreReferencedInABlade() throws Exception {		
		given(sdkLegacyThirdparty).hasBeenCreated()
			.and(sdkLegacyThirdparty).containsFileWithContents("library.manifest", "depends:")
    		.and(sdkLegacyThirdparty).containsFileWithContents("src.js", "window.lib = { }")
    		.and(blade).hasClasses("mypkg.bs.b1.Class1", "mypkg.bs.b1.Class2")
    		.and(blade).classRequiresThirdpartyLib("mypkg.bs.b1.Class1", sdkLegacyThirdparty)
    		.and(aspect).indexPageRefersTo("mypkg.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }");
	}
	
	@Test
	public void canBundleLegacyThirdpartyLibAndItsOtherDependencyLibsIfTheyAreReferencedInABlade() throws Exception {		
		given(sdkLegacyThirdparty).hasBeenCreated()
			.and(sdkLegacyThirdparty2).hasBeenCreated()
			.and(sdkLegacyThirdparty2).containsFileWithContents("library.manifest", "depends: ")
			.and(sdkLegacyThirdparty2).containsFileWithContents("src.js", "window.legacy2 = { }")
			.and(sdkLegacyThirdparty).containsFileWithContents("library.manifest", "depends: legacy-thirdparty2")
			.and(sdkLegacyThirdparty).containsFileWithContents("src.js", "window.legacy = { }")
			.and(aspect).indexPageRefersTo(sdkLegacyThirdparty);

		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.legacy = { }")
			.and(response).containsText("window.legacy2 = { }");
	}
}
