package org.bladerunnerjs.spec.bundling.aspect;


import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'appns' when the default namespace is 'appns')?
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
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			bladeWithSubstringOfAnotherBlade = bladeset.blade("b1b");
	}
	
	@Test
	public void requirePathsCanBeRelative() throws Exception {
		given(blade).containsFileWithContents("src/appns/bs/b1/Class1.js", "Class1 = function(){}; require('./Class2')")
			.and(blade).containsFileWithContents("src/appns/bs/b1/Class2.js", "Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/bs/b1/Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("Class2 = function(){};");
	}
	
	@Test
	public void requirePathsCanBeRelativeWithSpaces() throws Exception {
		given(blade).containsFileWithContents("src/appns/bs/b1/Class1.js", "require( './Class2' ); Class1 = function(){}; ")
			.and(blade).containsFileWithContents("src/appns/bs/b1/Class2.js", "Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/bs/b1/Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("Class2 = function(){};");
	}
	
	@Test
	public void weBundleABladeClassIfItIsReferredToInTheIndexPage() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.b1.Class1");
	}
	
	@Test
	public void weBundleImplicitTransitiveDependenciesFromABlade() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2");
	}
	
	@Test
	public void weBundleExplicitTransitiveDependenciesForFromABlade() throws Exception {
		given(blade).hasClasses("appns/bs/b1/Class1", "appns/bs/b1/Class2")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
			.and(blade).classRequires("appns/bs/b1/Class1", "appns.bs.b1.Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2");
	}
	
	@Test
	public void weBundleImplicitTransitiveDependenciesFromABladeIncludingBladesetDependencies() throws Exception {	
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).classDependsOn("appns.bs.Class1", "appns.bs.Class2")
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsClasses("appns.bs.Class1", "appns.bs.b1.Class1");
	}
	
	@Test
	public void weBundleExplicitTransitiveDependenciesFromABladeIncludingBladesetDependencies() throws Exception {	
		given(bladeset).hasClasses("appns/bs/Class1", "appns/bs/Class2")
			.and(bladeset).classRequires("appns/bs/Class1", "appns.bs.Class2")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.Class1")
			.and(response).containsClasses("appns.bs.b1.Class1");
	}
	
	@Test
	public void devRequestsContainThePackageDefinitionsAtTheTop() throws Exception {
		given(bladeset).hasClasses("appns/bs/Class1", "appns/bs/Class2")
			.and(bladeset).classRequires("appns/bs/Class1", "appns.bs.Class2")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("mergePackageBlock(window, {\"appns\":{\"bs\":{\"b1\":{}}}});");
	}
	
	@Test
	public void prodRequestsContainThePackageDefinitionsAtTheTop() throws Exception {
		given(bladeset).hasClasses("appns/bs/Class1", "appns/bs/Class2")
			.and(bladeset).classRequires("appns/bs/Class1", "appns.bs.Class2")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("js/prod/combined/bundle.js", response);
		then(response).containsText("mergePackageBlock(window, {\"appns\":{\"bs\":{\"b1\":{}}}});");
	}
	
	@Test
	public void packageDefinitionsAreDefinedInASingleRequest() throws Exception {	
		given(bladeset).hasClasses("appns/bs/Class1", "appns/bs/Class2")
			.and(bladeset).classRequires("appns/bs/Class1", "appns.bs.Class2")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("namespaced-js/package-definitions.js", response);
		then(response).containsOrderedTextFragments("// package definition block\n", "mergePackageBlock(window, {\"appns\":{\"bs\":{\"b1\":{}}}});");
	}
	
	@Test	// blade unhappy paths
	public void weDontBundleABladeIfItIsNotReferredToAnAspect() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsClasses("appns.bs.b1.Class2")
			.and(response).doesNotContainClasses("appns.bs.b1.Class1");
	}
	
	@Test	// blade unhappy paths
	public void bladeClassesCanOnlyDependOnExistentClassesWhenAspectIsRequested() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
			.and(blade).classRequires("appns/bs/b1/Class1", "appns.NonExistentClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "appns/NonExistentClass")
			.whereTopLevelExceptionIs(ContentProcessingException.class);
	}
	
	@Test
	public void bladeClassesThatReferToNonExistentClassesWontCauseAnExceptionWhenAspectIsRequested() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.b1.NonExistentClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void bladesReferencedByOtherBladesDoNotGetBundled() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).classFileHasContent("appns.bs.b1.Class1", "should not get bundled")
			.and(bladeWithSubstringOfAnotherBlade).hasNamespacedJsPackageStyle()
			.and(bladeWithSubstringOfAnotherBlade).hasClass("appns.bs.b1b.Class1")
			.and(bladeWithSubstringOfAnotherBlade).classDependsOn("appns.bs.b1b.Class1", "appns.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1b.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsClasses("appns.bs.b1b.Class1")
			.and(response).doesNotContainText("should not get bundled");
	}
	
	@Test
	public void bladeNamespaceWithSubstringOfAnotherBladeShouldNotGetBundled() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(bladeWithSubstringOfAnotherBlade).hasClass("appns.bs.b1b.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1b.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).doesNotContainText("appns.bs.b1.Class1 = function() {");
	}
	
	@Test
	public void bladesCanNotDependOnAspectClasses() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(blade).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.AspectClass")
			.and(blade).hasClass("appns.bs.b1.BladeClass")
			.and(blade).classDependsOn("appns.bs.b1.BladeClass", "appns.AspectClass")
			.and(aspect).indexPageRefersTo("appns.bs.b1.BladeClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.bs.b1.BladeClass =")
			.and(response).doesNotContainText("appns.AspectClass =");
	}
	
	@Test
	public void weBundleABladeClassIfItIsContainedInDoubleQuotes() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("src/appns/App.js", "\"appns.bs.b1.Class1\"")
			.and(aspect).indexPageRefersTo("appns.App");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.b1.Class1");
	}
	
	@Test
	public void weBundleABladeClassIfItIsContainedInSingleQuotes() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).hasNamespacedJsPackageStyle()			
			.and(aspect).containsFileWithContents("src/appns/App.js", "'appns.bs.b1.Class1'")
			.and(aspect).indexPageRefersTo("appns.App");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.b1.Class1");
	}
	
	@Test
	public void weBundleABladeClassIfItIsContainedInEscapedDoubleQuotes() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("src/appns/App.js", "\"\\\"appns.bs.b1.Class1\\\"\"")
			.and(aspect).indexPageRefersTo("appns.App");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.b1.Class1");
	}
	
	@Test
	public void weBundleABladeClassIfItIsContainedInEscapedSingleQuotes() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("src/appns/App.js", "'\\'appns.bs.b1.Class1\\'")
			.and(aspect).indexPageRefersTo("appns.App");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.b1.Class1");
	}
	
}
