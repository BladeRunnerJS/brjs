package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsBundlerPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'mypkg' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectBundlingOfBladesetSource extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
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
	}
	
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
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "/mypkg/NonExistentClass")
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
}
