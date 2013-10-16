package org.bladerunnerjs.model.appserver;


public interface ApplicationServer
{
	void start() throws Exception;
	void stop() throws Exception;
	int getPort();
}