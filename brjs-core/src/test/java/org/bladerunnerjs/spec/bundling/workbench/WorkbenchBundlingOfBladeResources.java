package org.bladerunnerjs.spec.bundling.workbench;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class WorkbenchBundlingOfBladeResources extends SpecTest {
	private App app;
	private Bladeset bladeset;
	private Blade blade;
	private StringBuffer response = new StringBuffer();
	private Workbench workbench;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			workbench = blade.workbench();
	}
	 
	@Test
	public void classesReferringToABladeInAspectXMlFilesAreBundled() throws Exception {
		given(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
    		.and(workbench).indexPageRefersTo("appns.bs.b1.Class1")
    		.and(blade).classRequires("appns.bs.b1.Class1", "appns.bs.b1.Class2");
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/js/dev/en/combined/bundle.js", response);
		then(response).containsClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2");
	}
	
}
