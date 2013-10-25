package com.caplin.cutlass.bundler.js;

import java.io.IOException;
import java.io.Writer;

public class CharacterCountingWriter extends Writer
{
	private Writer writer;
	public int offsetLine = 0;
	public int offsetIndex = 0;
	
	public CharacterCountingWriter(Writer writer)
	{
		this.writer = writer;
	}
	
	@Override
	public void close() throws IOException
	{
		writer.close();
	}
	
	@Override
	public void flush() throws IOException
	{
		writer.flush();
	}
	
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException
	{
		// TODO: update offsetLine & offsetIndex
		writer.write(cbuf, off, len);
	}
}
