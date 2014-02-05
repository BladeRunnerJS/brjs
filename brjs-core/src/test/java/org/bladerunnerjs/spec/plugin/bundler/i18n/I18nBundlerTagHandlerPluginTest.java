package org.bladerunnerjs.spec.plugin.bundler.i18n;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;


public class I18nBundlerTagHandlerPluginTest extends SpecTest
{

	private App app;
	@SuppressWarnings("unused")
	private Aspect aspect;
	@SuppressWarnings("unused")
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
		
}
