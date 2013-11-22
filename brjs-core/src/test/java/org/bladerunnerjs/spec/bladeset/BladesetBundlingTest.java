package org.bladerunnerjs.spec.bladeset;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class BladesetBundlingTest extends SpecTest {
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
	public void weDontBundleABladesetIfItIsNotReferredToByAnAspect() throws Exception {
		given(bladeset).hasPackageStyle("src/novox/bs", "caplin-js")
			.and(bladeset).hasClasses("novox.bs.Class1", "novox.bs.Class2")
			.and(bladeset).classRefersTo("novox.bs.Class1", "novox.bs.Class2")
			.and(aspect).indexPageRefersTo("novox.bs.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/js.bundle", response);
		then(response).containsClasses("novox.bs.Class2");
	}
	
	@Test
	public void bladesetClassesCanOnlyDependOnExistentClasses() throws Exception {
		given(bladeset).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(bladeset).classDependsOn("novox.Class1", "novox.NonExistentClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/js.bundle", response);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "novox/NonExistentClass")
			.whereTopLevelExceptionIs(BundlerProcessingException.class);
	}
	
	@Test
	public void bladesetClassesThatReferToNonExistentClassesWontCauseAnExceptionWhenAspectIsRequested() throws Exception {
		given(bladeset).hasPackageStyle("src/novox/bs", "caplin-js")
			.and(bladeset).hasClass("novox.bs.Class1")
			.and(aspect).indexPageRefersTo("novox.bs.Class1")
			.and(bladeset).classRefersTo("novox.bs.Class1", "novox.bs.NonExistentClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/js.bundle", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	// W O R K B E N C H
	//TODO
}
