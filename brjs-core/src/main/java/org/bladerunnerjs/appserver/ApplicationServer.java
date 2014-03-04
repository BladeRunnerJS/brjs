package org.bladerunnerjs.appserver;


public interface ApplicationServer
{
	int getPort();
	void start() throws Exception;
	void stop() throws Exception;
	void join() throws Exception;
}