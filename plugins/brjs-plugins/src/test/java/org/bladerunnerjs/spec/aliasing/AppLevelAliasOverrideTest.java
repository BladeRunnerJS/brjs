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
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasesFile;
import org.junit.Before;
import org.junit.Test;

public class AppLevelAliasOverrideTest extends SpecTest {

	private App app;
	private AppConf appConf;
	private Aspect aspect;
	private JsLib brLib;
	private AliasesFileBuilder appAliasesFileBuilder, aspectAliasesFileBuilder, workbenchAliasesFileBuilder;
	private AliasesVerifier aspectAliasesVerifier;
	private AliasDefinitionsFileBuilder aspectResourcesAliaseDefinitionsFileBuilder;
	private AliasDefinitionsFileBuilder brLibAliasDefinitionsFileBuilder;
	private SdkJsLib servicesLib;
	private StringBuffer response = new StringBuffer();
	private StringBuffer responseWithoutAliasesFile = new StringBuffer();
	private AliasDefinitionsFileBuilder bladeAliasDefinitionsFileBuilder;
	private Bladeset bladeset;
	private Blade blade, bladeForWorkbenchWithoutAliasesFile;
	private BladeWorkbench workbench;
	private AliasesFile worbenchAliasesFile;
	private Aspect aspectWithoutAliasesFile;
	private BladeWorkbench workbenchWithoutAliasesFile;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs)
			.automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()			
			.and(brjs).hasBeenCreated();
		app = brjs.app("app1");
		appConf = app.appConf();
		aspect = app.aspect("default");
		aspectWithoutAliasesFile = app.aspect("withoutAliasesFile");
		bladeset = app.bladeset("bs");
		blade = bladeset.blade("b1");
		bladeForWorkbenchWithoutAliasesFile = bladeset.blade("b2");
		brLib = app.jsLib("br");
		workbench = blade.workbench();
		workbenchWithoutAliasesFile = bladeForWorkbenchWithoutAliasesFile.workbench();
		worbenchAliasesFile = aliasesFile(workbench);
		workbenchAliasesFileBuilder = new AliasesFileBuilder(this, worbenchAliasesFile);
		appAliasesFileBuilder = new AliasesFileBuilder(this, aliasesFile(app));
		aspectAliasesVerifier = new AliasesVerifier(this, aspect);
		aspectAliasesFileBuilder = new AliasesFileBuilder(this, aliasesFile(aspect));
		brLibAliasDefinitionsFileBuilder = new AliasDefinitionsFileBuilder(this, aliasDefinitionsFile(brLib, "resources"));
		aspectResourcesAliaseDefinitionsFileBuilder = new AliasDefinitionsFileBuilder(this, aliasDefinitionsFile(aspect, "resources"));
		bladeAliasDefinitionsFileBuilder = new AliasDefinitionsFileBuilder(this, aliasDefinitionsFile(blade, "src"));
		
		servicesLib = brjs.sdkLib("ServicesLib");
		given(servicesLib).containsFileWithContents("br-lib.conf", "requirePrefix: br")
			.and(servicesLib).hasClasses("br/AliasRegistry", "br/ServiceRegistry");
	}
	
	@Test
	public void aspectAliasesAreOverridenByTheAppAliases() throws Exception {
		given(appAliasesFileBuilder).hasAlias("the-alias", "TheClassOverride");
		then(aspectAliasesVerifier).hasAlias("the-alias", "TheClassOverride");
	}
	
	@Test
	public void aliasDefinitionsInLibsAreOverridenByTheAppAliases() throws Exception {
		given(brLib).hasClasses("br/LibClass1", "br/LibClass2")
			.and(brLibAliasDefinitionsFileBuilder).hasAlias("br.libAlias", "br.LibClass1")
			.and(appAliasesFileBuilder).hasAlias("br.libAlias", "br.LibClass2")
			.and(aspect).indexPageHasAliasReferences("br.libAlias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("br/LibClass2");
	}
	
	@Test
	public void aliasDefinitionsInAspectAreOverridenByTheAppAliases() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.App", "appns.AspectClass1", "appns.AspectClass2", "appns.AspectClass3")
			.and(aspectResourcesAliaseDefinitionsFileBuilder).hasAlias("appns.aspectAlias1", "appns.AspectClass1")
			.and(aspect).containsFileWithContents("resources/subfolder/aliasDefinitions.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><aliasDefinitions xmlns=\"http://schema.bladerunnerjs.org/aliasDefinitions\">\n" + "<alias defaultClass=\"appns.AspectClass2\" name=\"appns.aspectAlias2\"/>\n" + "</aliasDefinitions>")
			.and(aspect).containsFileWithContents("resources/subfolder/subfolder/aliasDefinitions.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><aliasDefinitions xmlns=\"http://schema.bladerunnerjs.org/aliasDefinitions\">\n" + "<alias defaultClass=\"appns.AspectClass3\" name=\"appns.aspectAlias3\"/>\n" + "</aliasDefinitions>")
			.and(aspect).indexPageRefersTo("appns.App")
			.and(appAliasesFileBuilder).hasAlias("appns.aspectAlias1", "appns.AspectClass2")
			.and(aspect).classFileHasContent("appns.App", "'appns.aspectAlias1'");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsNamespacedJsClasses("appns.AspectClass2");
	}
	
	@Test
	public void aliasDefinitionsInBladeAreOverridenByTheAppAliases() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(bladeAliasDefinitionsFileBuilder).hasAlias("appns.bs.b1.bladeAlias", "appns.Class1")
			.and(appAliasesFileBuilder).hasAlias("appns.bs.b1.bladeAlias", "appns.Class2")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.bladeAlias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class2");
	}
	
	@Test
	public void aliasesAreTakenFromBothAppAliasesAndBladeAliasDefinitionsForDifferentAliases() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns/AspectClass1", "appns/AspectClass2")
			.and(bladeAliasDefinitionsFileBuilder).hasAlias("appns.bs.b1.bladeAliasDefinitionForClass1", "appns.AspectClass1")
			.and(appAliasesFileBuilder).hasAlias("appns.appAliasForClass2", "appns.AspectClass2")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.bladeAliasDefinitionForClass1", "appns.appAliasForClass2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.AspectClass1", "appns.AspectClass2");
	}
	
	@Test
	public void appAliasesHavePriorityIfAliasesArePresentInBothAppAliasesAndBladeAliasDefinitionsAndAlsoContainUniqueOnes() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns/AspectClass1", "appns/AspectClass2", "appns/AspectClass3")
			.and(blade).hasClasses("appns/bs/b1/BladeClass1", "appns/bs/b1/BladeClass2")
			.and(bladeAliasDefinitionsFileBuilder).hasAlias("appns.bs.b1.bladeAliasDefinition1", "appns.bs.b1.BladeClass1")
			.and(bladeAliasDefinitionsFileBuilder).hasAlias("appns.bs.b1.bladeAliasDefinition2", "appns.bs.b1.BladeClass2")
			.and(appAliasesFileBuilder).hasAlias("appns.AppOverrideForAspectClass2", "appns.AspectClass2")
			.and(appAliasesFileBuilder).hasAlias("appns.bs.b1.bladeAliasDefinition2", "appns.AspectClass3")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.bladeAliasDefinition1", "appns.AppOverrideForAspectClass2", "appns.bs.b1.bladeAliasDefinition2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.b1.BladeClass1", "appns.AspectClass2", "appns.AspectClass3");
	}
	
	@Test
	public void aspectAliasesOverridesAppAliases() throws Exception {
		given(aspect).hasClasses("appns/App", "appns/Class1", "appns/Class2")
			.and(aspectAliasesFileBuilder).hasAlias("appns.aspectAlias", "appns.Class2")
			.and(appAliasesFileBuilder).hasAlias("appns.aspectAlias", "appns.Class1")
			.and(aspect).indexPageHasAliasReferences("appns.aspectAlias");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class2");
	}
	
	@Test
	public void workbenchAliasesOverridesAppAliases() throws Exception {
		given(workbench).hasClasses("appns/WorkbenchClass1", "appns/WorkbenchClass2")
			.and(workbenchAliasesFileBuilder).hasAlias("workbenchAlias", "appns.WorkbenchClass2")
			.and(appAliasesFileBuilder).hasAlias("workbenchAlias", "appns.WorkbenchClass1")
			.and(workbench).indexPageRequires("alias!workbenchAlias");
		when(workbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.WorkbenchClass2");
	}
	
//	@Test
//	public void testPackAliasesOverrideAppAliases() throws Exception {
//		given(appConf).hasRequirePrefix("appns")
//			.and(aspect).hasClasses("appns/Class1", "appns/Class2")
//			// given(testPack).containsFileWithContents("Class1.js", "Class1")
//			// .and(testPack).containsFileWithContents("Class2.js", "Class2")
//			.and(testPackAliasesFileBuilder).hasAlias("testAlias", "appns.Class1")
//			.and(appAliasesFileBuilder).hasAlias("testAlias", "appns.Class2")
//			.and(testPack).hasNamespacedJsPackageStyle()
//			.and(testPack).testRefersTo("pkg/test.js", "alias!testAlias");
//		when(testPack).requestReceivedInDev("js/dev/combined/bundle.js", response);
//		then(response).containsCommonJsClasses("appns.Class1");
//	}
	
	@Test
	public void aspectWithoutAnAliasesFileUsesAppAliasesAndAspectWithAnAliasesFileUsesItsAliasesFile() throws Exception {
		given(aspectWithoutAliasesFile).hasClasses("appns/Class1WithoutAlias", "appns/Class2WithoutAlias")
			.and(aspect).hasClasses("appns/App", "appns/Class1", "appns/Class2")
			.and(aspectAliasesFileBuilder).hasAlias("appns.aspectAliasWithAspectAliasesFile", "appns.Class2")
			.and(appAliasesFileBuilder).hasAlias("appns.aspectAliasWithAspectAliasesFile", "appns.Class1")
			.and(appAliasesFileBuilder).hasAlias("appns.aspectAliasWithoutAspectAliasesFile", "appns.Class1WithoutAlias")
			.and(aspect).indexPageHasAliasReferences("appns.aspectAliasWithAspectAliasesFile")
			.and(aspectWithoutAliasesFile).indexPageHasAliasReferences("appns.aspectAliasWithoutAspectAliasesFile");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response)
			.and(aspectWithoutAliasesFile).requestReceivedInDev("js/dev/combined/bundle.js", responseWithoutAliasesFile);
		then(response).containsCommonJsClasses("appns.Class2")
			.and(responseWithoutAliasesFile).containsCommonJsClasses("appns.Class1WithoutAlias");
	}
	
	@Test
	public void workbenchWithoutAnAliasesFileUsesAppAliasesAndWorkbenchWithAnAliasesFileUsesItsAliasesFile() throws Exception {
		given(workbenchWithoutAliasesFile).hasClasses("appns/Class1WithoutAlias", "appns/Class2WithoutAlias")
			.and(workbench).hasClasses("appns/App", "appns/Class1", "appns/Class2")
			.and(workbenchAliasesFileBuilder).hasAlias("appns.aspectAliasWithAspectAliasesFile", "appns.Class2")
			.and(appAliasesFileBuilder).hasAlias("appns.aspectAliasWithAspectAliasesFile", "appns.Class1")
			.and(appAliasesFileBuilder).hasAlias("appns.aspectAliasWithoutAspectAliasesFile", "appns.Class1WithoutAlias")
			.and(workbench).indexPageRequires("alias!appns.aspectAliasWithAspectAliasesFile")
			.and(workbenchWithoutAliasesFile).indexPageRequires("alias!appns.aspectAliasWithoutAspectAliasesFile");
		when(workbench).requestReceivedInDev("js/dev/combined/bundle.js", response)
			.and(workbenchWithoutAliasesFile).requestReceivedInDev("js/dev/combined/bundle.js", responseWithoutAliasesFile);
		then(response).containsCommonJsClasses("appns.Class2")
			.and(responseWithoutAliasesFile).containsCommonJsClasses("appns.Class1WithoutAlias");
	}
	
}
