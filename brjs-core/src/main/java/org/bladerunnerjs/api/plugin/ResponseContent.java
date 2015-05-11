package org.bladerunnerjs.api.plugin;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An interface for writing and closing binary and char streams.
 */

public interface ResponseContent extends AutoCloseable
{
	/**
	 * The method writes the content of the corresponding property of the class e.g. Reader or InputStream to the specified OutputStream.
	 * 
	 * @param outputStream an OutputStream object that will contain the newly added stream
	 */
	public void write(OutputStream outputStream) throws IOException;
	
	/**
	 * The method closesthe corresponding property of the class e.g. Reader or InputStream .
	 */
	public void close();
}
