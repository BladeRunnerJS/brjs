package org.bladerunnerjs.spec.bundling.aspect.resources;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;

public class AspectBundlingOfI18N extends SpecTest {
	private App app;
	private Aspect aspect;
	@SuppressWarnings("unused")	// TODO remove when we add tests
	private Theme standardAspectTheme;
	@SuppressWarnings("unused") // TODO remove when we add tests
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			standardAspectTheme = aspect.theme("standard");
	}
	
	// TODO add tests
}
