package org.bladerunnerjs.spec.aliasing;

import static org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility.aliasDefinitionsFile;
import static org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility.aliasesFile;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.BladeWorkbench;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.model.exception.NamespaceException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.plugin.bundlers.aliasing.AmbiguousAliasException;
import org.junit.Before;
import org.junit.Test;

public class AppLevelAliasOverrideTest extends SpecTest {

	private App app;
	private AppConf appConf;
	private Aspect aspect;
	private JsLib brLib;
	private AliasesFileBuilder appAliasesFile, aspectAliasesFile, workbenchAliasesFile;
	private AliasDefinitionsFileBuilder aspectAliaseDefinitionsFile;
	private AliasDefinitionsFileBuilder brLibAliasDefinitionsFile;
	private SdkJsLib servicesLib;
	private StringBuffer response = new StringBuffer();
	private StringBuffer responseWithoutAliasesFile = new StringBuffer();
	private Bladeset bladeset;
	private Blade blade, bladeForWorkbenchWithoutAliasesFile;
	private BladeWorkbench workbench;
	private Aspect aspectWithoutAliasesFile;
	private BladeWorkbench workbenchWithoutAliasesFile;
	private TestPack bladeTestPack;
	private AliasesFileBuilder bladeTestPackAliasesFile;
	private AliasDefinitionsFileBuilder bladesetAliasDefinitionsFile;
	private AliasDefinitionsFileBuilder bladeAliasDefinitionsFile;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()			
			.and(brjs).hasBeenCreated();
		app = brjs.app("app1");
		appConf = app.appConf();
		aspect = app.aspect("default");
		aspectWithoutAliasesFile = app.aspect("withoutAliasesFile");
		bladeset = app.bladeset("bs");
		blade = bladeset.blade("b1");
		bladeTestPack = blade.testType("ut").defaultTestTech();
		bladeForWorkbenchWithoutAliasesFile = bladeset.blade("b2");
		brLib = app.jsLib("br");
		workbench = blade.workbench();
		workbenchWithoutAliasesFile = bladeForWorkbenchWithoutAliasesFile.workbench();
		workbenchAliasesFile = new AliasesFileBuilder(this, aliasesFile(workbench));
		appAliasesFile = new AliasesFileBuilder(this, aliasesFile(app));
		aspectAliasesFile = new AliasesFileBuilder(this, aliasesFile(aspect));
		brLibAliasDefinitionsFile = new AliasDefinitionsFileBuilder(this, aliasDefinitionsFile(brLib, "resources"));
		aspectAliaseDefinitionsFile = new AliasDefinitionsFileBuilder(this, aliasDefinitionsFile(aspect, "resources"));
		bladesetAliasDefinitionsFile = new AliasDefinitionsFileBuilder(this, aliasDefinitionsFile(bladeset, "resources"));
		bladeAliasDefinitionsFile = new AliasDefinitionsFileBuilder(this, aliasDefinitionsFile(blade, "resources"));
		bladeTestPackAliasesFile = new AliasesFileBuilder(this, aliasesFile(bladeTestPack));
		
