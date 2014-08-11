package org.bladerunnerjs.spec.aliasing;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.aliasing.IncompleteAliasException;
import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AliasModelTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private AliasesFile aspectAliasesFile;
	private Bladeset bladeset;
	private AliasDefinitionsFile bladesetAliasDefinitionsFile;
	private Blade blade;
	private AliasDefinitionsFile bladeAliasDefinitionsFile;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			aspectAliasesFile = aspect.aliasesFile();
			bladeset = app.bladeset("bs");
			bladesetAliasDefinitionsFile = bladeset.assetLocation("resources").aliasDefinitionsFile();
			blade = bladeset.blade("b1");
			bladeAliasDefinitionsFile = blade.assetLocation("resources").aliasDefinitionsFile();
	}
	
	@Test
	public void aliasesAreRetrievableViaTheModel() throws Exception {
		given(aspectAliasesFile).hasAlias("the-alias", "TheClass");
		then(aspect).hasAlias("the-alias", "TheClass");
	}
	
	@Test
	public void aliasDefinitionsAreRetrievableViaTheModel() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "TheClass", "TheInterface");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "TheClass", "TheInterface");
	}
	
	@Test
	public void nonExistentAliasesAreNotRetrievable() throws Exception {
		when(aspect).retrievesAlias("no-such-alias");
		then(exceptions).verifyException(UnresolvableAliasException.class, "no-such-alias");
	}
	
	@Test
	public void aliasesOverridesMustDefineAClassName() throws Exception {
		given(aspectAliasesFile).hasAlias("the-alias", null);
		when(aspect).retrievesAlias("the-alias");
		then(exceptions).verifyException(ContentFileProcessingException.class, doubleQuoted("alias"), doubleQuoted("class"));
	}
	
	// TODO - why does this give an IncompleteAliasException at the aspect level, but the test below it for the blade does not
	@Test
	public void aliasesOverridesMustDefineANonEmptyClassName() throws Exception {
		given(aspectAliasesFile).hasAlias("the-alias", "");
		when(aspect).retrievesAlias("the-alias");
		then(exceptions).verifyException(IncompleteAliasException.class, "the-alias");
	}
	
	@Test
	public void aliasesCanHaveAnEmptyStringClassReferenceIfTheyProviderAnInterfaceReference() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void aliasDefinitionsDefinedWithinBladesetsMustBeNamespaced() throws Exception {
		given(bladesetAliasDefinitionsFile).hasAlias("the-alias", "TheClass");
		when(aspect).retrievesAlias("the-alias");
		then(exceptions).verifyException(NamespaceException.class, "the-alias", "appns.bs.*");
	}

	@Test
	public void aliasDefinitionsDefinedWithinBladesetsMustBeCorrectlyNamespaced() throws Exception {
		given(bladesetAliasDefinitionsFile).hasAlias("appns.bsblah.the-alias", "TheClass");
		when(aspect).retrievesAlias("appns.bsblah.the-alias");
		then(exceptions).verifyException(NamespaceException.class, "appns.bsblah.the-alias", "appns.bs.*");
	}
	
	@Test
	public void aliasDefinitionsDefinedWithinBladesMustBeNamespaced() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("the-alias", "TheClass");
		when(aspect).retrievesAlias("the-alias");
		then(exceptions).verifyException(NamespaceException.class, "the-alias", "appns.bs.b1.*");
	}
	
	@Test
	public void aliasDefinitionsCanBeOverriddenWithinTheAliasesFile() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "Class1")
			.and(aspectAliasesFile).hasAlias("appns.bs.b1.the-alias", "Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "Class2");
	}
	
	@Test
	public void aliasDefinitionsCantBeOverriddenWithinTheBladeset() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "Class1")
			.and(bladesetAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "Class2");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "appns.bs.b1.the-alias", aspectAliasesFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void unspecifiedAliasDefinitionsPointToTheUnknownClass() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", null, "TheInterface");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "br.UnknownClass", "TheInterface");
	}
	
	@Test
	public void unusedAliasDefinitionsDoNotNeedToBeMadeConcrete() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.alias1", "TheClass", "Interface1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.alias2", null, "Interface2");
		when(aspect).retrievesAlias("appns.bs.b1.alias1");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void incompleteAliasDefinitionsCanBeMadeConcreteViaDirectOverride() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", null, "TheInterface")
			.and(aspectAliasesFile).hasAlias("appns.bs.b1.the-alias", "TheClass");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void incompleteAliasDefinitionsCanBeMadeConcreteUsingGroups() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", null, "TheInterface")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "TheClass")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void theNonScenarioAliasIsUsedByDefault() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "Class1");
	}
	
	@Test
	public void settingTheScenarioChangesTheAliasesThatAreUsed() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "Class2")
			.and(aspectAliasesFile).usesScenario("s1");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "Class2");
	}
	
	@Test
	public void multipleScenariosCanBeDefinedForAnAlias() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "Class2")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s2", "appns.bs.b1.the-alias", "Class3")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s3", "appns.bs.b1.the-alias", "Class4")
			.and(aspectAliasesFile).usesScenario("s2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "Class3");
	}
	
	@Test
	public void aliasesCanStillBeOverriddenWhenTheScenarioIsSet() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "Class2")
			.and(aspectAliasesFile).usesScenario("s1")
			.and(aspectAliasesFile).hasAlias("appns.bs.b1.the-alias", "Class3");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "Class3");
	}
	
	@Test
	public void scenarioAliasesAreAlsoNamespaced() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("the-alias", "Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "the-alias", "Class2")
			.and(aspectAliasesFile).usesScenario("s1");
		when(aspect).retrievesAlias("the-alias");
		then(exceptions).verifyException(NamespaceException.class, "the-alias", "appns.bs.b1.*");
	}
	
	@Test
	public void theNonGroupAliasIsUsedByDefault() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "Class1");
	}
	
	@Test
	public void settingAGroupChangesTheAliasesThatAreUsed() throws Exception {
		given(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "appns.bs.b1.the-alias", "Class2")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "Class2");
	}
	
	@Test
	public void aliasesCanStillBeOverriddenWhenAGroupIsSet() throws Exception {
		given(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "Class1")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1")
			.and(aspectAliasesFile).hasAlias("appns.bs.b1.the-alias", "Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "Class2");
	}
	
	@Test
	public void groupAliasesCanOverrideNonGroupAliases() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "appns.bs.b1.the-alias", "Class3")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "Class2");
	}
	
	@Test
	public void groupsCanContainMultipleAliases() throws Exception {
		given(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "alias1", "Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "alias2", "Class2")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1");
		then(aspect).hasAlias("alias1", "Class1")
			.and(aspect).hasAlias("alias2", "Class2");
	}
	
	@Test
	public void groupAliasesDoNotNeedToBeNamespaced() throws Exception {
		given(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "the-alias", "TheClass")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1");
		then(aspect).hasAlias("the-alias", "TheClass");
	}
	
	@Test
	public void groupIdentifiersMustBeNamespaced() throws Exception {
		given(bladeAliasDefinitionsFile).hasGroupAlias("g1", "the-alias", "TheClass");
		when(aspect).retrievesAlias("the-alias");
		then(exceptions).verifyException(NamespaceException.class, "g1", "appns.bs.b1.*");
	}
	
	@Test
	public void usingGroupsCanLeadToAmbiguity() throws Exception {
		given(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "appns.bs.b1.the-alias", "Class2")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1", "appns.bs.b1.g2");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "appns.bs.b1.the-alias", bladeAliasDefinitionsFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void usingGroupsCanLeadToAmbiguityEvenWhenASingleGroupIsUsed() throws Exception {
		given(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "Class1")
			.and(bladesetAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "Class2")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "appns.bs.b1.the-alias", aspectAliasesFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void settingMultipleGroupsChangesTheAliasesThatAreUsed() throws Exception {
		given(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.alias1", "Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "appns.bs.b1.alias2", "Class2")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1", "appns.bs.b1.g2");
		then(aspect).hasAlias("appns.bs.b1.alias1", "Class1");
		then(aspect).hasAlias("appns.bs.b1.alias2", "Class2");
	}
	
	@Test
	public void usingMultipleGroupsCanLeadToAmbiguity() throws Exception {
		given(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g2", "appns.bs.b1.the-alias", "Class2")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1", "appns.bs.b1.g2");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "appns.bs.b1.the-alias", bladeAliasDefinitionsFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void theInterfaceIsMaintainedWhenAnAliasIsOverriddenInAliasesFile() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "Class1", "TheInterface")
			.and(aspectAliasesFile).hasAlias("appns.bs.b1.the-alias", "Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "Class2", "TheInterface");
	}
	
	@Test
	public void theInterfaceIsMaintainedWhenAnAliasIsOverriddenInTheScenario() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "Class1", "TheInterface")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "Class2")
			.and(aspectAliasesFile).usesScenario("s1");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "Class2", "TheInterface");
	}
	
	@Test
	public void theInterfaceIsMaintainedWhenAnAliasIsOverriddenInAGroup() throws Exception {
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "Class1", "TheInterface")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("appns.bs.b1.g1", "appns.bs.b1.the-alias", "Class2")
			.and(aspectAliasesFile).usesGroups("appns.bs.b1.g1");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "Class2", "TheInterface");
	}
	
	@Test
	public void nestedAliasDefinitionsFilesCanBeUsedInResourcesDirectories() throws Exception {
		// TODO: think of a way of doing this in a more BDD way
		FileUtils.write(blade.assetLocation("resources").file("aliasDefinitions.xml"), "<aliasDefinitions/>");
		FileUtils.write(blade.assetLocation("resources").file("dir/aliasDefinitions.xml"), "<aliasDefinitions/>");
		AliasDefinitionsFile nestedBladeAliasDefinitionsFile = blade.assetLocation("resources").aliasDefinitionsFiles().get(1);
		
		given(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.alias1", "Class1", "TheInterface")
			.and(nestedBladeAliasDefinitionsFile).hasAlias("appns.bs.b1.alias2", "Class2", "TheInterface");
		then(aspect).hasAlias("appns.bs.b1.alias1", "Class1", "TheInterface")
			.and(aspect).hasAlias("appns.bs.b1.alias2", "Class2", "TheInterface");
	}
	
}
