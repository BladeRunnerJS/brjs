package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.exception.CircularDependencyException;
import org.bladerunnerjs.model.exception.UnresolvableRelativeRequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'appns' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectBundlingOfAspectSource extends SpecTest {
	private App app;
	private Aspect aspect;

	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	@Test
	public void utf8CharactersAreBundledCorrectlyForDev() throws Exception {
		given(aspect).containsFileWithContents("src/Class1.js", "£$€")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("£$€");
	}
	
	@Test
	public void utf8CharactersAreBundledCorrectlyForProd() throws Exception {
		given(aspect).containsFileWithContents("src/Class1.js", "£$€")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/js/prod/en_GB/combined/bundle.js", response);
		then(response).containsText("£$€");
	}
	
	@Test
	public void weBundleAnAspectClassIfItIsReferredToInTheIndexPage() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("appns.Class1");
	}
	
	@Test
	public void requirePathsCanBeRelative() throws Exception {
		given(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classRequires("appns.Class1", "./Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("appns.Class1", "appns.Class2");
	}
	
	@Test
	public void relativeRequirePathsWorkInChildPackages() throws Exception {
		given(aspect).hasClasses("appns.pkg.Class1", "appns.pkg.Class2")
			.and(aspect).indexPageRefersTo("appns.pkg.Class1")
			.and(aspect).classRequires("appns.pkg.Class1", "./Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("appns.pkg.Class1", "appns.pkg.Class2");
	}
	
	@Test
	public void relativeRequirePathsCanPointToTheParentDirectory() throws Exception {
		given(aspect).hasClasses("appns.pkg.Class1", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.pkg.Class1")
			.and(aspect).classRequires("appns.pkg.Class1", "../Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("appns.pkg.Class1", "appns.Class2");
	}
	
	@Test
	public void relativeRequirePathsCanPointToAnyLevelParentDirectory() throws Exception {
		given(aspect).hasClasses("appns.pkg.pkg2.Class1", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.pkg.pkg2.Class1")
			.and(aspect).classRequires("appns.pkg.pkg2.Class1", "../../Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("appns.pkg.pkg2.Class1", "appns.Class2");
	}
	
	@Test
	public void exceptionIsThrownIfRelativeRequirePathGoesAboveRoot() throws Exception {
		given(aspect).hasClasses("appns.pkg.pkg2.Class1", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.pkg.pkg2.Class1")
			.and(aspect).classRequires("appns.pkg.pkg2.Class1", "../../../../Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyException(UnresolvableRelativeRequirePathException.class, "appns/pkg/pkg2", "../../../../Class2")
			.whereTopLevelExceptionIs(ContentProcessingException.class);
	}
	
	@Test
	public void requireCallDoesNotGetProcessedIfCommentedOutWithTwoSlashes() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; //require('appns/Class2')")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).doesNotContainText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallDoesNotGetProcessedIfCommentedOutWithSlashStar() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; /* require('appns/Class2') */")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).doesNotContainText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallDoesNotGetProcessedIfCommentedOutWithSlashStarStar() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; /** require('appns/Class2') */")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).doesNotContainText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveSingleQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; require('appns/Class2')")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveSingleQuotesWithSpaces() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; require( 'appns/Class2' )")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveDoubleQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; require(\"appns/Class2\")")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveSpacesBeforeOpenQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; require( \"appns/Class2\")")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveSpacesBeforeCloseQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; require(\"appns/Class2\" )")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveSpacesBeforeOpenAndCloseQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; require( \"appns/Class2\" )")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("appns.Class2 = function(){};");
	}
	
	@Test
	public void requireCallCanHaveSpacesBeforeOpenAndCloseWithSingleQuotes() throws Exception {
		given(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function(){}; require( 'appns/Class2' )")
			.and(aspect).containsFileWithContents("src/appns/Class2.js", "appns.Class2 = function(){};")
			.and(aspect).indexPageRequires("appns/Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("appns.Class2 = function(){};");
	}
	
	@Test
	public void weBundleFilesRequiredFromAnAspect() throws Exception {
		given(aspect).containsFileWithContents("src/appns/App.js", "var App = function() {};  module.exports = App;")
			.and(aspect).indexPageHasContent("var App = require('appns/App')");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("var App = function() {};  module.exports = App;");
	}
	
	@Test
	public void circularDependenciesCauseAnExceptionToBeThrown() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).indexPageHasContent("appns.Class1")
			.and(aspect).classExtends("appns.Class1", "appns.Class2")
			.and(aspect).classExtends("appns.Class2", "appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyException(CircularDependencyException.class, "appns/Class1", "appns/Class2");
	}
	
	@Test
	public void indirectCircularDependenciesCauseAnExceptionToBeThrown() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).indexPageHasContent("appns.Class1")
			.and(aspect).classExtends("appns.Class1", "appns.Class2")
			.and(aspect).classExtends("appns.Class2", "appns.Class3")
			.and(aspect).classExtends("appns.Class3", "appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyException(CircularDependencyException.class, "appns/Class1", "appns/Class2", "appns/Class3");
	}
}
