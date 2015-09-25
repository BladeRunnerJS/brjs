package org.bladerunnerjs.spec.bundling.aspect;

import static org.bladerunnerjs.model.BundleSetCreator.Messages.*;
import static org.bladerunnerjs.model.AbstractBundlableNode.Messages.*;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.spec.engine.SpecTest;
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
	private JsLib sdkLib;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			sdkLib = brjs.sdkLib("br");
	}
	
	@Test
	public void helpfulLoggingMessagesAreEmitted() throws Exception {
		given(logging).enabled()
			.and(brjs).pluginsAccessed()
			.and(blade).hasClasses("appns/bs/b1/Class1", "appns/bs/b1/Class2")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.bs.b1.Class1")
			.and(blade).classRequires("appns/bs/b1/Class1", "appns/bs/b1/Class2")
			.and(sdkLib).hasBeenCreated();
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(logging).debugMessageReceived(REQUEST_HANDLED_MSG, "js/dev/combined/bundle.js", "app1")
			.and(logging).debugMessageReceived(CONTEXT_IDENTIFIED_MSG, "Aspect", "default", "js/dev/combined/bundle.js")
			.and(logging).debugMessageReceived(BUNDLER_IDENTIFIED_MSG, "CompositeJsContentPlugin", "js/dev/combined/bundle.js")
			.and(logging).debugMessageReceived(BUNDLABLE_NODE_SEED_FILES_MSG, unquoted("Aspect"), "default", unquoted("'default-aspect/index.html', 'default-aspect/resources/xml/config.xml', 'bs-bladeset/blades/b1', 'default-aspect'"))
			.and(logging).debugMessageReceived(APP_SOURCE_LOCATIONS_MSG, "app1", "'default-aspect', 'bs-bladeset', 'bs-bladeset/blades/b1', 'bs-bladeset/blades/b1/workbench', 'sdk/libs/javascript/br'")
			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "default-aspect/index.html", "'bs-bladeset/blades/b1/src/appns/bs/b1/Class1.js', 'default-aspect'")
			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "bs-bladeset/blades/b1/src/appns/bs/b1/Class1.js", "'bs-bladeset/blades/b1/src/appns/bs/b1/Class2.js', 'bs-bladeset/blades/b1'")
			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "bs-bladeset/blades/b1/src/appns/bs/b1/Class2.js", "'bs-bladeset/blades/b1'")
			.and(logging).debugMessageReceived(FILE_DEPENDENCIES_MSG, "default-aspect", "'default-aspect/resources', 'default-aspect/resources/xml', 'default-aspect/resources/xml/config.xml'")
			.and(logging).otherMessagesIgnored();
	}
	
	// 'helpfulLoggingMessagesAreEmittedWhenThereAreNoSeedFiles' test is no longer valid with our current set of plugins but *may* be true if we stop using BRJS conformant Asset plugins 
}
