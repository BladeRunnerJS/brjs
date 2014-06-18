package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.OutputStream;


public abstract class ContentOutputStream extends OutputStream
{
	
	private OutputStream outputStream;

	public ContentOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	public abstract String getLocalUrlContents(String urlPath) throws IOException;
	public abstract void writeLocalUrlContents(String url) throws IOException;
	
	@Override
	public void write(int b) throws IOException
	{
		outputStream.write(b);
	}
}
