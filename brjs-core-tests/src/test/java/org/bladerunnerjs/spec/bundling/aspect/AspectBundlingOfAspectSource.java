package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.model.exception.CircularDependencyException;
import org.bladerunnerjs.api.model.exception.OutOfBundleScopeRequirePathException;
import org.bladerunnerjs.api.model.exception.UnresolvableRelativeRequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'appns' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectBundlingOfAspectSource extends SpecTest {
	private App app;
	private Aspect aspect;
	private Aspect otherAspect;
	private Aspect rootDefaultAspect;

	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			otherAspect = app.aspect("other");
			
			rootDefaultAspect = app.defaultAspect();
	}
	
	@Test
	public void utf8CharactersAreBundledCorrectlyForDev() throws Exception {
		given(aspect).containsFileWithContents("src/Class1.js", "£$€")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("£$€");
	}
	
	@Test
	public void utf8CharactersAreBundledCorrectlyForProd() throws Exception {
		given(aspect).containsFileWithContents("src/Class1.js", "£$€")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("js/prod/combined/bundle.js", response);
		then(response).containsText("£$€");
	}
	
	@Test
	public void weBundleAnAspectClassIfItIsReferredToInTheIndexPage() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1");
	}
	
	@Test
	public void weDontBundleNamespacedClassesFromOtherAspects() throws Exception {
		given(otherAspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).doesNotContainClasses("appns.Class1");
	}
	
	@Test
	public void weThrowAnExceptionIfARequiredCommonJsClassIsOnlyAvailableInAnotherAspect() throws Exception {
		given(otherAspect).hasClass("appns/Class2")
			.and(aspect).classRequires("appns/Class1", "appns/Class2")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(OutOfBundleScopeRequirePathException.class, "appns/Class2");
	}
	
	@Test
	public void requirePathsCanBeRelative() throws Exception {
		given(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classRequires("appns/Class1", "./Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1", "appns.Class2");
	}
	
	@Test
	public void relativeRequirePathsWorkInChildPackages() throws Exception {
		given(aspect).hasClasses("appns/pkg/Class1", "appns/pkg/Class2")
			.and(aspect).indexPageRefersTo("appns.pkg.Class1")
			.and(aspect).classRequires("appns/pkg/Class1", "./Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.pkg.Class1", "appns.pkg.Class2");
	}
	
	@Test
	public void relativeRequirePathsCanPointToTheParentDirectory() throws Exception {
		given(aspect).hasClasses("appns/pkg/Class1", "appns/Class2")
			.and(aspect).indexPageRefersTo("appns.pkg.Class1")
			.and(aspect).classRequires("appns/pkg/Class1", "../Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.pkg.Class1", "appns.Class2");
	}
	
	@Test
	public void relativeRequirePathsCanPointToAnyLevelParentDirectory() throws Exception {
		given(aspect).hasClasses("appns/pkg/pkg2/Class1", "appns/Class2")
			.and(aspect).indexPageRefersTo("appns.pkg.pkg2.Class1")
			.and(aspect).classRequires("appns/pkg/pkg2/Class1", "../../Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.pkg.pkg2.Class1", "appns.Class2");
	}
	
	@Test
	public void exceptionIsThrownIfRelativeRequirePathGoesAboveRoot() throws Exception {
		given(aspect).hasClasses("appns/pkg/pkg2/Class1", "appns/Class2")
			.and(aspect).indexPageRefersTo("appns.pkg.pkg2.Class1")
			.and(aspect).classRequires("appns/pkg/pkg2/Class1", "../../../../Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(UnresolvableRelativeRequirePathException.class, "appns/pkg/pkg2", "../../../../Class2")
			.whereTopLevelExceptionIs(ContentProcessingException.class);
	}
	
	@Test
	public void requireCallDoesNotGetProcessedIfCommentedOutWithTwoSlashes() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; //require('appns/Class2')")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).doesNotContainText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallDoesNotGetProcessedIfCommentedOutWithSlashStar() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; /* require('appns/Class2') */")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).doesNotContainText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallDoesNotGetProcessedIfCommentedOutWithSlashStarStar() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; /** require('appns/Class2') */")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).doesNotContainText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveSingleQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; require('appns/Class2')")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveSingleQuotesWithSpaces() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; require( 'appns/Class2' )")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveDoubleQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; require(\"appns/Class2\")")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveSpacesBeforeOpenQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; require( \"appns/Class2\")")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveSpacesBeforeCloseQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; require(\"appns/Class2\" )")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveSpacesBeforeOpenAndCloseQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; require( \"appns/Class2\" )")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveSpacesBeforeOpenAndCloseWithSingleQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; require( 'appns/Class2' )")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.Class2 = function(){};");
	}
	
	@Test
	public void weBundleFilesRequiredFromAnAspect() throws Exception {
		given(aspect).containsFileWithContents("src/appns/App.js", "var App = function() {};  module.exports = App;")
			.and(aspect).indexPageHasContent("var App = require('appns/App')");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("var App = function() {};  module.exports = App;");
	}
	
	@Test // test for https://github.com/BladeRunnerJS/brjs/issues/1517
	public void aNPEIsntThrownWhenACommonJSSourceModuleIsOnlyRequiredAsAPostExportDependency() throws Exception {
		given(aspect).classFileHasContent("appns/App", "App = function() {\n"+"};\n"+"module.exports = App\n"+"require('appns/Util');")
			.and(aspect).hasClass("appns/Util")
			.and(aspect).indexPageRequires("appns/App");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyNoOutstandingExceptions()
			.and(response).containsCommonJsClasses("appns/Util", "appns/App");
	}
	
	@Test // test for https://github.com/BladeRunnerJS/brjs/issues/1517
	public void aNPEIsntThrownWhenANamespacedJSSourceModuleIsOnlyRequiredAsAPostExportDependency() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).classFileHasContent("appns.App", "appns.App = function() {\n"+"appns.Util();\n"+"};\n")
			.and(aspect).hasClass("appns.Util")
			.and(aspect).indexPageRequires("appns/App");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyNoOutstandingExceptions()
			.and(response).containsNamespacedJsClasses("appns.Util", "appns.App");
	}
	
	@Test
	public void circularDependenciesCauseAnExceptionToBeThrown() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).indexPageHasContent("appns.Class1")
			.and(aspect).classExtends("appns.Class1", "appns.Class2")
			.and(aspect).classExtends("appns.Class2", "appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(CircularDependencyException.class, unquoted("appns/Class1 => appns/Class2 => appns/Class1"));
	}
	
	@Test
	public void indirectCircularDependenciesCauseAnExceptionToBeThrown() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).indexPageHasContent("appns.Class1")
			.and(aspect).classExtends("appns.Class1", "appns.Class2")
			.and(aspect).classExtends("appns.Class2", "appns.Class3")
			.and(aspect).classExtends("appns.Class3", "appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(CircularDependencyException.class, unquoted("appns/Class1 => appns/Class2 => appns/Class3 => appns/Class1"));
	}
	
	@Test
	public void theCircularDependencyMessageShouldNotIncludeClassesNotThemselvesPartOfTheCircle() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).indexPageHasContent("appns.Class1")
			.and(aspect).classExtends("appns.Class1", "appns.Class2")
			.and(aspect).classExtends("appns.Class2", "appns.Class3")
			.and(aspect).classExtends("appns.Class3", "appns.Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(CircularDependencyException.class, unquoted("appns/Class2 => appns/Class3 => appns/Class2"));
	}
	
	@Test
	public void havingAPostDefineDependencyLeadToTheCircleDoesntCauseAProblem() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).indexPageHasContent("appns.Class1")
			.and(aspect).classDependsOn("appns.Class1", "appns.Class2")
			.and(aspect).classExtends("appns.Class2", "appns.Class3")
			.and(aspect).classExtends("appns.Class3", "appns.Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(CircularDependencyException.class, unquoted("appns/Class2 => appns/Class3 => appns/Class2"));
	}
	
	@Test
	public void weBundleARootLevelAspectClassIfItIsReferredToInTheIndexPage() throws Exception {
		given(rootDefaultAspect).hasClass("appns/Class1")
			.and(rootDefaultAspect).indexPageRefersTo("appns.Class1");
		when(rootDefaultAspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1");
	}
	
	@Test
	public void sourceCodeInTheSameDirectoryAsAUsedClassIsNotAutomaticallyBundledIfItIsntReferenced() throws Exception {
		given(rootDefaultAspect).hasClasses("appns/foo/Class1", "appns/foo/Class2")
			.and(rootDefaultAspect).indexPageRefersTo("appns.foo.Class1");
		when(rootDefaultAspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).doesNotContainText("appns/foo/Class2");
	}
}
