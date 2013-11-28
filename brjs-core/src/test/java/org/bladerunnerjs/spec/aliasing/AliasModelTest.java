package org.bladerunnerjs.spec.aliasing;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.model.aliasing.NamespaceException;
import org.bladerunnerjs.model.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.model.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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
			bladesetAliasDefinitionsFile = bladeset.src().aliasDefinitionsFile();
			blade = bladeset.blade("b1");
			bladeAliasDefinitionsFile = blade.src().aliasDefinitionsFile();
	}
	
	@Ignore
	@Test
	public void aliasesAreRetrievableViaTheModel() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClass("novox.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1", "novox.Interface1");
		then(aspect).hasAlias("novox.bs.b1.the-alias", "novox.Class1", "novox.Interface1");
	}
	
	@Test
	public void nonExistentAliasesAreNotRetrievable() throws Exception {
		when(aspect).retrievesAlias("no-such-alias");
		then(exceptions).verifyException(UnresolvableAliasException.class, "no-such-alias");
	}
	
	@Test
	public void aliasDefinitionsDefinedWithinBladesetsMustBeNamespaced() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClass("novox.Class1")
			.and(bladesetAliasDefinitionsFile).hasAlias("the-alias", "novox.Class1");
		when(aspect).retrievesAlias("the-alias");
		then(exceptions).verifyException(NamespaceException.class, "the-alias", "novox.bs");
	}
	
	@Test
	public void aliasDefinitionsDefinedWithinBladesMustBeNamespaced() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClass("novox.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("the-alias", "novox.Class1");
		when(aspect).retrievesAlias("the-alias");
		then(exceptions).verifyException(NamespaceException.class, "the-alias", "novox.bs.b1");
	}
	
	@Test
	public void aliasDefinitionsCanBeOverriddenWithinTheAliasesFile() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(aspectAliasesFile).hasAlias("novox.bs.b1.the-alias", "novox.Class2");
		then(aspect).hasAlias("novox.bs.b1.the-alias", "novox.Class2");
	}
	
	@Test
	public void aliasDefinitionsCantBeOverriddenWithinTheBladeset() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(bladesetAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class2");
		when(aspect).retrievesAlias("novox.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "novox.bs.b1.the-alias", aspectAliasesFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void theNonScenarioAliasIsUsedByDefault() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "novox.bs.b1.the-alias", "novox.Class2");
		then(aspect).hasAlias("novox.bs.b1.the-alias", "novox.Class1");
	}
	
	@Test
	public void settingTheScenarioChangesTheAliasesThatAreUsed() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspectAliasesFile).usesScenario("s1");
		then(aspect).hasAlias("novox.bs.b1.the-alias", "novox.Class2");
	}
	
	@Test
	public void aliasesCanStillBeOverriddenWhenTheScenarioIsSet() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2", "novox.Class3")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspectAliasesFile).usesScenario("s1")
			.and(aspectAliasesFile).hasAlias("novox.bs.b1.the-alias", "novox.Class3");
		then(aspect).hasAlias("novox.bs.b1.the-alias", "novox.Class3");
	}
	
	@Test
	public void theNonGroupAliasIsUsedByDefault() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.the-alias", "novox.Class2");
		then(aspect).hasAlias("novox.bs.b1.the-alias", "novox.Class1");
	}
	
	@Test
	public void settingAGroupChangesTheAliasesThatAreUsed() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2", "novox.Class3")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g2", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspectAliasesFile).usesGroups("g2");
		then(aspect).hasAlias("novox.bs.b1.the-alias", "novox.Class2");
	}
	
	@Test
	public void aliasesCanStillBeOverriddenWhenAGroupIsSet() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.the-alias", "novox.Class1")
			.and(aspectAliasesFile).usesGroups("g1")
			.and(aspectAliasesFile).hasAlias("novox.bs.b1.the-alias", "novox.Class2");
		then(aspect).hasAlias("novox.bs.b1.the-alias", "novox.Class2");
	}
	
	@Ignore
	@Test
	public void groupAliasesCanOverrideNonGroupAliases() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2", "novox.Class3")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.the-alias", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g2", "novox.bs.b1.the-alias", "novox.Class3")
			.and(aspectAliasesFile).usesGroups("g1");
		then(aspect).hasAlias("novox.bs.b1.the-alias", "novox.Class2");
	}
	
	@Test
	public void usingGroupsCanLeadToAmbiguity() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g2", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspectAliasesFile).usesGroups("g1", "g2");
		when(aspect).retrievesAlias("novox.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "novox.bs.b1.the-alias", bladeAliasDefinitionsFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void usingGroupsCanLeadToAmbiguityEvenWhenASingleGroupIsUsed() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.the-alias", "novox.Class1")
			.and(bladesetAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspectAliasesFile).usesGroups("g1");
		when(aspect).retrievesAlias("novox.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "novox.bs.b1.the-alias", aspectAliasesFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void settingMultipleGroupsChangesTheAliasesThatAreUsed() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.alias1", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g2", "novox.bs.b1.alias2", "novox.Class2")
			.and(aspectAliasesFile).usesGroups("g1", "g2");
		then(aspect).hasAlias("novox.bs.b1.alias1", "novox.Class1");
		then(aspect).hasAlias("novox.bs.b1.alias2", "novox.Class2");
	}
	
	@Test
	public void usingMultipleGroupsCanLeadToAmbiguity() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g2", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspectAliasesFile).usesGroups("g1", "g2");
		when(aspect).retrievesAlias("novox.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "novox.bs.b1.the-alias", bladeAliasDefinitionsFile.getUnderlyingFile().getPath());
	}
	
	@Ignore
	@Test
	public void theInterfaceIsMaintainedWhenAnAliasIsOverriddenInAliasesFile() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClass("novox.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1", "novox.Interface1")
			.and(aspectAliasesFile).hasAlias("novox.bs.b1.the-alias", "novox.Class2");
		then(aspect).hasAlias("novox.bs.b1.the-alias", "novox.Class2", "novox.Interface1");
	}
	
	@Ignore
	@Test
	public void theInterfaceIsMaintainedWhenAnAliasIsOverriddenInTheScenario() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClass("novox.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1", "novox.Interface1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspectAliasesFile).usesScenario("s1");
		then(aspect).hasAlias("novox.bs.b1.the-alias", "novox.Class2", "novox.Interface1");
	}
	
	@Ignore
	@Test
	public void theInterfaceIsMaintainedWhenAnAliasIsOverriddenInAGroup() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClass("novox.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1", "novox.Interface1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspectAliasesFile).usesGroups("g1");
		then(aspect).hasAlias("novox.bs.b1.the-alias", "novox.Class2", "novox.Interface1");
	}
}
