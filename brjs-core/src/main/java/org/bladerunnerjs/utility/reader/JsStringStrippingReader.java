package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;


public class JsStringStrippingReader extends AbstractStrippingReader
{

	private Reader sourceReader;

	public JsStringStrippingReader(Reader sourceReader)
	{
		this.sourceReader = sourceReader;
	}

	@Override
	public void close() throws IOException
	{
		sourceReader.close();
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return sourceReader.read(cbuf, off, len);
	}

}
