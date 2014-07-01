package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;


public abstract class UrlContentAccessor
{
	
	/**
	 * Write the contents of the reply from the given URL to the writer. Binary unsafe since it's assume the content is a String.
	 */
	public abstract void writeLocalUrlContentsToWriter(String urlPath, Writer writer) throws IOException;
	
	/**
	 * 
	 * Write the contents of the reply from the given URL to the output stream. A binary safe equivalent of writeLocalUrlContentsToWriter 
	 */
	public abstract void writeLocalUrlContentsToOutputStream(String urlPath, OutputStream output) throws IOException;
	
}
