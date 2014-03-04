package org.bladerunnerjs.spec.aliasing;

import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class AliasBundlingTest extends SpecTest {
	private App app;
	private AppConf appConf;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private JsLib brLib;
	private AliasesFile aspectAliasesFile;
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
			blade = bladeset.blade("b1");
			bladeAliasDefinitionsFile = blade.assetLocation("src").aliasDefinitionsFile();
			brLib = app.jsLib("br");
	}
	
	@Test
	public void weBundleAClassIfItsAliasIsReferredToInTheIndexPage() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageRefersTo("the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1");
	}
	
	@Test
	public void weAlsoBundleAClassIfTheAliasIsDefinedInABladeAliasDefinitionsXml() throws Exception {
		given(appConf).hasRequirePrefix("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(aspect).indexPageRefersTo("\"appns.bs.b1.the-alias\"");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1");
	}
	
	// TODO: get this class working once we add support for requiring aliases (Adam I suggests we can do the same for services and HTML templates too)
	@Ignore
	@Test
	public void weBundleAClassIfItsAliasIsReferredToFromAnotherNodeJsClass() throws Exception {
		given(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classRequires("appns.Class1", "the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1", "appns.Class2");
	}
	
	@Test
	public void weBundleAClassIfItsAliasIsReferredToFromAnotherNamespacedClass() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classRefersTo("appns.Class1", "the-alias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1", "appns.Class2");
	}
	
	@Test
	public void weBundleTheDependenciesOfClassesIncludedViaAlias() throws Exception {
		given(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).classRequires("appns.Class1", "appns.Class2")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageRefersTo("the-alias");
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
			.and(aspect).indexPageRefersTo("the-alias");
		when(app).requestReceived("/default-aspect/aliasing/bundle.js", response);
		then(response).containsText("setAliasData({'the-alias':{'class':require('appns/Class1'),'className':'appns.Class1'}})");
	}
	
	// TODO: we need lots more tests...
}
