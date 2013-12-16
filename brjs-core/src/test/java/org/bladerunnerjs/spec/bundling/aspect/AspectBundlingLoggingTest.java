package org.bladerunnerjs.spec.bundling.aspect;

import static org.bladerunnerjs.utility.LogicalRequestHandler.Messages.*;
import static org.bladerunnerjs.model.BundleSetCreator.Messages.*;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'appns' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectBundlingLoggingTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
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
	
	@Test
	public void helpfulLoggingMessagesAreEmitted() throws Exception {
		given(logging).enabled()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.bs.b1.Class1")
			.and(blade).classRequires("appns.bs.b1.Class1", "appns.bs.b1.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(logging).debugMessageReceived(REQUEST_HANDLED_MSG, "js/dev/en_GB/combined/bundle.js", "app1")
			.and(logging).debugMessageReceived(CONTEXT_IDENTIFIED_MSG, "Aspect", "default", "js/dev/en_GB/combined/bundle.js")
			.and(logging).debugMessageReceived(BUNDLER_IDENTIFIED_MSG, "CompositeJsBundlerContentPlugin", "js/dev/en_GB/combined/bundle.js")
			.and(logging).debugMessageReceived(BUNDLABLE_NODE_SEED_FILES_MSG, unquoted("Aspect"), "default", unquoted("'index.html', 'resources/xml/config.xml'"))
			.and(logging).debugMessageReceived(APP_SOURCE_LOCATIONS_MSG, "app1", "'default-aspect/', 'bs-bladeset/', 'bs-bladeset/blades/b1/', 'sdk/libs/javascript/caplin'")
			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "index.html", "'src/appns/bs/b1/Class1.js'")
			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "src/appns/bs/b1/Class1.js", "'src/appns/bs/b1/Class2.js'")
			.and(logging).debugMessageReceived(FILE_HAS_NO_DEPENDENCIES_MSG, "src/appns/bs/b1/Class2.js")
			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "resources/xml/config.xml", "'src/appns/bs/b1/Class1.js'");
	}
	
	@Test
	public void helpfulLoggingMessagesAreEmittedWhenThereAreNoSeedFiles() throws Exception {
		given(logging).enabled()
			.and(blade).hasClasses("appns.Class1", "appns.Class2")
			.and(blade).classRequires("appns.Class1", "appns.Class2")
			.and(aspect).hasBeenCreated();
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(logging).debugMessageReceived(REQUEST_HANDLED_MSG, "js/dev/en_GB/combined/bundle.js", "app1")
			.and(logging).debugMessageReceived(CONTEXT_IDENTIFIED_MSG, unquoted("Aspect"), "default", "js/dev/en_GB/combined/bundle.js")
			.and(logging).debugMessageReceived(BUNDLER_IDENTIFIED_MSG, "CompositeJsBundlerContentPlugin", "js/dev/en_GB/combined/bundle.js")
			.and(logging).debugMessageReceived(BUNDLABLE_NODE_HAS_NO_SEED_FILES_MSG, unquoted("Aspect"), "default")
			.and(logging).debugMessageReceived(APP_SOURCE_LOCATIONS_MSG, "app1", unquoted("'default-aspect/', 'bs-bladeset/', 'bs-bladeset/blades/b1/', 'sdk/libs/javascript/caplin'"));
	}
}
