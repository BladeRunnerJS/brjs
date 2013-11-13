package org.bladerunnerjs.model.appserver;


public interface ApplicationServer
{
	int getPort();
	void start() throws Exception;
	void stop() throws Exception;
}