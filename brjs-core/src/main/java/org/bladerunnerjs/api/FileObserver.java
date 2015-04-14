package org.bladerunnerjs.api;

import java.io.IOException;


public interface FileObserver
{

	void start() throws IOException;
	void stop() throws IOException, InterruptedException;;
	
}
