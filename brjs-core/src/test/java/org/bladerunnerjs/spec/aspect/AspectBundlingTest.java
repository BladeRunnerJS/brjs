package org.bladerunnerjs.spec.aspect;

import static org.bladerunnerjs.model.utility.LogicalRequestHandler.Messages.*;
import static org.bladerunnerjs.model.BundleSetCreator.Messages.*;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


// TODO: change all tests within this test class to go through the composite bundler (we can create some other test classes for bundler specific testing)
public class AspectBundlingTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private Blade blade;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			blade = app.bladeset("bs").blade("b1");
	}
	
	@Test
	public void weBundleAnAspectClassIfItIsReferredToInTheIndexPage() throws Exception {
		given(aspect).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/js.bundle", response);
		then(response).containsClasses("novox.Class1");
	}
	
	@Test
	public void weBundleABladeClassIfItIsReferredToInTheIndexPage() throws Exception {
		given(blade).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/js.bundle", response);
		then(response).containsClasses("novox.Class1");
	}
	
	@Test
	public void weBundleImplicitTransitiveDependencies() throws Exception {
		given(blade).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(blade).classRefersTo("novox.Class1", "novox.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/js.bundle", response);
		then(response).containsClasses("novox.Class1", "novox.Class2");
	}
	
	@Test
	@Ignore
	public void weBundleExplicitTransitiveDependencies() throws Exception {
		given(blade).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(blade).classDependsOn("novox.Class1", "novox.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/js.bundle", response);
		then(response).containsClasses("novox.Class1", "novox.Class2");
	}
	
	@Test
	public void weDontBundleAClassIfItIsNotReferredTo() throws Exception {
		given(blade).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.Class2")
			.and(blade).classRefersTo("novox.Class1", "novox.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/js.bundle", response);
		then(response).containsClasses("novox.Class2");
	}
	
	@Test
	@Ignore
	public void classesCanOnlyDependOnExistentClasses() throws Exception {
		given(blade).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(blade).classDependsOn("novox.Class1", "novox.NonExistentClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/js.bundle", response);
		then(exceptions).verifyException(ClassNotFoundException.class, "novox/NonExistentClass.js");
	}
	
	@Test
	public void classesThatReferToExistentClassesWontCauseAnException() throws Exception {
		given(blade).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(blade).classRefersTo("novox.Class1", "novox.NonExistentClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/js.bundle", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	// TODO: uncomment missing test lines as bugs are fixed
	// TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
	@Test
	public void helpfulLoggingMessagesAreEmitted() throws Exception {
		given(logging).enabled()
			.and(blade).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1")
			.and(aspect).resourceFileRefersTo("html/view.html", "novox.Class1")
			.and(blade).classRefersTo("novox.Class1", "novox.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/js.bundle", response);
		then(logging).debugMessageReceived(REQUEST_HANDLED_MSG, "js/dev/en_GB/combined/js.bundle", "app1")
			.and(logging).debugMessageReceived(CONTEXT_IDENTIFIED_MSG, "Aspect", "default", "js/dev/en_GB/combined/js.bundle")
			.and(logging).debugMessageReceived(BUNDLER_IDENTIFIED_MSG, "CompositeJsBundlerPlugin", "js/dev/en_GB/combined/js.bundle")
			.and(logging).debugMessageReceived(BUNDLABLE_NODE_SEED_FILES_MSG, unquoted("Aspect"), "default", unquoted("'index.html', 'resources/html/view.html', 'resources/xml/config.xml'"))
			.and(logging).debugMessageReceived(APP_SOURCE_LOCATIONS_MSG, "app1", "'default-aspect/', 'bs-bladeset/', 'bs-bladeset/blades/b1/'")
			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "index.html", "'src/novox/Class1.js'")
//			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "xml/config.xml", "'src/novox/Class1.js'") // TODO: uncomment this line once xml seed files are supported
//			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "novox/Class1.js", "'src/novox/Class2.js'") // TODO: uncomment this line once logging assertions don't just grab the first matching message they find
			.and(logging).debugMessageReceived(FILE_HAS_NO_DEPENDENCIES_MSG, "src/novox/Class2.js");
	}
	
	@Test
	public void helpfulLoggingMessagesAreEmittedWhenThereAreNoSeedFiles() throws Exception {
		given(logging).enabled()
			.and(blade).hasClasses("novox.Class1", "novox.Class2")
			.and(blade).classRefersTo("novox.Class1", "novox.Class2")
			.and(aspect).hasBeenCreated();
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/js.bundle", response);
		then(logging).debugMessageReceived(REQUEST_HANDLED_MSG, "js/dev/en_GB/combined/js.bundle", "app1")
			.and(logging).debugMessageReceived(CONTEXT_IDENTIFIED_MSG, unquoted("Aspect"), "default", "js/dev/en_GB/combined/js.bundle")
			.and(logging).debugMessageReceived(BUNDLER_IDENTIFIED_MSG, "CompositeJsBundlerPlugin", "js/dev/en_GB/combined/js.bundle")
			.and(logging).debugMessageReceived(BUNDLABLE_NODE_HAS_NO_SEED_FILES_MSG, unquoted("Aspect"), "default")
			.and(logging).debugMessageReceived(APP_SOURCE_LOCATIONS_MSG, "app1", unquoted("'default-aspect/', 'bs-bladeset/', 'bs-bladeset/blades/b1/'"));
	}
}