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
	
}
