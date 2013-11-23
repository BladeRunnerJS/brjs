package org.bladerunnerjs.spec.blade;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class BladeBundlingTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private Blade blade;
	private Bladeset bladeset;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
		
		app = brjs.app("app1");
		aspect = app.aspect("default");
		bladeset = app.bladeset("bs");
		blade = bladeset.blade("b1");
	}
	
	// Specific edge cases not covered in Aspect/Workbench bundling tests	
	
	// A S P E C T
	@Test
	public void weDontBundleABladeIfItIsNotReferredToAnAspect() throws Exception {
		given(blade).hasPackageStyle("src/novox/bs/b1", "caplin-js")
			.and(blade).hasClasses("novox.bs.b1.Class1", "novox.bs.b1.Class2")
			.and(blade).classRefersTo("novox.bs.b1.Class1", "novox.bs.b1.Class2")
			.and(aspect).indexPageRefersTo("novox.bs.b1.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("novox.bs.b1.Class2");
	}
	
	@Test
	public void bladeClassesCanOnlyDependOnExistentClassesWhenAspectIsRequested() throws Exception {
		given(blade).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(blade).classDependsOn("novox.Class1", "novox.NonExistentClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "novox/NonExistentClass")
			.whereTopLevelExceptionIs(BundlerProcessingException.class);
	}
	
	@Test
	public void bladeClassesThatReferToNonExistentClassesWontCauseAnExceptionWhenAspectIsRequested() throws Exception {
		given(blade).hasPackageStyle("src/novox/bs/b1", "caplin-js")
			.and(blade).hasClass("novox.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("novox.bs.b1.Class1")
			.and(blade).classRefersTo("novox.bs.b1.Class1", "novox.bs.b1.NonExistentClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	// W O R K B E N C H
	//TODO
}
