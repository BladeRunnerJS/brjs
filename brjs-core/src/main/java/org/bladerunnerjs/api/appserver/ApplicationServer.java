package org.bladerunnerjs.api.appserver;

import org.bladerunnerjs.api.App;


/**
 * The development application server. 
 */
public interface ApplicationServer
{
	int getPort();
	
	/**
	 * Start the application server. This method should start the server in a new thread so the call does not block.
	 * @throws Exception
	 */
	void start() throws Exception;
	
	/**
	 * Stop the application server thread.
	 * @throws Exception
	 */
	void stop() throws Exception;
	
	/**
	 * Wait for the application server thread to terminate. This method behaves in a similar way to {@link Thread#join}
	 * @throws Exception
	 */
	void join() throws Exception;
	
	/**
	 * Set how often the {@link App} deployment watcher should check for new apps that need deploying to the application server.
	 * @param interval The check interval
	 */
	void setAppDeploymentWatcherInterval(long interval);
}