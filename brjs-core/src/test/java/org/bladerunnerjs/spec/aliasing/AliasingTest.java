package org.bladerunnerjs.spec.aliasing;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.aliasing.AliasDefinitionsFile;
import org.bladerunnerjs.model.aliasing.AliasesFile;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AliasingTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private AliasesFile aspectAliasesFile;
	private Bladeset bladeset;
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
			aspect = app.aspect("default");
			aspectAliasesFile = aspect.aliasesFile();
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			bladeAliasDefinitionsFile = blade.src().aliasDefinitionsFile();
	}
	
	@Test
	public void weBundleAClassIfTheAliasIsDefinedInTheAliasesXml() throws Exception {
		given(aspect).hasClass("novox.Class1")
			.and(aspectAliasesFile).hasAlias("thealias", "novox.Class1") // TODO: change back to 'the-alias' once the Trie is updated to support all Javascript variable name characters
			.and(aspect).indexPageRefersTo("thealias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class1");
	}
	
	@Test
	public void weBundleAClassIfTheAliasIsDefinedInABladeAliasDefinitionsXml() throws Exception {
		given(aspect).hasClass("novox.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("thealias", "novox.Class1") // TODO: change back to 'the-alias' once the Trie is updated to support all Javascript variable name characters
			.and(aspect).indexPageRefersTo("thealias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.Class1");
	}
}
