package org.bladerunnerjs.spec.bundling.aspect;


import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsBundlerPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'mypkg' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectBundlingOfBladeSource extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade, bladeWithSubstringOfAnotherBlade;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			bladeWithSubstringOfAnotherBlade = bladeset.blade("b1b");
	}
	
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
	
	@Test	// blade unhappy paths
	public void bladeClassesCanOnlyDependOnExistentClassesWhenAspectIsRequested() throws Exception {
		given(blade).hasClass("mypkg.Class1")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(blade).classRequires("mypkg.Class1", "mypkg.NonExistentClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "/mypkg/NonExistentClass")
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
	
	// TODO This test should fail
	@Ignore
	@Test
	public void bladesReferencedByOtherBladesDoNotGetBundled() throws Exception {
		given(blade).hasPackageStyle("src/mypkg/bs/b1", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(blade).hasClass("mypkg.bs.b1.Class1")
			.and(bladeWithSubstringOfAnotherBlade).hasPackageStyle("src/mypkg/bs/b1b", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(bladeWithSubstringOfAnotherBlade).classRefersTo("mypkg.bs.b1b.Class1", "mypkg.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("mypkg.bs.b1b.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.bs.b1b.Class1")
			.and(response).doesNotContainClasses("mypkg.bs.b1.Class1");
	}
	
	@Test
	public void bladeNamespaceWithSubstringOfAnotherBladeShouldNotGetBundled() throws Exception {
		given(blade).hasPackageStyle("src/mypkg/bs/b1", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(blade).hasClass("mypkg.bs.b1.Class1")
			.and(bladeWithSubstringOfAnotherBlade).hasPackageStyle("src/mypkg/bs/b1b", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(bladeWithSubstringOfAnotherBlade).hasClass("mypkg.bs.b1b.Class1")
			.and(aspect).indexPageRefersTo("mypkg.bs.b1b.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).doesNotContainClasses("mypkg.bs.b1.Class1");
	}
}
