package org.bladerunnerjs.api.memoization;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.memoization.PollingFileModificationObserver;
import org.bladerunnerjs.memoization.WatchingFileModificationObserver;
import org.bladerunnerjs.spec.brjs.BRJSTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class BundleCachingIntegrationTest extends SpecTest
{
	private StringBuffer response = new StringBuffer();
	private File secondaryTempFolder = null;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
		
		// dont use fields for BRJS objects since we recreate BRJS in each test
	}
	
	@After
	public void stopServer() throws Exception {
		if (brjs != null) {
			brjs.applicationServer(appServerPort).stop();
		}
		try {
			brjs.fileObserver().stop();
		} catch (Exception ex) {
			// ignore
		}
		try {
			fileWatcherThread.stop();
		} catch (Exception ex) {
			// ignore
		}
	}
	
	@After
	public void deleteCreatedBrjsAppsDirFromTemp() throws IOException {
		if (secondaryTempFolder != null) FileUtils.deleteQuietly(secondaryTempFolder);
	}
	
	@Test @Ignore //TODO: this test is unreliable - fix it
	public void fileWatcherDetectsChangesWhenBrjsAppsAtTheSameLevelAsSdk() throws Exception {
		given(brjs).hasBeenAuthenticallyCreatedWithFileWatcherThread();
			App app = brjs.app("app1");
			Aspect aspect = app.defaultAspect();
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
	
	@Test @Ignore //TODO: this test is unreliable - fix it
	public void fileWatcherPollsFolderBrjsAppsAtTheSameLevelAsSdk() throws Throwable {
		given(brjs).hasBeenAuthenticallyCreatedWithFilePollingThread();
		App app = brjs.app("app1");
		Aspect aspect = app.defaultAspect();
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
	public void brjsConfCanBeUsedToConfigureFileObserverToPolling() throws Throwable {
		System.err.println("Running test: brjsConfCanBeUsedToConfigureFileObserverToPolling");
		given(brjs.bladerunnerConf()).hasFileObserverValue("polling")
			.and(logging).enabled();
		when(brjs).hasBeenAuthenticallyCreatedWithAutoConfiguredObserverThread();
		then(logging).debugMessageReceived(BRJS.Messages.FILE_WATCHER_MESSAGE, PollingFileModificationObserver.class.getSimpleName())
			.and(logging).otherMessagesIgnored();
			
	}
	
	@Test
	public void brjsConfCanBeUsedToConfigureFileObserverToPollingWithASetInterval() throws Throwable {
		System.err.println("Running test: brjsConfCanBeUsedToConfigureFileObserverToPollingWithASetInterval");
		given(brjs.bladerunnerConf()).hasFileObserverValue("polling:1000")
			.and(logging).enabled();
    	when(brjs).hasBeenAuthenticallyCreatedWithAutoConfiguredObserverThread();
    	then(logging).debugMessageReceived(BRJS.Messages.FILE_WATCHER_MESSAGE, PollingFileModificationObserver.class.getSimpleName())
    		.and(logging).debugMessageReceived(PollingFileModificationObserver.INIT_MESSAGE, PollingFileModificationObserver.class.getSimpleName(), 1000)
    		.and(logging).otherMessagesIgnored();
	}
	
	@Test
	public void brjsConfCanBeUsedToConfigureFileObserveToWatching() throws Throwable {
		System.err.println("Running test: brjsConfCanBeUsedToConfigureFileObserveToWatching");
		given(brjs.bladerunnerConf()).hasFileObserverValue("watching")
			.and(logging).enabled();
		when(brjs).hasBeenAuthenticallyCreatedWithAutoConfiguredObserverThread();
		then(logging).debugMessageReceived(BRJS.Messages.FILE_WATCHER_MESSAGE, WatchingFileModificationObserver.class.getSimpleName())
			.and(logging).otherMessagesIgnored();
	}
	
	@Test
	public void watchingFileObserverIsUsedAsDefault() throws Throwable {
		System.err.println("Running test: watchingFileObserverIsUsedAsDefault");
		given(logging).enabled();
		when(brjs).hasBeenAuthenticallyCreatedWithAutoConfiguredObserverThread();
		then(logging).debugMessageReceived(BRJS.Messages.FILE_WATCHER_MESSAGE, WatchingFileModificationObserver.class.getSimpleName())
			.and(logging).otherMessagesIgnored();
	}
	
	@Test @Ignore //TODO: this test is unreliable - fix it
	public void brjsConfConfiguredFileObserverDetectsChanges() throws Throwable {
		given(brjs).hasBeenAuthenticallyCreatedWithAutoConfiguredObserverThread();
    		App app = brjs.app("app1");
    		Aspect aspect = app.defaultAspect();
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
	public void exceptionIsThrownIfFileObserverValueIsInvalid() throws Throwable {
		System.err.println("Running test: exceptionIsThrownIfFileObserverValueIsInvalid");
		given(brjs.bladerunnerConf()).hasFileObserverValue("invalid");
		when(brjs).hasBeenAuthenticallyCreatedWithAutoConfiguredObserverThread();
		then(exceptions).verifyException(ConfigException.class, "invalid", "polling(:([0-9]+))?", "watching");
	}
	
	@Test @Ignore
	public void fileWatcherDetectsChangesWhenBrjsAppsAtAnotherLevelThanTheSdk() throws Exception {
		secondaryTempFolder = org.bladerunnerjs.utility.FileUtils.createTemporaryDirectory(BRJSTest.class);
		given(secondaryTempFolder).containsFolder("brjs-apps")
			.and(brjs).hasBeenAuthenticallyCreatedWithFileWatcherThreadAndWorkingDir(new File(secondaryTempFolder, "brjs-apps"));
			App app = brjs.app("app1");
			Aspect aspect = app.defaultAspect();
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
