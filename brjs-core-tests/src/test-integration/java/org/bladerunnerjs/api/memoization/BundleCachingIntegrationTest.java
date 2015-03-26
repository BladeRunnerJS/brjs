package org.bladerunnerjs.api.memoization;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.memoization.PollingFileModificationObserverThread;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class BundleCachingIntegrationTest extends SpecTest
{
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
		
		// dont use fields for BRJS objects since we recreate BRJS in each test
	}
	
	@SuppressWarnings("deprecation")
	@After
	public void stopServer() throws Exception {
		if (brjs != null) {
			brjs.applicationServer(appServerPort).stop();
		}
		brjs.getFileWatcherThread().interrupt();
		brjs.getFileWatcherThread().stop();
	}
	
	@Test @Ignore //TODO: this test is unreliable - fix it
	public void fileWatcherWatchesFolderBrjsAppsAtTheSameLevelAsSdk() throws Throwable {
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
	
	@Test
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
		given(brjs.bladerunnerConf()).hasFileObserverValue("polling")
			.and(logging).enabled();
		when(brjs).hasBeenAuthenticallyCreatedWithAutoConfiguredObserverThread();
		then(logging).debugMessageReceived(BRJS.Messages.FILE_WATCHER_MESSAGE, PollingFileModificationObserverThread.class.getSimpleName())
			.and(logging).otherMessagesIgnored();
			
	}
	
	@Test
	public void brjsConfCanBeUsedToConfigureFileObserverToPollingWithASetInterval() throws Throwable {
		given(brjs.bladerunnerConf()).hasFileObserverValue("polling:1000")
			.and(logging).enabled();
    	when(brjs).hasBeenAuthenticallyCreatedWithAutoConfiguredObserverThread();
    	then(logging).debugMessageReceived(BRJS.Messages.FILE_WATCHER_MESSAGE, PollingFileModificationObserverThread.class.getSimpleName())
    		.and(logging).debugMessageReceived(PollingFileModificationObserverThread.THREAD_INIT_MESSAGE, PollingFileModificationObserverThread.class.getSimpleName(), 1000)
    		.and(logging).otherMessagesIgnored();
	}
	
	@Test
	public void brjsConfCanBeUsedToConfigureFileObserveToWatching() throws Throwable {
		given(brjs.bladerunnerConf()).hasFileObserverValue("watching")
			.and(logging).enabled();
		when(brjs).hasBeenAuthenticallyCreatedWithAutoConfiguredObserverThread();
		then(logging).debugMessageReceived(BRJS.Messages.FILE_WATCHER_MESSAGE, WatchingFileModificationObserverThread.class.getSimpleName())
			.and(logging).otherMessagesIgnored();
	}
	
	@Test
	public void watchingFileObserverIsUsedAsDefault() throws Throwable {
		given(logging).enabled();
		when(brjs).hasBeenAuthenticallyCreatedWithAutoConfiguredObserverThread();
		then(logging).debugMessageReceived(BRJS.Messages.FILE_WATCHER_MESSAGE, WatchingFileModificationObserverThread.class.getSimpleName())
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
		given(brjs.bladerunnerConf()).hasFileObserverValue("invalid");
		when(brjs).hasBeenAuthenticallyCreatedWithAutoConfiguredObserverThread();
		then(exceptions).verifyException(ConfigException.class, "invalid", "polling(:([0-9]+))?", "watching");
	}
	
}
