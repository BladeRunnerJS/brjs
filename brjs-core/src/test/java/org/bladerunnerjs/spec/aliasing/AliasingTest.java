package org.bladerunnerjs.spec.aliasing;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.aliasing.AliasDefinitionsFile;
import org.bladerunnerjs.model.aliasing.AliasesFile;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AliasingTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private AliasesFile aspectAliasesFile;
	private Bladeset bladeset;
	@SuppressWarnings("unused")
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
			//bladeAliasDefinitionsFile = blade.src().aliasDefinitionsFile(); // TODO: we need to convert src() and resources() to AssetLocation instances, rather than DirNode instances
	}
	
	@Test
	public void weBundleAClassIfTheAliasIsDefinedInTheAliasesXml() throws Exception {
		given(aspect).hasClass("novox.Class1")
			.and(aspectAliasesFile).hasAlias("thealias", "novox.Class1") // TODO: change back to 'the-alias' once the Trie is updated to support all Javascript variable name characters
			.and(aspect).indexPageRefersTo("thealias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/js.bundle", response);
		then(response).containsClasses("novox.Class1");
	}
	
	@Ignore
	@Test
	public void weBundleAClassIfTheAliasIsDefinedInABladeAliasDefinitionsXml() throws Exception {
		given(aspect).hasClass("novox.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("thealias", "novox.Class1") // TODO: change back to 'the-alias' once the Trie is updated to support all Javascript variable name characters
			.and(aspect).indexPageRefersTo("thealias");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/js.bundle", response);
		then(response).containsClasses("novox.Class1");
	}
}
