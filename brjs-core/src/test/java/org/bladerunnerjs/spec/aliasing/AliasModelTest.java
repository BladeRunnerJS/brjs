package org.bladerunnerjs.spec.aliasing;

import org.bladerunnerjs.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.aliasing.IncompleteAliasException;
import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

// TODO: should we fail if we refer to interfaces that don't exist
// TODO: should we have tests that confirm that interfaces are loaded if referred to by an alias?
public class AliasModelTest extends SpecTest {
	private App app;
	private AppConf appConf;
	private Aspect aspect;
	private AliasesFile aspectAliasesFile;
	private Bladeset bladeset;
	private AliasDefinitionsFile bladesetAliasDefinitionsFile;
	private Blade blade;
	private AliasDefinitionsFile bladeAliasDefinitionsFile;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			appConf = app.appConf();
			aspect = app.aspect("default");
			aspectAliasesFile = aspect.aliasesFile();
			bladeset = app.bladeset("bs");
			bladesetAliasDefinitionsFile = bladeset.assetLocation("resources").aliasDefinitionsFile();
			blade = bladeset.blade("b1");
			bladeAliasDefinitionsFile = blade.assetLocation("resources").aliasDefinitionsFile();
	}
	
	@Test
	public void aliasesAreRetrievableViaTheModel() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1", "appns.Interface1");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class1", "appns.Interface1");
	}
	
	@Test
	public void nonExistentAliasesAreNotRetrievable() throws Exception {
		when(aspect).retrievesAlias("no-such-alias");
		then(exceptions).verifyException(UnresolvableAliasException.class, "no-such-alias");
	}
	
	@Test
	public void aliasesOverridesMustDefineAClassName() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspectAliasesFile).hasAlias("appns.the-alias", null);
		when(aspect).retrievesAlias("appns.the-alias");
		then(exceptions).verifyException(ContentFileProcessingException.class, doubleQuoted("alias"), doubleQuoted("class"));
	}
	
	// TODO - why does this give an IncompleteAliasException at the aspect level, but the test below it for the blade does not
	@Test
	public void aliasesOverridesMustDefineANonEmptyClassName() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspectAliasesFile).hasAlias("appns.the-alias", "");
		when(aspect).retrievesAlias("appns.the-alias");
		then(exceptions).verifyException(IncompleteAliasException.class, "appns.the-alias");
	}
	
	@Test
	public void aliasesCanHaveAnEmptyStringClassReferenceIfTheyProviderAnInterfaceReference() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void aliasDefinitionsDefinedWithinBladesetsMustBeNamespaced() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladesetAliasDefinitionsFile).hasAlias("the-alias", "appns.Class1");
		when(aspect).retrievesAlias("the-alias");
		then(exceptions).verifyException(NamespaceException.class, "the-alias", "appns.bs");
	}
	
	@Test
	public void aliasDefinitionsDefinedWithinBladesMustBeNamespaced() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("the-alias", "appns.Class1");
		when(aspect).retrievesAlias("the-alias");
		then(exceptions).verifyException(NamespaceException.class, "the-alias", "appns.bs.b1");
	}
	
	@Test
	public void aliasDefinitionsCanBeOverriddenWithinTheAliasesFile() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(aspectAliasesFile).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
	}
	
	@Test
	public void aliasDefinitionsCantBeOverriddenWithinTheBladeset() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(bladesetAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "appns.bs.b1.the-alias", aspectAliasesFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void usedAliasDefinitionsMustBeMadeConcrete() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", null, "appns.Interface");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyException(IncompleteAliasException.class, "appns.bs.b1.the-alias");
	}
	
	@Test
	public void unusedAliasDefinitionsDoNotNeedToBeMadeConcrete() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns.Class")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.alias1", "appns.Class", "appns.Interface1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.alias2", null, "appns.Interface2");
		when(aspect).retrievesAlias("appns.bs.b1.alias1");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void incompleteAliasDefinitionsCanBeMadeConcreteViaDirectOverride() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns.Class")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", null, "appns.Interface")
			.and(aspectAliasesFile).hasAlias("appns.bs.b1.the-alias", "appns.Class");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void incompleteAliasDefinitionsCanBeMadeConcreteUsingGroups() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns.Class")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", null, "appns.Interface")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "appns.Class")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void theNonScenarioAliasIsUsedByDefault() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "appns.Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class1");
	}
	
	@Test
	public void settingTheScenarioChangesTheAliasesThatAreUsed() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesScenario("s1");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
	}
	
	@Test
	public void multipleScenariosCanBeDefinedForAnAlias() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2", "appns.Class3", "appns.Class4")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s2", "appns.bs.b1.the-alias", "appns.Class3")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s3", "appns.bs.b1.the-alias", "appns.Class4")
			.and(aspectAliasesFile).usesScenario("s2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class3");
	}
	
	@Test
	public void aliasesCanStillBeOverriddenWhenTheScenarioIsSet() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2", "appns.Class3")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesScenario("s1")
			.and(aspectAliasesFile).hasAlias("appns.bs.b1.the-alias", "appns.Class3");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class3");
	}
	
	@Test
	public void scenarioAliasesAreAlsoNamespaced() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesScenario("s1");
		when(aspect).retrievesAlias("the-alias");
		then(exceptions).verifyException(NamespaceException.class, "the-alias", "appns.bs.b1");
	}
	
	@Test
	public void theNonGroupAliasIsUsedByDefault() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "appns.Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class1");
	}
	
	@Test
	public void settingAGroupChangesTheAliasesThatAreUsed() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2", "appns.Class3")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
	}
	
	@Test
	public void aliasesCanStillBeOverriddenWhenAGroupIsSet() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "appns.Class1")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1")
			.and(aspectAliasesFile).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
	}
	
	@Test
	public void groupAliasesCanOverrideNonGroupAliases() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2", "appns.Class3")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "appns.bs.b1.the-alias", "appns.Class3")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
	}
	
	@Test
	public void groupsCanContainMultipleAliases() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "alias1", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "alias2", "appns.Class2")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1");
		then(aspect).hasAlias("alias1", "appns.Class1")
			.and(aspect).hasAlias("alias2", "appns.Class2");
	}
	
	@Test
	public void groupAliasesDoNotNeedToBeNamespaced() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "the-alias", "appns.Class1")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1");
		then(aspect).hasAlias("the-alias", "appns.Class1");
	}
	
	@Test
	public void groupIdentifiersMustBeNamespaced() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "the-alias", "appns.Class2");
		when(aspect).retrievesAlias("the-alias");
		then(exceptions).verifyException(NamespaceException.class, "g1", "appns.bs.b1");
	}
	
	@Test
	public void usingGroupsCanLeadToAmbiguity() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1", "appns.bs.b1.g2");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "appns.bs.b1.the-alias", bladeAliasDefinitionsFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void usingGroupsCanLeadToAmbiguityEvenWhenASingleGroupIsUsed() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "appns.Class1")
			.and(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "appns.bs.b1.the-alias", aspectAliasesFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void settingMultipleGroupsChangesTheAliasesThatAreUsed() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.alias1", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "appns.bs.b1.alias2", "appns.Class2")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1", "appns.bs.b1.g2");
		then(aspect).hasAlias("appns.bs.b1.alias1", "appns.Class1");
		then(aspect).hasAlias("appns.bs.b1.alias2", "appns.Class2");
	}
	
	@Test
	public void usingMultipleGroupsCanLeadToAmbiguity() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1", "appns.bs.b1.g2");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "appns.bs.b1.the-alias", bladeAliasDefinitionsFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void theInterfaceIsMaintainedWhenAnAliasIsOverriddenInAliasesFile() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1", "appns.Interface1")
			.and(aspectAliasesFile).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2", "appns.Interface1");
	}
	
	@Test
	public void theInterfaceIsMaintainedWhenAnAliasIsOverriddenInTheScenario() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1", "appns.Interface1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesScenario("s1");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2", "appns.Interface1");
	}
	
	@Test
	public void theInterfaceIsMaintainedWhenAnAliasIsOverriddenInAGroup() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1", "appns.Interface1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2", "appns.Interface1");
	}
}
