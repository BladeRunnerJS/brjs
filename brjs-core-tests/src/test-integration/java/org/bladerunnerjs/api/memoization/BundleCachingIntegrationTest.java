package org.bladerunnerjs.api.memoization;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.spec.brjs.BRJSTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BundleCachingIntegrationTest extends SpecTest
{

	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	private File secondaryTempFolder = null;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
	}
	
	@After
	public void deleteCreatedBrjsAppsDirFromTemp() throws IOException {
		if (secondaryTempFolder != null) FileUtils.deleteQuietly(secondaryTempFolder);
	}
	
	@Test
	public void fileWatcherDetectsChangesWhenBrjsAppsAtTheSameLevelAsSdk() throws Exception {
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
	
	@Test
	public void fileWatcherDetectsChangesWhenBrjsAppsAtAnotherLevelThanTheSdk() throws Exception {
		secondaryTempFolder = org.bladerunnerjs.utility.FileUtils.createTemporaryDirectory(BRJSTest.class);
		given(secondaryTempFolder).containsFolder("brjs-apps")
			.and(brjs).hasBeenAuthenticallyCreatedWithFileWatcherThreadAndWorkingDir(new File(secondaryTempFolder, "brjs-apps"));
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
