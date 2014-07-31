package org.bladerunnerjs.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;


public class BinaryResponseContent implements ResponseContent
{
	
	private InputStream input;

	public BinaryResponseContent(InputStream input) {
		this.input = input;
	}

	public InputStream getInputStream() {
		return input;
	}
	
	@Override
	public void write(OutputStream outputStream) throws IOException
	{
		IOUtils.copy(input, outputStream);
		outputStream.flush();
	}
	
	@Override
	public void close()
	{
		try
		{
			input.close();
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
}
