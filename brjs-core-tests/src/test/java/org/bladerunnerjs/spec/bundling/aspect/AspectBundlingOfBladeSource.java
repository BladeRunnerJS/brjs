package org.bladerunnerjs.spec.bundling.aspect;


import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.model.exception.OutOfScopeRequirePathException;
import org.bladerunnerjs.api.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.plugin.plugins.require.AliasDataSourceModule;
import org.bladerunnerjs.utility.BundleSetBuilder;
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
	private Bladeset defaultBladeset;
	private Blade blade1InDefaultBladeset;
	private Blade blade2InDefaultBladeset;
	
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
			defaultBladeset = app.defaultBladeset();
			blade1InDefaultBladeset = defaultBladeset.blade("b1");
			blade2InDefaultBladeset = defaultBladeset.blade("b2");
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
		then(response).containsNamespacedJsClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2");
	}
	
	@Test
	public void weBundleExplicitTransitiveDependenciesForFromABlade() throws Exception {
		given(blade).hasClasses("appns/bs/b1/Class1", "appns/bs/b1/Class2")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
			.and(blade).classRequires("appns/bs/b1/Class1", "appns/bs/b1/Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2");
	}
	
	@Test
	public void weBundleImplicitTransitiveDependenciesFromABladeIncludingBladesetDependencies() throws Exception {	
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).classDependsOn("appns.bs.Class1", "appns.bs.Class2")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsNamespacedJsClasses("appns.bs.Class1", "appns.bs.b1.Class1");
	}
	
	@Test
	public void weBundleExplicitTransitiveDependenciesFromABladeIncludingBladesetDependencies() throws Exception {	
		given(bladeset).hasClasses("appns/bs/Class1", "appns/bs/Class2")
			.and(bladeset).classRequires("appns/bs/Class1", "appns/bs/Class2")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.Class1")
			.and(response).containsNamespacedJsClasses("appns.bs.b1.Class1");
	}
	
	@Test
	public void devRequestsContainThePackageDefinitionsAtTheTop() throws Exception {
		given(bladeset).hasClasses("appns/bs/Class1", "appns/bs/Class2")
			.and(bladeset).classRequires("appns/bs/Class1", "appns/bs/Class2")
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
			.and(bladeset).classRequires("appns/bs/Class1", "appns/bs/Class2")
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
			.and(bladeset).classRequires("appns/bs/Class1", "appns/bs/Class2")
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
		then(response).containsNamespacedJsClasses("appns.bs.b1.Class2")
			.and(response).doesNotContainClasses("appns.bs.b1.Class1");
	}
	
	@Test	// blade unhappy paths
	public void bladeClassesCanOnlyDependOnExistentClassesWhenAspectIsRequested() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
			.and(blade).classRequires("appns/bs/b1/Class1", "appns/NonExistentClass");
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
		then(response).containsNamespacedJsClasses("appns.bs.b1b.Class1")
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
	
	@Test
	public void bladeClassesInDefaultBladesetCanBeBundled() throws Exception {
		given(aspect).classFileHasContent("appns/App", "require('./b1/Blade1Class'); require('./b2/Blade2Class');")
			.and(blade1InDefaultBladeset).hasClass("appns/b1/Blade1Class")
			.and(blade2InDefaultBladeset).hasClass("appns/b2/Blade2Class")
			.and(aspect).indexPageRequires("appns/App");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns/b1/Blade1Class", "appns/b2/Blade2Class");
	}
	
	@Test
	public void exceptionIsThrownIfBladeClassRequestsAResourceFromDefaultAspect() throws Exception {
		given(aspect).indexPageRequires("appns/b1/Blade1Class")
			.and(blade1InDefaultBladeset).classRequires("Blade1Class", "appns/b2/Blade2Class")
			.and(blade2InDefaultBladeset).hasClass("Blade2Class");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(OutOfScopeRequirePathException.class, 
				"appns/b1/Blade1Class", "appns/b2/Blade2Class", "blades/b2/src/Blade2Class.js", Blade.class.getSimpleName(),
				"apps/app1/blades/b1, apps/app1")
			.whereTopLevelExceptionIs(ContentProcessingException.class);
	}
	
	@Test
	public void exceptionIsNotThrownIfBladeClassRequestsAResourceFromDefaultAspect_andRequiringBladeAssetContainerHasNoScrictCheckingFile() throws Exception {
		given(aspect).indexPageRequires("appns/b1/Blade1Class")
			.and(blade1InDefaultBladeset).classRequires("Blade1Class", "appns/b2/Blade2Class")
			.and(blade1InDefaultBladeset).containsEmptyFile("no-strict-checking")
			.and(blade2InDefaultBladeset).hasClass("Blade2Class");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns/b1/Blade1Class", "appns/b2/Blade2Class")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void exceptionIsNotThrownIfBladeClassRequestsAResourceFromDefaultAspect_andRequiredBladeAssetContainerHasNoScrictCheckingFile() throws Exception {
		given(aspect).indexPageRequires("appns/b1/Blade1Class")
			.and(blade1InDefaultBladeset).classRequires("Blade1Class", "appns/b2/Blade2Class")
			.and(blade2InDefaultBladeset).hasClass("Blade2Class")
			.and(blade2InDefaultBladeset).containsEmptyFile("no-strict-checking");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns/b1/Blade1Class", "appns/b2/Blade2Class")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void exceptionIsThrownIfBladeRequiresAnAspectClass() throws Exception {
		given(aspect).indexPageRequires("appns/b1/Blade1Class")
			.and(blade1InDefaultBladeset).classRequires("Blade1Class", "appns/AspectClass")
			.and(aspect).hasClass("appns/AspectClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(OutOfScopeRequirePathException.class);
	}
	
	@Test
	public void exceptionIsThrownIfBladeRequiresADefaultAspectClass() throws Exception {
		given( app.defaultAspect() ).indexPageRequires("appns/b1/Blade1Class")
			.and(blade1InDefaultBladeset).classRequires("Blade1Class", "appns/AspectClass")
			.and( app.defaultAspect() ).hasClass("appns/AspectClass");
		when( app.defaultAspect() ).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(OutOfScopeRequirePathException.class);
	}
	
	@Test
	public void noStrictCheckingFileCanBeAtANestedLevelInsideTheBlade() throws Exception {
		given(aspect).indexPageRequires("appns/b1/Blade1Class")
    		.and(blade1InDefaultBladeset).classRequires("Blade1Class", "appns/b2/foo/Blade2Class")		
    		.and(blade2InDefaultBladeset).hasDir("src/foo")
    		.and(blade2InDefaultBladeset).containsEmptyFile("src/foo/no-strict-checking")
    		.and(blade2InDefaultBladeset).hasClass("foo/Blade2Class");
    	when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(response).containsCommonJsClasses("appns/b1/Blade1Class", "appns/b2/Blade2Class")
    		.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void noStrictCheckingFileCanBeAtANestedLevelInsideTheBladeAndOnlyAppliesToSubfolders() throws Exception {
		given(aspect).indexPageRequires("appns/b1/Blade1Class")
    		.and(blade1InDefaultBladeset).classRequires("Blade1Class", "appns/b2/Blade2Class")		
    		.and(blade2InDefaultBladeset).hasDir("src/foo")
    		.and(blade2InDefaultBladeset).containsEmptyFile("src/foo/no-strict-checking")
    		.and(blade2InDefaultBladeset).hasClass("Blade2Class");
    	when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(exceptions).verifyException(OutOfScopeRequirePathException.class);
	}
	
	@Test
	public void warningIsLoggedWhenStrictCheckingIsDisabled() throws Exception {
		given(aspect).indexPageRequires("appns/b1/Blade1Class")
    		.and(blade1InDefaultBladeset).classRequires("Blade1Class", "appns/b2/foo/Blade2Class")
    		.and(blade2InDefaultBladeset).hasDir("src/foo")
    		.and(blade2InDefaultBladeset).containsEmptyFile("src/foo/no-strict-checking")
    		.and(blade2InDefaultBladeset).hasClass("foo/Blade2Class")
    		.and(logging).enabled();
    	when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(logging).warnMessageReceived(BundleSetBuilder.INVALID_REQUIRE_MSG, "appns/b1/Blade1Class", "appns/b2/foo/Blade2Class")
    		.and(logging).warnMessageReceived(BundleSetBuilder.STRICT_CHECKING_DISABLED_MSG, "apps/app1/blades/b2/src/foo", "blades/b2/src/foo/Blade2Class.js", "apps/app1/blades/b2/src/foo/no-strict-checking")
    		.and(logging).otherMessagesIgnored();
	}
	
	@Test
	public void badRequiresAreLoggedWhenStrictCheckingIsDisabled() throws Exception {
		given(aspect).indexPageRequires("appns/b1/Blade1Class")
    		.and(blade1InDefaultBladeset).classRequires("Blade1Class", "appns/b2/foo/Blade2Class")
    		.and(blade2InDefaultBladeset).hasDir("src/foo")
    		.and(blade2InDefaultBladeset).containsEmptyFile("src/foo/no-strict-checking")
    		.and(blade2InDefaultBladeset).hasClass("foo/Blade2Class")
    		.and(logging).enabled();
    	when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(logging).warnMessageReceived(BundleSetBuilder.INVALID_REQUIRE_MSG, "appns/b1/Blade1Class", "appns/b2/foo/Blade2Class")
    		.and(logging).otherMessagesIgnored();
	}
	
	@Test
	public void badRequiresAreNotLoggedForNoneScopeEnforcedSourceModules() throws Exception {
		given(aspect).indexPageRequires("appns/b1/Blade1Class")
    		.and(blade1InDefaultBladeset).classRequires("Blade1Class", AliasDataSourceModule.PRIMARY_REQUIRE_PATH)
    		.and(logging).enabled();
    	when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(logging).doesNotContainWarnMessage(BundleSetBuilder.INVALID_REQUIRE_MSG);
	}
	
}
