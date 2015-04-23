package org.bladerunnerjs.api;

import java.io.IOException;

/**
 * An observer that monitors {@link Asset}s for changes.
 */
public interface FileObserver
{

	/**
	 * The method starts a FileObserver object for monitoring files for changes.
	 * 
	 * @throws IOException if an error occurs initialising the observer
	 */
	void start() throws IOException;
	
	/**
	 * The method stops a FileObserver object for monitoring files for changes.
	 * 
	 * @throws IOException if an error occurs stopping the observer
	 */
	void stop() throws IOException, InterruptedException;;
	
}
