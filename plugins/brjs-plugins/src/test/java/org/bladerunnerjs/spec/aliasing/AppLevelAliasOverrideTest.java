package org.bladerunnerjs.spec.aliasing;

import static org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility.aliasesFile;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AppLevelAliasOverrideTest extends SpecTest {

	private App app;
	private Aspect aspect;
	private AliasesFileBuilder appAliasesFileBuilder;
	private AliasesVerifier aspectAliasesVerifier;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs)
			.automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()			
			.and(brjs).hasBeenCreated();
		app = brjs.app("app1");
		aspect = app.aspect("default");
		appAliasesFileBuilder = new AliasesFileBuilder(this, aliasesFile(app));
		aspectAliasesVerifier = new AliasesVerifier(this, aspect);
	}
	
	@Test
	public void aspectAliasesAreOverridenByTheAppAliases() throws Exception {
		given(appAliasesFileBuilder).hasAlias("the-alias", "TheClassOverride");
		then(aspectAliasesVerifier).hasAlias("the-alias", "TheClassOverride");
	}
	
}
