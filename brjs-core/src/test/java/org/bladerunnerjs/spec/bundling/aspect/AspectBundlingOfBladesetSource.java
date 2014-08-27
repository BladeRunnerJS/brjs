package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'appns' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectBundlingOfBladesetSource extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
	}
	
	@Test
	public void weBundleABladesetClassIfItIsReferredToInTheIndexPage() throws Exception {
		given(bladeset).hasClass("appns/bs/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.Class1");
	}
	
	@Test
	public void weBundleImplicitTransitiveDependenciesFromABladeset() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).classDependsOn("appns.bs.Class1", "appns.bs.Class2")
			.and(aspect).indexPageRefersTo("appns.bs.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsNamespacedJsClasses("appns.bs.Class1", "appns.bs.Class2");
	}
	
	@Test
	public void weBundleExplicitTransitiveDependenciesForFromABladeset() throws Exception {
		given(bladeset).hasClasses("appns/bs/Class1", "appns/bs/Class2")
			.and(aspect).indexPageRefersTo("appns.bs.Class1")
			.and(bladeset).classRequires("appns/bs/Class1", "appns.bs.Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.Class1", "appns.bs.Class2");
	}
	
	@Test	// bladeset unhappy paths
	public void weDontBundleABladesetClassIfItIsNotReferredToByAnAspect() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).classDependsOn("appns.bs.Class1", "appns.bs.Class2")
			.and(aspect).indexPageRefersTo("appns.bs.Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsNamespacedJsClasses("appns.bs.Class2")
			.and(response).doesNotContainClasses("appns.bs.Class1");
	}
	
	@Test
	public void bladesetClassesCanOnlyDependOnExistentClasses() throws Exception {
		given(bladeset).hasClass("appns/bs/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.Class1")
			.and(bladeset).classRequires("appns/bs/Class1", "appns.NonExistentClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "appns/NonExistentClass")
			.whereTopLevelExceptionIs(ContentProcessingException.class);
	}
	
	@Test
	public void bladesetClassesThatReferToNonExistentClassesWontCauseAnExceptionWhenAspectIsRequested() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClass("appns.bs.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.Class1")
			.and(bladeset).classDependsOn("appns.bs.Class1", "appns.bs.NonExistentClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
}
