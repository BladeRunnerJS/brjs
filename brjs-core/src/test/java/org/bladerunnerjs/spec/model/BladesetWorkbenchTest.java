package org.bladerunnerjs.spec.model;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.BladesetWorkbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class BladesetWorkbenchTest extends SpecTest {
	private App app;
	private Bladeset bladeset;
	private BladesetWorkbench workbench;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			bladeset = app.bladeset("bladeset");
			workbench = bladeset.workbench();
	}
	
	@Test
	public void parentGivesTheCorrectNode() throws Exception {
		given(bladeset).hasBeenCreated();
		then(workbench.parent()).isSameAs(bladeset);
	}
}