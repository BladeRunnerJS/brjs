package org.bladerunnerjs.spec.aliasing;

import org.bladerunnerjs.aliasing.AliasNameIsTheSameAsTheClassException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.Workbench;
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
	private Workbench workbench;
	private AliasesFile worbenchAliasesFile;
	private Bladeset defaultBladeset;
	private Blade bladeInDefaultBladeset;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			appConf = app.appConf();
			aspect = app.aspect("default");
			aspectAliasesFile = aspect.aliasesFile();
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			bladeAliasDefinitionsFile = blade.assetLocation("src").aliasDefinitionsFile();
			workbench = blade.workbench();
			worbenchAliasesFile = workbench.aliasesFile();
			brLib = app.jsLib("br");
			brLibAliasDefinitionsFile = brLib.assetLocation("resources").aliasDefinitionsFile();
			otherBrLib = brjs.sdkLib("otherBrLib");
			defaultBladeset = app.bladeset("default");
			bladeInDefaultBladeset = defaultBladeset.blade("b1");
	}
	
	// SDK AliasDefinitions
	@Test
	public void sdkLibAliasDefinitionsShouldNotGetScannedForDependenciesIfTheClassesAreNotReferencedViaIndexPage() throws Exception {
		given(brLib).hasClass("br/Class1")
			.and(brLibAliasDefinitionsFile).hasAlias("br.test-class", "otherBrLib.TestClass")
			.and(otherBrLib).classFileHasContent("otherBrLib.TestClass", "I should not be bundled")
			.and(aspect).indexPageRefersTo("br.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).doesNotContainText("otherBrLib.TestClass")
			.and(response).doesNotContainText("I should not be bundled");
	}
	
	@Test
	public void sdkLibAliasDefinitionsShouldNotGetScannedForDependenciesIfTheClassesAreNotReferencedViaAspectClass() throws Exception {
		given(brLib).hasClass("br/Class1")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias-class", "otherBrLib.TestClass")
			.and(otherBrLib).classFileHasContent("otherBrLib.TestClass", "I should not be bundled")
			.and(aspect).hasCommonJsPackageStyle()
			.and(aspect).classFileHasContent("appns.Class1", "'br.alias-class'")
			.and(aspect).indexPageRefersTo("br.Class1", "appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).doesNotContainText("otherBrLib.TestClass")
			.and(response).doesNotContainText("I should not be bundled");
	}
	
	@Test
	public void sdkLibAliasDefinitionsReferencesAreBundledIfTheyAreReferencedViaIndexPage() throws Exception {
		given(brLib).hasClasses("br/Class1", "br/Class2")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias", "br.Class2")
			.and(aspect).indexPageHasAliasReferences("br.alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("br/Class2");
	}
	
	// TODO: refactor/remove these tests once we have a more thought-through support for alias and service dependency analysis
	// e.g. require('alias!someAlias') and require('service!someService');
	@Test
	public void aliasClassesReferencedByACommonJsSourceModuleAreIncludedInTheBundle() throws Exception {
		given(brLib).hasClasses("br/Class1", "br/Class2")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias", "br.Class2")
			.and(aspect).hasCommonJsPackageStyle()
			.and(aspect).classFileHasContent("Class1", "aliasRegistry.getAlias('br.alias')")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("br/Class2");
	}
	
	@Test
	public void serviceClassesReferencedByACommonJsSourceModuleAreIncludedInTheBundle() throws Exception {
		given(brLib).hasClasses("br/Class1", "br/Class2")
			.and(brLibAliasDefinitionsFile).hasAlias("br.service", "br.Class2")
			.and(aspect).hasCommonJsPackageStyle()
			.and(aspect).classFileHasContent("Class1", "serviceRegistry.getService('br.service')")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("br/Class2");
	}
	
	@Test // test exception isnt thrown for services - services can be defined and configure at run time, which differs from aliases
	public void anExceptionIsntThrownIfAServiceClassesReferencedByACommonJsSourceModuleDoesntExist() throws Exception {
		given(brLib).hasClasses("br/Class1", "br/Class2")
    		.and(aspect).hasCommonJsPackageStyle()
    		.and(aspect).classFileHasContent("Class1", "serviceRegistry.getService('br.service')")
    		.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	// -----------------------------------
	
	@Test
	public void weBundleAClassWhoseAliasIsReferredToInTheIndexPage() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageHasAliasReferences("the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1");
	}
	
	@Test
	public void weBundleAClassIfItTheAliasReferenceIsInDoubleQuotes() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageHasContent("\"the-alias\"");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1");
	}
	
	@Test
	public void weBundleAClassIfItTheAliasReferenceIsInXmlTag() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageHasContent("<the-alias attr='val'/>");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1");
	}
	
	@Test
	public void weBundleAClassIfItTheAliasReferenceIsInASelfClosingXmlTag() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageHasContent("<the-alias/>");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1");
	}
	
	// Blade/Aspect AliasDefinitions
	@Test
	public void weAlsoBundleAClassIfTheAliasIsDefinedInABladeAliasDefinitionsXml() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns/Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.the-alias");	
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1");
	}
	
	public void weBundleAClassWhoseAliasIsReferredToFromAnotherCommonJsClass() throws Exception {
		given(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1", "aliasRegistry.getAlias('the-alias')");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsDefinedClasses("appns/Class1", "appns/Class2");
	}
	
	@Test
	public void weBundleAClassWhoseAliasIsReferredToFromAnotherNamespacedClass() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classDependsOnAlias("appns.Class1", "the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1", "appns.Class2");
	}
	
	@Test
	public void bundlingWorksForAliasesDefinedAtTheBladeLevel() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns/Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1");
	}
	
	@Test
	public void weBundleTheCorrespondingInterfaceForAliasesThatSpecifyAnInterface() throws Exception {
		given(aspect).hasClasses("appns/TheClass", "appns/TheInterface")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.TheClass", "appns.TheInterface")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsOrderedTextFragments("define('appns/TheInterface'", "define('appns/TheClass'"); // TODO: create a containsOrderedClass() method once Andy Berry has finished the test re-factoring
	}
	
	@Test
	public void weBundleTheDependenciesOfClassesIncludedViaAlias() throws Exception {
		given(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).classRequires("appns/Class1", "appns.Class2")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageHasAliasReferences("the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1", "appns.Class2");
	}
	
	@Test
	public void weDoNotBundleAClassIfADefinedAliasIsNotReferenced() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns/Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).doesNotContainClasses("appns.Class1");
	}
	
	@Test
	public void aliasesAreNotSetIfTheAliasRegistryClassDoesntExist() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("aliasing/bundle.js", response);
		then(response).doesNotContainText("setAliasData(");
	}
	
	@Test
	public void aliasesAreNotSetIfTheAliasRegistryClassIsNotUsed() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(brLib).hasClass("br/AliasRegistry")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("aliasing/bundle.js", response);
		then(response).doesNotContainText("setAliasData(");
	}
	
	@Test
	public void theAliasBlobIsEmptyIfNoAliasesAreUsed() throws Exception {
		given(aspect).classRequires("appns/Class1", "br/AliasRegistry")
			.and(brLib).hasClass("br/AliasRegistry")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("aliasing/bundle.js", response);
		then(response).containsText("setAliasData({});\n");
	}
	
	@Test
	public void theAliasBlobContainsAClassReferencedByAlias() throws Exception {
		given(aspect).classRequires("appns/Class1", "br/AliasRegistry")
			.and(brLib).hasClass("br/AliasRegistry")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageHasAliasReferences("the-alias");
		when(aspect).requestReceivedInDev("aliasing/bundle.js", response);
		then(response).containsText("setAliasData({'the-alias':{'class':require('appns/Class1'),'className':'appns.Class1'}})");
	}
	
	@Test
	public void anExceptionIsThrownIfTheClassReferredToByAnAliasDoesntExist() throws Exception {
		given(aspectAliasesFile).hasAlias("the-alias", "NonExistentClass")
			.and(aspect).indexPageHasAliasReferences("the-alias");
		when(aspect).requestReceivedInDev("aliasing/bundle.js", response);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "NonExistentClass");
	}
	
	@Test
	public void anExceptionIsThrownIfTheInterfaceReferredToByAnAliasDoesntExist() throws Exception {
		given(aspect).hasClass("appns/TheClass")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.TheClass", "NonExistentInterface")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.the-alias");
		when(aspect).requestReceivedInDev("aliasing/bundle.js", response);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "NonExistentInterface");
	}
	
	@Test
	public void aliasesDefinedInMultipleLocationsDontCauseATrieException() throws Exception {
		given(brLib).hasClasses("br/Class1")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias", "br.Class1")
			.and(aspect).hasClass("appns/Class1")
			.and(aspectAliasesFile).hasAlias("br.alias", "appns.Class1")
			.and(aspect).indexPageRefersTo("aliasRegistry.getAlias('br.alias')")
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(worbenchAliasesFile).hasAlias("br.alias", "appns/bs/b1/Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	
	@Test
	public void multipleAliasDefinitionsCanBeInAnAssetContainer() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns/pkg1/Class1", "appns/pkg1/pkg2/Class2", "appns/pkg1/pkg2/pkg3/Class3")
			.and(aspect.assetLocation("src/appns/pkg1").aliasDefinitionsFile()).hasAlias("appns.alias1", "appns.pkg1.Class1")
			.and(aspect.assetLocation("src/appns/pkg1/pkg2").aliasDefinitionsFile()).hasAlias("appns.alias2", "appns.pkg1.pkg2.Class2")
			.and(aspect.assetLocation("src/appns/pkg1/pkg2/pkg3").aliasDefinitionsFile()).hasAlias("appns.alias3", "appns.pkg1.pkg2.pkg3.Class3")
			.and(aspect).indexPageHasAliasReferences("\"appns.alias1\" 'appns.alias2' \"appns.alias3\"");	
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.pkg1.Class1", "appns.pkg1.pkg2.Class2", "appns.pkg1.pkg2.pkg3.Class3");
	}
	
	@Test
	public void unknownClassRepresntingAbstractAliasesIsSetAsANullClass() throws Exception {
		given(aspect).hasClasses("appns/TheInterface")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", null, "appns/TheInterface")
			.and(aspect).indexPageHasContent("br.AliasRegistry('appns.bs.b1.the-alias');")
			.and(brLib).hasClasses("br/UnknownClass", "br/AliasRegistry");
		when(aspect).requestReceivedInDev("aliasing/bundle.js", response);			
		then(response).containsText("require('br/AliasRegistry').setAliasData({'appns.bs.b1.the-alias':{'interface':appns/TheInterface,'interfaceName':'appns/TheInterface'}});");
	}
	
	@Test
	public void multipleAliasDefinitionsCanBeInsideResourcesFolderAndSubfoldersAndUsedInANamespacedClass() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.App", "appns.Class1", "appns.Class2", "appns.Class3")
			.and(aspect.assetLocation("resources").aliasDefinitionsFile()).hasAlias("appns.alias1", "appns.Class1")
			.and(aspect).containsFileWithContents("resources/subfolder/aliasDefinitions.xml",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?><aliasDefinitions xmlns=\"http://schema.caplin.com/CaplinTrader/aliasDefinitions\">\n" +
						"<alias defaultClass=\"appns.Class2\" name=\"appns.alias2\"/>\n" +
					"</aliasDefinitions>")
			.and(aspect).containsFileWithContents("resources/subfolder/subfolder/aliasDefinitions.xml",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?><aliasDefinitions xmlns=\"http://schema.caplin.com/CaplinTrader/aliasDefinitions\">\n" +
							"<alias defaultClass=\"appns.Class3\" name=\"appns.alias3\"/>\n" +
						"</aliasDefinitions>")
			.and(aspect).indexPageRefersTo("appns.App")	
			.and(aspect).classFileHasContent("appns.App", "'appns.alias1' 'appns.alias2' 'appns.alias3'");	
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1", "appns.Class2", "appns.Class3");
	}
	
	@Test
	public void multipleAliasDefinitionsCanBeInsideResourcesFolderAndSubfoldersAndUsedInACommonJsClass() throws Exception {
		given(aspect).hasCommonJsPackageStyle()
			.and(aspect).hasClasses("appns/App", "appns/Class1", "appns/Class2", "appns/Class3")
			.and(aspect.assetLocation("resources").aliasDefinitionsFile()).hasAlias("appns.alias1", "appns.Class1")
			.and(aspect).containsFileWithContents("resources/subfolder/aliasDefinitions.xml",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?><aliasDefinitions xmlns=\"http://schema.caplin.com/CaplinTrader/aliasDefinitions\">\n" +
						"<alias defaultClass=\"appns.Class2\" name=\"appns.alias2\"/>\n" +
					"</aliasDefinitions>")
			.and(aspect).containsFileWithContents("resources/subfolder/subfolder/aliasDefinitions.xml",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?><aliasDefinitions xmlns=\"http://schema.caplin.com/CaplinTrader/aliasDefinitions\">\n" +
							"<alias defaultClass=\"appns.Class3\" name=\"appns.alias3\"/>\n" +
						"</aliasDefinitions>")
			.and(aspect).indexPageRequires("appns/App")	
			.and(aspect).classFileHasContent("appns.App", "sr.getService('appns.alias1'); sr.getService('appns.alias2'); sr.getService('appns.alias3');");	
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1", "appns.Class2", "appns.Class3");
	}
	
	@Test
	public void anExceptionIsThrownInTheAliasNameIsTheSameAsTheDefaultClass() throws Exception {
		given(aspect).hasClasses("appns/Class1")
			.and(aspect.assetLocation("src").aliasDefinitionsFile()).hasAlias("appns.Class1", "appns.Class1")
			.and(aspect).indexPageHasAliasReferences("appns.Class1");	
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(AliasNameIsTheSameAsTheClassException.class, "appns.Class1");
	}
	
	@Test
	public void anExceptionIsThrownInTheAliasNameIsTheSameAsTheAssignedClass() throws Exception {
		given(aspect).hasClasses("appns/Class1", "appns/AliasClass")
			.and(aspect.assetLocation("src").aliasDefinitionsFile()).hasAlias("appns.AliasClass", "appns.Class1")
			.and(aspectAliasesFile).hasAlias("appns.AliasClass", "appns.AliasClass")
			.and(aspect).indexPageHasAliasReferences("appns.AliasClass");	
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(AliasNameIsTheSameAsTheClassException.class, "appns.AliasClass");
	}
	
	@Test
	public void theAliasBlobContainsAliasDefinitionsOnlyOnceEvenIfTheyAreReferencedMultipleTimes() throws Exception {
		given(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).classFileHasContent("appns/Class1", "require('br/AliasRegistry').getService('the-alias');")
			.and(aspect).classFileHasContent("appns/Class2", "require('br/AliasRegistry').getService('the-alias');")
			.and(brLib).hasClass("br/AliasRegistry")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageRequires("appns/Class1");
		when(aspect).requestReceivedInDev("aliasing/bundle.js", response);
		then(response).containsText("setAliasData({'the-alias':{'class':require('appns/Class1'),'className':'appns.Class1'}})");
	}
	
	@Test
	public void aliasesInDefaultBladesetCanBeBundled() throws Exception {
		given(bladeInDefaultBladeset).hasClasses("appns/b1/BladeClass", "appns/b1/Class1")
			.and(bladeInDefaultBladeset).classFileHasContent("appns/b1/BladeClass", "require('br/AliasRegistry').getService('the-alias');")
    		.and(aspectAliasesFile).hasAlias("the-alias", "appns.b1.Class1")
    		.and(aspect).indexPageRequires("appns/b1/BladeClass")
    		.and(brLib).hasClass("br/AliasRegistry");
		when(aspect).requestReceivedInDev("aliasing/bundle.js", response);
    	then(response).containsText("setAliasData({'the-alias':{'class':require('appns/b1/Class1'),'className':'appns.b1.Class1'}})");
	}
	
}
