package org.bladerunnerjs.spec.aspect;

import static org.bladerunnerjs.model.utility.LogicalRequestHandler.Messages.*;
import static org.bladerunnerjs.model.BundleSetCreator.Messages.*;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.JsNonBladeRunnerLib;
import org.bladerunnerjs.model.aliasing.AliasesFile;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'novox' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectBundlingTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private AliasesFile aspectAliasesFile;
	private Bladeset bladeset;
	private Blade blade;
	private JsLib sdkLib;
	private JsNonBladeRunnerLib sdkThirdpartyLib;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			aspectAliasesFile = aspect.aliasesFile();
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			sdkLib = brjs.sdkLib();
			sdkThirdpartyLib = brjs.sdkNonBladeRunnerLib("legacy-thirdparty");
	}
	
	// A S P E C T
	@Test
	public void weBundleAnAspectClassIfItIsReferredToInTheIndexPage() throws Exception {
		given(aspect).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class1");
	}
	
	@Test
	public void weBundleAClassIfItsAliasIsReferredToInTheIndexPage() throws Exception {
		given(aspect).hasClass("novox.Class1")
			.and(aspectAliasesFile).hasAlias("the-alias", "novox.Class1")
			.and(aspect).indexPageRefersTo("the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class1");
	}
	
	// B L A D E S E T
	@Test
	public void weBundleABladesetClassIfItIsReferredToInTheIndexPage() throws Exception {
		given(bladeset).hasClass("novox.bs.Class1")
			.and(aspect).indexPageRefersTo("novox.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.bs.Class1");
	}
	
	@Test
	public void weBundleImplicitTransitiveDependenciesFromABladeset() throws Exception {
		given(bladeset).hasPackageStyle("novox", "caplin-js")
			.and(bladeset).hasClasses("novox.bs.Class1", "novox.bs.Class2")
			.and(bladeset).classRefersTo("novox.bs.Class1", "novox.bs.Class2")
			.and(aspect).indexPageRefersTo("novox.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.bs.Class1", "novox.bs.Class2");
	}
	
	@Test
	public void weBundleExplicitTransitiveDependenciesForFromABladeset() throws Exception {
		given(bladeset).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(bladeset).classRequires("novox.Class1", "novox.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class1", "novox.Class2");
	}
	
	// B L A D E
	@Test
	public void weBundleABladeClassIfItIsReferredToInTheIndexPage() throws Exception {
		given(blade).hasClass("novox.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("novox.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.bs.b1.Class1");
	}
	
	@Test
	public void weBundleImplicitTransitiveDependenciesFromABlade() throws Exception {
		given(blade).hasPackageStyle("novox/bs", "caplin-js")
			.and(blade).hasClasses("novox.bs.Class1", "novox.bs.Class2")
			.and(blade).classRefersTo("novox.bs.Class1", "novox.bs.Class2")
			.and(aspect).indexPageRefersTo("novox.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.bs.Class1", "novox.bs.Class2");
	}
	
	@Test
	public void weBundleExplicitTransitiveDependenciesForFromABlade() throws Exception {
		given(blade).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(blade).classRequires("novox.Class1", "novox.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class1", "novox.Class2");
	}
	
	@Test
	public void weBundleImplicitTransitiveDependenciesFromABladeIncludingBladesetDependencies() throws Exception {	
		given(bladeset).hasPackageStyle("novox/bs", "caplin-js")
			.and(bladeset).hasClasses("novox.bs.Class1", "novox.bs.Class2")
			.and(bladeset).classRefersTo("novox.bs.Class1", "novox.bs.Class2")
			.and(blade).hasClass("novox.bs.b1.Class1")
			.and(blade).hasPackageStyle("novox/bs/b1", "caplin-js")
			.and(blade).classRefersTo("novox.bs.b1.Class1", "novox.bs.Class1")
			.and(aspect).indexPageRefersTo("novox.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.bs.Class1", "novox.bs.b1.Class1");
	}
	
	@Test
	public void weBundleExplicitTransitiveDependenciesFromABladeIncludingBladesetDependencies() throws Exception {	
		given(bladeset).hasClasses("novox.bs.Class1", "novox.bs.Class2")
			.and(bladeset).classRequires("novox.bs.Class1", "novox.bs.Class2")
			.and(blade).hasClass("novox.bs.b1.Class1")
			.and(blade).hasPackageStyle("novox/bs/b1", "caplin-js")
			.and(blade).classRefersTo("novox.bs.b1.Class1", "novox.bs.Class1")
			.and(aspect).indexPageRefersTo("novox.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.bs.Class1", "novox.bs.b1.Class1");
	}
	
	@Test
	public void devRequestsContainThePackageDefinitionsAtTheTop() throws Exception {
		given(bladeset).hasClasses("novox.bs.Class1", "novox.bs.Class2")
    		.and(bladeset).classRequires("novox.bs.Class1", "novox.bs.Class2")
    		.and(blade).hasClass("novox.bs.b1.Class1")
    		.and(blade).hasPackageStyle("novox/bs/b1", "caplin-js")
    		.and(blade).classRefersTo("novox.bs.b1.Class1", "novox.bs.Class1")
    		.and(aspect).indexPageRefersTo("novox.bs.b1.Class1");
    	when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.novox = {\"bs\":{\"b1\":{\"Class1\":{}}}};");
	}
	
	@Test
	public void packageDefinitionsAreDefinedInASingleRequest() throws Exception {	
		given(bladeset).hasClasses("novox.bs.Class1", "novox.bs.Class2")
    		.and(bladeset).classRequires("novox.bs.Class1", "novox.bs.Class2")
    		.and(blade).hasClass("novox.bs.b1.Class1")
    		.and(blade).hasPackageStyle("novox/bs/b1", "caplin-js")
    		.and(blade).classRefersTo("novox.bs.b1.Class1", "novox.bs.Class1")
    		.and(aspect).indexPageRefersTo("novox.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/caplin-js/package-definitions.js", response);
		then(response).textEquals("// package definition block\n" + "window.novox = {\"bs\":{\"b1\":{\"Class1\":{}}}};\n");
	}
	
	// X M L  &  H T M L
	@Test
	public void classesReferredToInXMlFilesAreBundled() throws Exception {
		given(blade).hasClasses("novox.Class1", "novox.Class2")
    		.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class1");
	}
	
	@Test
	public void classesReferredToInHTMlFilesAreBundled() throws Exception {
		given(blade).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).resourceFileRefersTo("html/view.html", "novox.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class1");
	}
	
	// L O G G I N G
	@Test
	public void helpfulLoggingMessagesAreEmitted() throws Exception {
		given(logging).enabled()
			.and(blade).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1")
			.and(blade).classRequires("novox.Class1", "novox.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(logging).debugMessageReceived(REQUEST_HANDLED_MSG, "js/dev/en_GB/combined/bundle.js", "app1")
			.and(logging).debugMessageReceived(CONTEXT_IDENTIFIED_MSG, "Aspect", "default", "js/dev/en_GB/combined/bundle.js")
			.and(logging).debugMessageReceived(BUNDLER_IDENTIFIED_MSG, "CompositeJsBundlerPlugin", "js/dev/en_GB/combined/bundle.js")
			.and(logging).debugMessageReceived(BUNDLABLE_NODE_SEED_FILES_MSG, unquoted("Aspect"), "default", unquoted("'index.html', 'resources/xml/config.xml'"))
			.and(logging).debugMessageReceived(APP_SOURCE_LOCATIONS_MSG, "app1", "'default-aspect/', 'bs-bladeset/', 'bs-bladeset/blades/b1/', 'sdk/libs/javascript/caplin'")
			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "index.html", "'src/novox/Class1.js'")
			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "src/novox/Class1.js", "'src/novox/Class2.js'")
			.and(logging).debugMessageReceived(FILE_HAS_NO_DEPENDENCIES_MSG, "src/novox/Class2.js")
			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "resources/xml/config.xml", "'src/novox/Class1.js'");
	}
	
	@Test
	public void helpfulLoggingMessagesAreEmittedWhenThereAreNoSeedFiles() throws Exception {
		given(logging).enabled()
			.and(blade).hasClasses("novox.Class1", "novox.Class2")
			.and(blade).classRequires("novox.Class1", "novox.Class2")
			.and(aspect).hasBeenCreated();
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(logging).debugMessageReceived(REQUEST_HANDLED_MSG, "js/dev/en_GB/combined/bundle.js", "app1")
			.and(logging).debugMessageReceived(CONTEXT_IDENTIFIED_MSG, unquoted("Aspect"), "default", "js/dev/en_GB/combined/bundle.js")
			.and(logging).debugMessageReceived(BUNDLER_IDENTIFIED_MSG, "CompositeJsBundlerPlugin", "js/dev/en_GB/combined/bundle.js")
			.and(logging).debugMessageReceived(BUNDLABLE_NODE_HAS_NO_SEED_FILES_MSG, unquoted("Aspect"), "default")
			.and(logging).debugMessageReceived(APP_SOURCE_LOCATIONS_MSG, "app1", unquoted("'default-aspect/', 'bs-bladeset/', 'bs-bladeset/blades/b1/', 'sdk/libs/javascript/caplin'"));
	}
	
	
	// J S   L I B S
	
	@Test
	public void aspectBundlesContainSdkLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(sdkLib).hasClass("sdk.Class1")
			.and(aspect).indexPageRefersTo("sdk.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("sdk.Class1");
	}
	
	@Test
	public void aspectBundlesContainSdkLibsIfTheyAreReferencedInAClass() throws Exception {
		given(sdkLib).hasBeenCreated()
			.and(sdkLib).hasPackageStyle("sdk", "caplin-js")
			.and(sdkLib).hasClass("sdk.Class1")
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(aspect).hasPackageStyle("novox", "caplin-js")
			.and(aspect).hasClass("novox.Class1")
			.and(aspect).classRefersTo("novox.Class1", "sdk.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("sdk.Class1");
	}
	
	@Test
	public void aspectBundlesContainSdkLibsIfTheyAreRequiredInAClass() throws Exception {
		given(sdkLib).hasClass("sdk.Class1")
		.and(aspect).indexPageRefersTo("novox.Class1")
		.and(aspect).hasClass("novox.Class1")
		.and(aspect).classRequires("novox.Class1", "sdk.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("sdk.Class1");
	}
	
	
	// T H I R D P A R T Y    L I B S
	
	@Ignore
	@Test
	public void aspectBundlesContainLegacyThirdpartyLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(sdkThirdpartyLib).hasBeenCreated()
			.and(sdkThirdpartyLib).containsFileWithContents("src.js", "window.lib = { }")
			.and(aspect).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo(sdkThirdpartyLib);
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }");
	}
	
	@Ignore
	@Test
	public void aspectBundlesContainLegacyThirdpartyLibsIfTheyAreReferencedInAClass() throws Exception {
		given(sdkThirdpartyLib).hasBeenCreated()
    		.and(sdkThirdpartyLib).containsFileWithContents("src.js", "window.lib = { }")
    		.and(aspect).hasClass("novox.Class1")
    		.and(aspect).classRequiresThirdpartyLib("novox.Class1", sdkThirdpartyLib)
    		.and(aspect).indexPageRefersTo("novox.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }");
	}
	
}
