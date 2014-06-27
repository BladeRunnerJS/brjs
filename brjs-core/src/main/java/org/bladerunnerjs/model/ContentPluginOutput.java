package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;


public abstract class ContentPluginOutput
{
	private OutputStream outputStream;
	private Reader reader;
	private Writer writer;
	
	public ContentPluginOutput(OutputStream outputStream, String encoding) {
		this.outputStream = outputStream;
		try {
			writer = new OutputStreamWriter(outputStream, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public Reader getReader() {
		return reader;
	}

	public void setReader(Reader reader) {
		this.reader = reader;
	}

	public Writer getWriter() {
		return writer;
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
	
	
}
