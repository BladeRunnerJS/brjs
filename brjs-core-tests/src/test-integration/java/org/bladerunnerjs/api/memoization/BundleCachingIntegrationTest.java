package org.bladerunnerjs.api.memoization;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BundleCachingIntegrationTest extends SpecTest
{

	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	@Test
	public void fileWatcherWatchesFolderBrjsAppsAtTheSameLevelAsSdk() throws Exception {
		given(brjs).hasBeenAuthenticallyCreatedWithFileWatcherThread();
			app = brjs.app("app1");
			aspect = app.defaultAspect();
			given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageHasContent("require('appns/App')")
			.and(aspect).classFileHasContent("App", "// App.js")
			.and(aspect).hasReceivedRequest("js/dev/combined/bundle.js");
		when(aspect).indexPageRefersToWithoutNotifyingFileRegistry("require('appns/AppClass')")
			.and(aspect).fileHasContentsWithoutNotifyingFileRegistry("src/AppClass.js", "// AppClass.js");
		then(aspect).devResponseEventuallyContains("js/dev/combined/bundle.js", "AppClass.js", response)
			.and(response).doesNotContainText("App.js");
	}
	
}
