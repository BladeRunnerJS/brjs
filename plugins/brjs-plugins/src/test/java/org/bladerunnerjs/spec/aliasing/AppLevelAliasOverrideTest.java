package org.bladerunnerjs.spec.aliasing;

import static org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility.aliasDefinitionsFile;
import static org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility.aliasesFile;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.SdkJsLib;
import org.junit.Before;
import org.junit.Test;

public class AppLevelAliasOverrideTest extends SpecTest {

	private App app;
	private Aspect aspect;
	private JsLib brLib;
	private AliasesFileBuilder appAliasesFileBuilder;
	private AliasesVerifier aspectAliasesVerifier;
	private AliasDefinitionsFileBuilder brLibAliasDefinitionsFileBuilder;
	private SdkJsLib servicesLib;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs)
			.automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()			
			.and(brjs).hasBeenCreated();
		app = brjs.app("app1");
		aspect = app.aspect("default");
		brLib = app.jsLib("br");
		appAliasesFileBuilder = new AliasesFileBuilder(this, aliasesFile(app));
		aspectAliasesVerifier = new AliasesVerifier(this, aspect);
		brLibAliasDefinitionsFileBuilder = new AliasDefinitionsFileBuilder(this, aliasDefinitionsFile(brLib, "resources"));
		
		servicesLib = brjs.sdkLib("ServicesLib");
		given(servicesLib).containsFileWithContents("br-lib.conf", "requirePrefix: br")
			.and(servicesLib).hasClasses("br/AliasRegistry", "br/ServiceRegistry");
	}
	
	@Test
	public void aspectAliasesAreOverridenByTheAppAliases() throws Exception {
		given(appAliasesFileBuilder).hasAlias("the-alias", "TheClassOverride");
		then(aspectAliasesVerifier).hasAlias("the-alias", "TheClassOverride");
	}
}