		servicesLib = brjs.sdkLib("ServicesLib");
		given(servicesLib).containsFileWithContents("br-lib.conf", "requirePrefix: br")
			.and(servicesLib).hasClasses("br/AliasRegistry", "br/ServiceRegistry");
	}
	
	@Test
	public void aliasDefinitionsInLibsAreOverridenByTheAppAliases() throws Exception {
		given(brLib).hasClasses("br/LibAliasClass", "br/LibClass")
			.and(brLibAliasDefinitionsFile).hasAlias("br.libAlias", "br.LibAliasClass")
			.and(aspect).hasClass("appns/AliasClass")
			.and(appAliasesFile).hasAlias("br.libAlias", "appns.AliasClass")
			.and(aspect).indexPageHasContent("require('br/LibClass'); 'br.libAlias'");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("br/LibClass", "appns/AliasClass")
			.and(response).doesNotContainText("br/LibAliasClass");
	}
	
	@Test
	public void aliasDefinitionsInLibsAreOverridenByTheAppAliases_AndEmptyAspectAliasesFileExists() throws Exception {
		given(brLib).hasClasses("br/LibAliasClass", "br/LibClass")
			.and(brLibAliasDefinitionsFile).hasAlias("br.libAlias", "br.LibAliasClass")
			.and(aspect).hasClass("appns/AliasClass")
			.and(appAliasesFile).hasAlias("br.libAlias", "appns.AliasClass")
			.and(aspectAliasesFile).exists()
			.and(aspect).indexPageHasContent("require('br/LibClass'); 'br.libAlias'");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("br/LibClass", "appns/AliasClass")
			.and(response).doesNotContainText("br/LibAliasClass");
	}
	
	@Test
	public void aliasDefinitionsInAspectAreOverridenByTheAppAliases() throws Exception {
		given(aspect).hasClasses("appns/AspectConfiguredAlias", "appns/AppConfiguredAlias")
			.and(aspectAliaseDefinitionsFile).hasAlias("appns.aspectAlias1", "appns.AspectConfiguredAlias")
			.and(appAliasesFile).hasAlias("appns.aspectAlias1", "appns.AppConfiguredAlias")
			.and(aspect).indexPageHasContent("'appns.aspectAlias1'");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns/AppConfiguredAlias")
			.and(response).doesNotContainText("appns/AspectConfiguredAlias");
	}
	
	@Test
	public void aliasDefinitionsInAspectAreOverridenByTheAppAliases_AndEmptyAspectAliasesFileExists() throws Exception {
		given(aspect).hasClasses("appns/AspectConfiguredAlias", "appns/AppConfiguredAlias")
    		.and(aspectAliaseDefinitionsFile).hasAlias("appns.aspectAlias1", "appns.AspectConfiguredAlias")
    		.and(appAliasesFile).hasAlias("appns.aspectAlias1", "appns.AppConfiguredAlias")
    		.and(aspectAliasesFile).exists()
    		.and(aspect).indexPageHasContent("'appns.aspectAlias1'");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns/AppConfiguredAlias")
			.and(response).doesNotContainText("appns/AspectConfiguredAlias");
	}
	
	@Test
	public void aliasDefinitionsInBladeAreOverridenByTheAppAliases() throws Exception {
		given(blade).hasClasses("appns/bs/b1/BladeAliasClass", "appns/bs/b1/BladeClass")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.bladeAlias", "appns.bs.b1.BladeAliasClass")
			.and(aspect).hasClasses("appns/AliasClass")
			.and(appAliasesFile).hasAlias("appns.bs.b1.bladeAlias", "appns.AliasClass")
			.and(aspect).indexPageHasContent("require('appns/bs/b1/BladeClass'); 'appns.bs.b1.bladeAlias'");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns/AliasClass", "appns/bs/b1/BladeClass")
			.and(response).doesNotContainText("appns/bs/b1/BladeAliasClass");
	}
	
	@Test
	public void aliasDefinitionsInBladeAreOverridenByTheAppAliases_AndEmptyAspectAliasesFileExists() throws Exception {
		given(blade).hasClasses("appns/bs/b1/BladeAliasClass", "appns/bs/b1/BladeClass")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.bladeAlias", "appns.bs.b1.BladeAliasClass")
			.and(aspect).hasClasses("appns/AliasClass")
			.and(appAliasesFile).hasAlias("appns.bs.b1.bladeAlias", "appns.AliasClass")
			.and(aspectAliasesFile).exists()
			.and(aspect).indexPageHasContent("require('appns/bs/b1/BladeClass'); 'appns.bs.b1.bladeAlias'");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns/AliasClass", "appns/bs/b1/BladeClass")
			.and(response).doesNotContainText("appns/bs/b1/BladeAliasClass");
	}
	
	@Test
	public void aliasesAreTakenFromBothAppAliasesAndBladeAliasDefinitionsForDifferentAliases() throws Exception {
		given(aspect).hasClasses("appns/AspectClass1", "appns/AspectClass2")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.bladeAliasDefinitionForClass1", "appns.AspectClass1")
			.and(appAliasesFile).hasAlias("appns.appAliasForClass2", "appns.AspectClass2")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.bladeAliasDefinitionForClass1", "appns.appAliasForClass2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.AspectClass1", "appns.AspectClass2");
	}
	
	@Test
	public void aliasesAreTakenFromBothAppAliasesAndBladeAliasDefinitionsForDifferentAliases_AndEmptyAspectAliasesFileExists() throws Exception {
		given(aspect).hasClasses("appns/AspectClass1", "appns/AspectClass2")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.bladeAliasDefinitionForClass1", "appns.AspectClass1")
			.and(appAliasesFile).hasAlias("appns.appAliasForClass2", "appns.AspectClass2")
			.and(aspectAliasesFile).exists()
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.bladeAliasDefinitionForClass1", "appns.appAliasForClass2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.AspectClass1", "appns.AspectClass2");
	}
	
	@Test
	public void appAliasesHavePriorityIfAliasesArePresentInBothAppAliasesAndBladeAliasDefinitionsAndAlsoContainUniqueOnes() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns/AspectClass1", "appns/AspectClass2", "appns/AspectClass3")
			.and(blade).hasClasses("appns/bs/b1/BladeClass1", "appns/bs/b1/BladeClass2")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.bladeAliasDefinition1", "appns.bs.b1.BladeClass1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.bladeAliasDefinition2", "appns.bs.b1.BladeClass2")
			.and(appAliasesFile).hasAlias("appns.AppOverrideForAspectClass2", "appns.AspectClass2")
			.and(appAliasesFile).hasAlias("appns.bs.b1.bladeAliasDefinition2", "appns.AspectClass3")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.bladeAliasDefinition1", "appns.AppOverrideForAspectClass2", "appns.bs.b1.bladeAliasDefinition2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.b1.BladeClass1", "appns.AspectClass2", "appns.AspectClass3");
	}
	
	@Test
	public void aspectAliasesOverridesAppAliases() throws Exception {
		given(aspect).hasClasses("appns/App", "appns/Class1", "appns/Class2")
			.and(aspectAliasesFile).hasAlias("appns.aspectAlias", "appns.Class2")
			.and(appAliasesFile).hasAlias("appns.aspectAlias", "appns.Class1")
			.and(aspect).indexPageHasAliasReferences("appns.aspectAlias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class2");
	}
	
	@Test
	public void workbenchAliasesOverridesAppAliases() throws Exception {
		given(workbench).hasClasses("appns/WorkbenchClass1", "appns/WorkbenchClass2")
			.and(workbenchAliasesFile).hasAlias("workbenchAlias", "appns.WorkbenchClass2")
			.and(appAliasesFile).hasAlias("workbenchAlias", "appns.WorkbenchClass1")
			.and(workbench).indexPageRequires("alias!workbenchAlias");
		when(workbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.WorkbenchClass2");
	}
	
	@Test
	public void testPackAliasesOverrideAppAliases() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns/Class1")
			.and(bladeTestPack).hasClass("test/Class1")
			.and(bladeTestPackAliasesFile).hasAlias("testAlias", "appns.bs.b1.test.Class1")
			.and(appAliasesFile).hasAlias("testAlias", "appns.Class2")
			.and(bladeTestPack).testRequires("pkg/test.js", "alias!testAlias");
		when(bladeTestPack).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.b1.test.Class1");
	}
	
	@Test
	public void aspectsUseTheCorrectAliasesWhenOneHasAnAliasesFileAndAnotherDoes() throws Exception {
		given(aspectWithoutAliasesFile).hasClasses("appns/Class1WithoutAlias", "appns/Class2WithoutAlias")
			.and(aspect).hasClasses("appns/App", "appns/Class1", "appns/Class2")
			.and(aspectAliasesFile).hasAlias("appns.aspectAliasWithAspectAliasesFile", "appns.Class2")
			.and(appAliasesFile).hasAlias("appns.aspectAliasWithAspectAliasesFile", "appns.Class1")
			.and(appAliasesFile).hasAlias("appns.aspectAliasWithoutAspectAliasesFile", "appns.Class1WithoutAlias")
			.and(aspect).indexPageHasAliasReferences("appns.aspectAliasWithAspectAliasesFile")
			.and(aspectWithoutAliasesFile).indexPageHasAliasReferences("appns.aspectAliasWithoutAspectAliasesFile");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response)
			.and(aspectWithoutAliasesFile).requestReceivedInDev("js/dev/combined/bundle.js", responseWithoutAliasesFile);
		then(response).containsCommonJsClasses("appns.Class2")
			.and(responseWithoutAliasesFile).containsCommonJsClasses("appns.Class1WithoutAlias");
	}
	
	@Test
	public void workbenchesUseTheCorrectAliasesWhenOneHasAnAliasesFileAndAnotherDoes() throws Exception {
		given(workbenchWithoutAliasesFile).hasClasses("appns/Class1WithoutAlias", "appns/Class2WithoutAlias")
			.and(workbench).hasClasses("appns/App", "appns/Class1", "appns/Class2")
			.and(workbenchAliasesFile).hasAlias("appns.aspectAliasWithAspectAliasesFile", "appns.Class2")
			.and(appAliasesFile).hasAlias("appns.aspectAliasWithAspectAliasesFile", "appns.Class1")
			.and(appAliasesFile).hasAlias("appns.aspectAliasWithoutAspectAliasesFile", "appns.Class1WithoutAlias")
			.and(workbench).indexPageRequires("alias!appns.aspectAliasWithAspectAliasesFile")
			.and(workbenchWithoutAliasesFile).indexPageRequires("alias!appns.aspectAliasWithoutAspectAliasesFile");
		when(workbench).requestReceivedInDev("js/dev/combined/bundle.js", response)
			.and(workbenchWithoutAliasesFile).requestReceivedInDev("js/dev/combined/bundle.js", responseWithoutAliasesFile);
		then(response).containsCommonJsClasses("appns.Class2")
			.and(responseWithoutAliasesFile).containsCommonJsClasses("appns.Class1WithoutAlias");
	}
	
	
	
	/* Alias Group and Scenrio Tests */
	
	@Test
	public void settingMultipleGroupsChangesTheAliasesThatAreUsed_usingTheAppToSetTheGroup_usingTheAppToSetTheGroup() throws Exception {
		given(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "br.alias1", "appns.bs.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "br.alias2", "appns.bs.Class2")
			.and(bladeset).hasClasses("Class1", "Class2")
			.and(appAliasesFile).usesGroups("appns.bs.b1.g1", "appns.bs.b1.g2")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias1", "br.Class")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias2", "br.Class")
			.and(aspect).indexPageHasAliasReferences("br.alias1", "br.alias2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("module.exports = {'br.alias1':{'class':'appns/bs/Class1','className':'appns.bs.Class1'},'br.alias2':{'class':'appns/bs/Class2','className':'appns.bs.Class2'}};");
	}
	
	@Test
	public void theInterfaceIsMaintainedWhenAnAliasIsOverriddenInAGroup_usingTheAppToSetTheGroup() throws Exception {
		given(bladesetAliasDefinitionsFile).hasAlias("appns.bs.the-alias", "appns.bs.Class1", "appns.bs.TheInterface")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.the-alias", "appns.bs.Class2")
			.and(bladeset).hasClasses("Class2", "TheInterface")
			.and(appAliasesFile).usesGroups("appns.bs.b1.g1")
			.and(aspect).indexPageHasAliasReferences("appns.bs.the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("module.exports = {'appns.bs.the-alias':{'class':'appns/bs/Class2','className':'appns.bs.Class2','interface':'appns/bs/TheInterface','interfaceName':'appns.bs.TheInterface'}};");
	}
	
	@Test
	public void groupAliasesCanOverrideNonGroupAliases_usingTheAppToSetTheGroup() throws Exception {
		given(bladesetAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.bs.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "appns.bs.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "appns.bs.b1.the-alias", "appns.bs.Class3")
			.and(bladeset).hasClass("Class2")
			.and(appAliasesFile).usesGroups("appns.bs.b1.g1")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("module.exports = {'appns.bs.b1.the-alias':{'class':'appns/bs/Class2','className':'appns.bs.Class2'}};");
	}
	
	@Test
	public void groupAliasesDoNotNeedToBeNamespaced_usingTheAppToSetTheGroup() throws Exception {
		given(brLibAliasDefinitionsFile).hasAlias("br.the-alias", "br.TheClass")
			.and(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "br.the-alias", "appns.bs.TheClass")
			.and(appAliasesFile).usesGroups("appns.bs.b1.g1")
			.and(bladeset).hasClass("TheClass")
			.and(aspect).indexPageHasAliasReferences("br.the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("module.exports = {'br.the-alias':{'class':'appns/bs/TheClass','className':'appns.bs.TheClass'}};");
	}
	
	@Test
	public void settingAGroupChangesTheAliasesThatAreUsed_usingTheAppToSetTheGroup() throws Exception {
		given(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "br.the-alias", "appns.bs.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "br.the-alias", "appns.bs.Class2")
			.and(bladeset).hasClass("appns/bs/Class2")
			.and(appAliasesFile).usesGroups("appns.bs.b1.g2")
			.and(brLibAliasDefinitionsFile).hasAlias("br.the-alias", "br.Class")
			.and(aspect).indexPageHasAliasReferences("br.the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("module.exports = {'br.the-alias':{'class':'appns/bs/Class2','className':'appns.bs.Class2'}};");
	}
	
	@Test
	public void usingGroupsCanLeadToAmbiguity_usingTheAppToSetTheGroup() throws Exception {
		given(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "br.the-alias", "Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "br.the-alias", "Class2")
			.and(appAliasesFile).usesGroups("appns.bs.b1.g1")
			.and(brLibAliasDefinitionsFile).hasAlias("br.the-alias", "br.Class")
			.and(aspect).indexPageHasAliasReferences("br.the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(AmbiguousAliasException.class, "br.the-alias", appAliasesFile.getUnderlyingFilePath());
	}
	
	@Test
	public void usingGroupsCanLeadToAmbiguityEvenWhenASingleGroupIsUsed_usingTheAppToSetTheGroup() throws Exception {
		given(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "br.the-alias", "Class1")
			.and(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "br.the-alias", "Class2")
			.and(appAliasesFile).usesGroups("appns.bs.b1.g1")
			.and(brLibAliasDefinitionsFile).hasAlias("br.the-alias", "br.Class")
			.and(aspect).indexPageHasAliasReferences("br.the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(AmbiguousAliasException.class, "br.the-alias", bladesetAliasDefinitionsFile.getUnderlyingFilePath());
	}
	
	@Test
	public void usingDifferentGroupsCanLeadToAmbiguity_usingTheAppToSetTheGroup() throws Exception {
		given(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "br.the-alias", "Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "br.the-alias", "Class2")
			.and(appAliasesFile).usesGroups("appns.bs.b1.g1", "appns.bs.b1.g2")
			.and(brLibAliasDefinitionsFile).hasAlias("br.the-alias", "br.Class")
			.and(aspect).indexPageHasAliasReferences("br.the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(AmbiguousAliasException.class, "br.the-alias", appAliasesFile.getUnderlyingFilePath());
	}
	
	@Test
	public void multipleScenariosCanBeDefinedForAnAlias_usingTheAppToSetTheScenario() throws Exception {
		given(bladesetAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.bs.Class1")
			.and(bladesetAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "appns.bs.Class2")
			.and(bladesetAliasDefinitionsFile).hasScenarioAlias("s2", "appns.bs.b1.the-alias", "appns.bs.Class3")
			.and(bladesetAliasDefinitionsFile).hasScenarioAlias("s3", "appns.bs.b1.the-alias", "appns.bs.Class4")
			.and(bladeset).hasClass("Class3")
			.and(appAliasesFile).usesScenario("s2")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("module.exports = {'appns.bs.b1.the-alias':{'class':'appns/bs/Class3','className':'appns.bs.Class3'}};");
	}
	
	@Test
	public void aliasDefinitionsDefinedWithinBladesMustBeNamespaced_usingTheAppToSetTheGroup() throws Exception {
		given(bladesetAliasDefinitionsFile).hasAlias("the-alias", "TheClass")
    		.and(aspect).indexPageHasAliasReferences("the-alias");
    	when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(exceptions).verifyException(NamespaceException.class, "the-alias", "appns.bs.*");
	}
	
	@Test
	public void theInterfaceIsMaintainedWhenAnAliasIsOverriddenInTheScenario_usingTheAppToSetTheScenario() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.bs.b1.Class1", "appns.bs.b1.TheInterface")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "appns.bs.b1.Class2")
			.and(blade).hasClass("TheInterface")
			.and(blade).hasClass("Class2")
			.and(appAliasesFile).usesScenario("s1")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("module.exports = {'appns.bs.b1.the-alias':{'class':'appns/bs/b1/Class2','className':'appns.bs.b1.Class2','interface':'appns/bs/b1/TheInterface','interfaceName':'appns.bs.b1.TheInterface'}};");
	}
	
	@Test
	public void settingTheScenarioChangesTheAliasesThatAreUsed_usingTheAppToSetTheScenario() throws Exception {
		given(bladesetAliasDefinitionsFile).hasAlias("appns.bs.the-alias", "Class1")
			.and(bladesetAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.the-alias", "appns.bs.Class2")
			.and(bladeset).hasClass("Class2")
			.and(appAliasesFile).usesScenario("s1")
			.and(aspect).indexPageHasAliasReferences("appns.bs.the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("module.exports = {'appns.bs.the-alias':{'class':'appns/bs/Class2','className':'appns.bs.Class2'}};");
	}
	
	@Test
	public void groupsCanContainMultipleAliases_usingTheAppToSetTheGroup() throws Exception {
		given(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "br.alias1", "appns.bs.Class1")
			.and(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "br.alias2", "appns.bs.Class2")
			.and(bladeset).hasClasses("Class1", "Class2")
			.and(appAliasesFile).usesGroups("appns.bs.b1.g1")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias1", "br.Class")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias2", "br.Class")
			.and(aspect).indexPageHasAliasReferences("br.alias1", "br.alias2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("module.exports = {'br.alias1':{'class':'appns/bs/Class1','className':'appns.bs.Class1'},'br.alias2':{'class':'appns/bs/Class2','className':'appns.bs.Class2'}};");
	}
	
	@Test
	public void aspectLevelGroupsOverrideAppLevelGroups() throws Exception {
		given(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "br.alias1", "appns.bs.Class1")
			.and(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "br.alias1", "appns.bs.Class3")
			.and(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "br.alias2", "appns.bs.Class2")
			.and(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "br.alias2", "appns.bs.Class4")
			.and(bladeset).hasClasses("Class1", "Class2", "Class3", "Class4")
			.and(appAliasesFile).usesGroups("appns.bs.b1.g1")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g2")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias1", "br.Class")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias2", "br.Class")
			.and(aspect).indexPageHasAliasReferences("br.alias1", "br.alias2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("module.exports = {'br.alias1':{'class':'appns/bs/Class3','className':'appns.bs.Class3'},'br.alias2':{'class':'appns/bs/Class4','className':'appns.bs.Class4'}};");
	}
	
	@Test
	public void settingTheScenarioViaTheAspectOverridesTheAppScenario() throws Exception {
		given(bladesetAliasDefinitionsFile).hasAlias("appns.bs.the-alias", "Class1")
			.and(bladesetAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.the-alias", "appns.bs.Class2")
			.and(bladesetAliasDefinitionsFile).hasScenarioAlias("s2", "appns.bs.the-alias", "appns.bs.Class3")
			.and(bladeset).hasClasses("Class2", "Class3")
			.and(appAliasesFile).usesScenario("s1")
			.and(aspectAliasesFile).usesScenario("s2")
			.and(aspect).indexPageHasAliasReferences("appns.bs.the-alias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("module.exports = {'appns.bs.the-alias':{'class':'appns/bs/Class3','className':'appns.bs.Class3'}};");
	}
	
}
