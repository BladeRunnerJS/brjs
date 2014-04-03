package org.bladerunnerjs.spec.aliasing;

import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class AliasBundlingTest extends SpecTest {
	private App app;
	private AppConf appConf;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private JsLib brLib, otherBrLib;
	private AliasesFile aspectAliasesFile;
	private AliasDefinitionsFile brLibAliasDefinitionsFile, bladeAliasDefinitionsFile;
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
			aspectAliasesFile = aspect.aliasesFile();
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			bladeAliasDefinitionsFile = blade.assetLocation("src").aliasDefinitionsFile();
			brLib = app.jsLib("br");
			brLibAliasDefinitionsFile = brLib.assetLocation("resources").aliasDefinitionsFile();
			otherBrLib = brjs.sdkLib("otherBrLib");
			
	}
	
	// SDK AliasDefinitions
	@Test
	public void sdkLibAliasDefinitionsShouldNotGetScannedForDependenciesIfTheClassesAreNotReferencedViaIndexPage() throws Exception {
		given(brLib).hasClass("br.Class1")
			.and(brLibAliasDefinitionsFile).hasAlias("br.test-class", "otherBrLib.TestClass")
			.and(otherBrLib).classFileHasContent("otherBrLib.TestClass", "I should not be bundled")
			.and(aspect).indexPageRefersTo("br.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).doesNotContainText("otherBrLib.TestClass")
			.and(response).doesNotContainText("I should not be bundled");
	}
	
	@Test
	public void sdkLibAliasDefinitionsShouldNotGetScannedForDependenciesIfTheClassesAreNotReferencedViaAspectClass() throws Exception {
		given(brLib).hasClass("br.Class1")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias-class", "otherBrLib.TestClass")
			.and(otherBrLib).classFileHasContent("otherBrLib.TestClass", "I should not be bundled")
			.and(aspect).hasNodeJsPackageStyle()
			.and(aspect).classFileHasContent("appns.Class1", "'br.alias-class'")
			.and(aspect).indexPageRefersTo("br.Class1", "appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).doesNotContainText("otherBrLib.TestClass")
			.and(response).doesNotContainText("I should not be bundled");
	}
	
	@Test
	public void sdkLibAliasDefinitionsReferencesAreBundledIfTheyAreReferencedViaIndexPage() throws Exception {
		given(brLib).hasClasses("br.Class1", "br.Class2")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias", "br.Class2")
			.and(aspect).indexPageHasAliasReferences("br.alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("br.Class2");
	}
	
	// TODO: refactor/remove these tests once we have a more thought-through support for alias and service dependency analysis
	// e.g. require('alias!someAlias') and require('service!someService');
	@Test
	public void aliasClassesReferencedByANodeJSSourceModuleAreIncludedInTheBundle() throws Exception {
		given(brLib).hasClasses("br.Class1", "br.Class2")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias", "br.Class2")
			.and(aspect).hasNodeJsPackageStyle()
			.and(aspect).classFileHasContent("Class1", "aliasRegistry.getAlias('br.alias')")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("br.Class2");
	}
	@Test
	public void serviceClassesReferencedByANodeJSSourceModuleAreIncludedInTheBundle() throws Exception {
		given(brLib).hasClasses("br.Class1", "br.Class2")
			.and(brLibAliasDefinitionsFile).hasAlias("br.service", "br.Class2")
			.and(aspect).hasNodeJsPackageStyle()
			.and(aspect).classFileHasContent("Class1", "serviceRegistry.getService('br.service')")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("br.Class2");
	}
	@Test // test exception isnt thrown for services - services can be defined and configure at run time, which differs from aliases
	public void anExceptionIsntThrownIfAServiceClassesReferencedByANodeJSSourceModuleDoesntExist() throws Exception {
		given(brLib).hasClasses("br.Class1", "br.Class2")
    		.and(aspect).hasNodeJsPackageStyle()
    		.and(aspect).classFileHasContent("Class1", "serviceRegistry.getService('br.service')")
    		.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	// -----------------------------------
	
	@Test
	public void weBundleAClassWhoseAliasIsReferredToInTheIndexPage() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageHasAliasReferences("the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1");
	}
	
	@Test
	public void weBundleAClassIfItTheAliasReferenceIsInDoubleQuotes() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageHasContent("\"the-alias\"");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1");
	}
	
	@Test
	public void weBundleAClassIfItTheAliasReferenceIsInXmlTag() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageHasContent("<the-alias attr='val'/>");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1");
	}
	
	// Blade/Aspect AliasDefinitions
	@Test
	public void weAlsoBundleAClassIfTheAliasIsDefinedInABladeAliasDefinitionsXml() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.the-alias");	
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1");
	}
	
	public void weBundleAClassWhoseAliasIsReferredToFromAnotherNodeJsClass() throws Exception {
		given(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1", "aliasRegistry.getAlias('the-alias')");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsDefinedClasses("appns/Class1", "appns/Class2");
	}
	
	@Test
	public void weBundleAClassWhoseAliasIsReferredToFromAnotherNamespacedClass() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classRefersToAlias("appns.Class1", "the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1", "appns.Class2");
	}
	
	@Test
	public void bundlingWorksForAliasesDefinedAtTheBladeLevel() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1");
	}
	
	@Test
	public void weBundleTheCorrespondingInterfaceForAliasesThatSpecifyAnInterface() throws Exception {
		given(aspect).hasClasses("appns.TheClass", "appns.TheInterface")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.TheClass", "appns.TheInterface")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsOrderedTextFragments("define('appns/TheInterface'", "define('appns/TheClass'"); // TODO: create a containsOrderedClass() method once Andy Berry has finished the test re-factoring
	}
	
	@Test
	public void weBundleTheDependenciesOfClassesIncludedViaAlias() throws Exception {
		given(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).classRequires("appns.Class1", "appns.Class2")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageHasAliasReferences("the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1", "appns.Class2");
	}
	
	@Test
	public void weDoNotBundleAClassIfADefinedAliasIsNotReferenced() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).doesNotContainClasses("appns.Class1");
	}
	
	@Test
	public void aliasesAreNotSetIfTheAliasRegistryClassDoesntExist() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/aliasing/bundle.js", response);
		then(response).doesNotContainText("setAliasData(");
	}
	
	@Test
	public void aliasesAreNotSetIfTheAliasRegistryClassIsNotUsed() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(brLib).hasClass("br.AliasRegistry")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/aliasing/bundle.js", response);
		then(response).doesNotContainText("setAliasData(");
	}
	
	@Test
	public void theAliasBlobIsEmptyIfNoAliasesAreUsed() throws Exception {
		given(aspect).classRequires("appns.Class1", "br/AliasRegistry")
			.and(brLib).hasClass("br.AliasRegistry")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/aliasing/bundle.js", response);
		then(response).containsText("setAliasData({});\n");
	}
	
	@Test
	public void theAliasBlobContainsAClassReferencedByAlias() throws Exception {
		given(aspect).classRequires("appns.Class1", "br/AliasRegistry")
			.and(brLib).hasClass("br.AliasRegistry")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageHasAliasReferences("the-alias");
		when(app).requestReceived("/default-aspect/aliasing/bundle.js", response);
		then(response).containsText("setAliasData({'the-alias':{'class':require('appns/Class1'),'className':'appns.Class1'}})");
	}
	
	@Test
	public void anExceptionIsThrownIfTheClassReferredToByAnAliasDoesntExist() throws Exception {
		given(aspectAliasesFile).hasAlias("the-alias", "NonExistentClass")
			.and(aspect).indexPageHasAliasReferences("the-alias");
		when(app).requestReceived("/default-aspect/aliasing/bundle.js", response);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "NonExistentClass");
	}
	
	@Test
	public void anExceptionIsThrownIfTheInterfaceReferredToByAnAliasDoesntExist() throws Exception {
		given(aspect).hasClass("appns.TheClass")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.TheClass", "NonExistentInterface")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/aliasing/bundle.js", response);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "NonExistentInterface");
	}
}
