package org.bladerunnerjs.api.memoization;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WatchingFileModificationObserverThreadSpectTest extends SpecTest{

	private final PrintStream oldOutput = System.out;
	private ByteArrayOutputStream outputContents; 
	
	@Before
	public void setup() {
		outputContents = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputContents));
	}
	
	@After
	public void tearDown() {
		System.setOut(oldOutput);
	} 
	
	@Test //TODO why does our log message go missing?
	public void logLineIsDisplayedWhenBrjsHasBeenCreatedWithWatchingFileThread() throws Exception {
		/* Our test should look like: 
		 * given(logging).enabled();
		 * then(logging).debugMessageReceived(WatchingFileModificationObserverThread.THREAD_STARTED, WatchingFileModificationObserverThread.THREAD_IDENTIFIER)
				.and(logging).otherMessagesIgnored();
		 * But after line 'logger.debug(THREAD_STARTED, THREAD_IDENTIFIER);' of WatchingFileModificationObserverThread 
		 * the logStore debugMessages doesn't show this	message as existing even though it was output.	
		 */ 
		given(logging).echoEnabled();
		when(brjs).hasBeenAuthenticallyCreatedWithFileWatcherThread();
		for (int i = 0; i < 10; i++) {
			if (outputContents.toString().contains("Thread WatchingFileModificationObserverThread has been started.")) {
				return;
			}
			Thread.sleep(250);
		}
		fail("Didn't receive thread start message");
		
	}
	
}
