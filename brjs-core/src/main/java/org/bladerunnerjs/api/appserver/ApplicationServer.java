package org.bladerunnerjs.api.appserver;


public interface ApplicationServer
{
	int getPort();
	void start() throws Exception;
	void stop() throws Exception;
	void join() throws Exception;
	void setAppDeploymentWatcherInterval(long interval);
}