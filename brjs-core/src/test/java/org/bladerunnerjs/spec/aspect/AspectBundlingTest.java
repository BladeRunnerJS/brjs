package org.bladerunnerjs.spec.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class AspectBundlingTest extends SpecTest {
	private App app = brjs.app("app1");
	private Aspect aspect = app.aspect("default");
	private Blade blade = app.bladeset("bs").blade("b1");
	
	private String CLASS_BUNDLED_MESSAGE = "class bundled"; /* TODO: once Bundler is moved into brjs-core static log messages will be on the relevant object */
	
	@Before
	public void setUp() throws Exception
	{
		given(app).hasBeenCreated();
	}
	
	@Ignore
	@Test
	public void weBundleAClassIfItIsReferredToInTheAspectIndexPage() throws Exception {
		given(blade).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.Class1");
		then(aspect).bundledFilesEquals(blade.src().file("novox/Class1.js"));
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