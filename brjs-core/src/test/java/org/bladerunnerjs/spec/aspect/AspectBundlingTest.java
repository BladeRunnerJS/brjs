package org.bladerunnerjs.spec.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class AspectBundlingTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private Blade blade;
	private StringBuffer response = new StringBuffer();
	
	private String CLASS_BUNDLED_MESSAGE = "class bundled"; /* TODO: once Bundler is moved into brjs-core static log messages will be on the relevant object */
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			blade = app.bladeset("bs").blade("b1");
	}
	
	@Test
	public void weBundleAnAspectClassIfItIsReferredToInTheAspectIndexPage() throws Exception {
		given(aspect).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.Class1");
		when(app).requestReceived("/default-aspect/caplin-js/js.bundle", response);
		then(response).containsText("novox.Class1 = function()");
	}
	
	@Test
	public void weBundleABladeClassIfItIsReferredToInTheAspectIndexPage() throws Exception {
		given(blade).hasClass("novox.Class1")
		.and(aspect).indexPageRefersTo("novox.Class1");
		when(app).requestReceived("/default-aspect/caplin-js/js.bundle", response);
		then(response).containsText("novox.Class1 = function()");
	}
	
	@Ignore
	@Test
	public void weBundleTransitiveDependenciesLinkedFromTheIndexPage() throws Exception {
		given(blade).hasClasses("novox.Class1", "novox.Class2")
			.and(blade).classRefersTo("novox.Class1", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.Class1");
		then(aspect).bundledFilesEquals(blade.src().file("novox/Class1.js"), blade.src().file("novox/Class2.js"));
	}
	
	@Ignore
	@Test
	public void weDontBundleAClassIfItIsNotReferredTo() throws Exception {
		given(blade).hasClasses("novox.Class1", "novox.Class2")
			.and(blade).classRefersTo("novox.Class1", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.Class2");
		then(aspect).bundledFilesEquals(blade.src().file("novox/Class2.js"));
	}
	
	@Ignore
	@Test(expected=ClassNotFoundException.class)
	public void classesCanOnlyReferToExistentClasses() throws Exception {
		given(blade).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.NonExistentClass");
		when(aspect).getBundledFiles();
	}
	
	@Ignore
	@Test(expected=ClassNotFoundException.class)
	public void testInfoLogMessageRecieved() throws Exception {
		given(logging).enabled()
			.and(blade).hasClasses("novox.Class1", "novox.Class2")
			.and(blade).classRefersTo("novox.Class1", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.Class2");
		then(logging).infoMessageReceived(CLASS_BUNDLED_MESSAGE, "novox/Class1.js");
	}
	
}