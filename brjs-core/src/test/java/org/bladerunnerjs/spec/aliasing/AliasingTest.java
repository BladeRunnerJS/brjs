package org.bladerunnerjs.spec.aliasing;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.aliasing.AliasDefinitionsFile;
import org.bladerunnerjs.model.aliasing.AliasesFile;
import org.bladerunnerjs.model.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.model.aliasing.NamespaceException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

// TODO: confirm with the team and or KG that groups can only be used to define aliases that haven't otherwise been defined -- we are currently throwing an ambiguity exception in this case
public class AliasingTest extends SpecTest {
	private App app;
	private AppConf appConf;
	private Aspect aspect;
	private AliasesFile aspectAliasesFile;
	private Bladeset bladeset;
	private AliasDefinitionsFile bladesetAliasDefinitionsFile;
	private Blade blade;
	private AliasDefinitionsFile bladeAliasDefinitionsFile;
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
			bladesetAliasDefinitionsFile = bladeset.src().aliasDefinitionsFile();
			blade = bladeset.blade("b1");
			bladeAliasDefinitionsFile = blade.src().aliasDefinitionsFile();
	}
	
	@Test
	public void aliasesCantPointToNonExistentClasses() throws Exception {
		given(aspectAliasesFile).hasAlias("the-alias", "novox.Class1")
			.and(aspect).indexPageRefersTo("the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "novox/Class1");
	}
	
	@Test
	public void weBundleAClassIfTheAliasIsDefinedInTheAliasesXml() throws Exception {
		given(aspect).hasClass("novox.Class1")
			.and(aspectAliasesFile).hasAlias("the-alias", "novox.Class1")
			.and(aspect).indexPageRefersTo("the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class1");
	}
	
	@Test
	public void weBundleAClassIfTheAliasIsDefinedInABladeAliasDefinitionsXml() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClass("novox.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(aspect).indexPageRefersTo("novox.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class1");
	}
	
	@Test
	public void aliasDefinitionsDefinedWithinBladesetsMustBeNamespaced() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClass("novox.Class1")
			.and(bladesetAliasDefinitionsFile).hasAlias("the-alias", "novox.Class1")
			.and(aspect).indexPageRefersTo("the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyException(NamespaceException.class, "the-alias", "novox.bs");
	}
	
	@Test
	public void aliasDefinitionsDefinedWithinBladesMustBeNamespaced() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClass("novox.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("the-alias", "novox.Class1")
			.and(aspect).indexPageRefersTo("the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyException(NamespaceException.class, "the-alias", "novox.bs.b1");
	}
	
	@Test
	public void aliasDefinitionsCanBeOverriddenWithinTheAliasesFile() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(aspectAliasesFile).hasAlias("novox.bs.b1.the-alias", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class2");
	}
	
	@Test
	public void aliasDefinitionsCantBeOverriddenWithinTheBladeset() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(bladesetAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyException(AmbiguousAliasException.class, "novox.bs.b1.the-alias", aspectAliasesFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void theNonScenarioAliasIsUsedByDefault() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class1");
	}
	
	@Test
	public void settingTheScenarioChangesTheAliasesThatAreUsed() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspectAliasesFile).usesScenario("s1")
			.and(aspect).indexPageRefersTo("novox.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class2");
	}
	
	@Test
	public void aliasesCanStillBeOverriddenWhenTheScenarioIsSet() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2", "novox.Class3")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspectAliasesFile).usesScenario("s1")
			.and(aspectAliasesFile).hasAlias("novox.bs.b1.the-alias", "novox.Class3")
			.and(aspect).indexPageRefersTo("novox.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class3");
	}
	
	@Test
	public void theNonGroupAliasIsUsedByDefault() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class1");
	}
	
	@Test
	public void settingAGroupChangesTheAliasesThatAreUsed() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2", "novox.Class3")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g2", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspectAliasesFile).usesGroups("g2")
			.and(aspect).indexPageRefersTo("novox.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class2");
	}
	
	@Test
	public void aliasesCanStillBeOverriddenWhenAGroupIsSet() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.the-alias", "novox.Class1")
			.and(aspectAliasesFile).usesGroups("g1")
			.and(aspectAliasesFile).hasAlias("novox.bs.b1.the-alias", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class2");
	}
	
	@Test
	public void usingGroupsCanLeadToAmbiguity() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspectAliasesFile).usesGroups("g1")
			.and(aspect).indexPageRefersTo("novox.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyException(AmbiguousAliasException.class, "novox.bs.b1.the-alias", bladeAliasDefinitionsFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void usingMultipleGroupsCanLeadToAmbiguity() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.the-alias", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g2", "novox.bs.b1.the-alias", "novox.Class2")
			.and(aspectAliasesFile).usesGroups("g1", "g2")
			.and(aspect).indexPageRefersTo("novox.bs.b1.the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyException(AmbiguousAliasException.class, "novox.bs.b1.the-alias", bladeAliasDefinitionsFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void settingMultipleGroupsChangesTheAliasesThatAreUsed() throws Exception {
		given(appConf).hasNamespace("novox")
			.and(aspect).hasClasses("novox.Class1", "novox.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "novox.bs.b1.alias1", "novox.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g2", "novox.bs.b1.alias2", "novox.Class2")
			.and(aspectAliasesFile).usesGroups("g1", "g2")
			.and(aspect).indexPageRefersTo("novox.bs.b1.alias1, novox.bs.b1.alias2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class1", "novox.Class2");
	}
}
