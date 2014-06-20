package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;


public abstract class ContentOutputStream extends OutputStream
{
	
	private OutputStream outputStream;

	public ContentOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	/**
	 * Write the contents of the reply from the given URL to the writer. Binary unsafe since it's assume the content is a String.
	 */
	public abstract void writeLocalUrlContentsToWriter(String urlPath, Writer writer) throws IOException;
	
	/**
	 * 
	 * Write the contents of the reply from the given URL to the output stream. A binary safe equivalent of writeLocalUrlContentsToWriter 
	 */
	public abstract void writeLocalUrlContentsToAnotherStream(String urlPath, OutputStream output) throws IOException;
	
	/**
	 * Behaves the same as writeLocalUrlContentsToAnotherStream, writing the contents to this OutputStream.
	 */
	public abstract void writeLocalUrlContents(String url) throws IOException;
	
	@Override
	public void write(int b) throws IOException
	{
		outputStream.write(b);
	}
}
