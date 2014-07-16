package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.OutputStream;


public abstract class UrlContentAccessor
{
	
	/**
	 * 
	 * Write the contents of the reply from the given URL to the output stream. 
	 */
	public abstract void writeLocalUrlContentsToOutputStream(String urlPath, OutputStream output) throws IOException;
	
	/**
	 * Attempts to handle the request using the server. If a server context isn't available writes the content of the local url path to the output stream, otherwise
	 * use the server context directly to handle the request.
	 */
	public abstract void handleRequest(String urlPath, OutputStream output) throws IOException;
	
}
