package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.OutputStream;


public abstract class UrlContentAccessor
{
	
	/**
	 * Write the contents of the reply from the given URL to the output stream. 
	 * @deprecated Use handleRequest(String, OutputStream) instead
	 */
	public abstract void writeLocalUrlContentsToOutputStream(String urlPath, OutputStream output) throws IOException;
	
	/**
	 * Attempts to handle the request using the server. 
	 * If a server context isn't available writes the content of the local URL path to the output stream, otherwise
	 * uses the server context to handle the request and write to the output stream.
	 */
	public abstract void handleRequest(String urlPath, OutputStream output) throws IOException;
	
}
