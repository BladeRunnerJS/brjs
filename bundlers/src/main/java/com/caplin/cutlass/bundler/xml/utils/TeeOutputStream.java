package com.caplin.cutlass.bundler.xml.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TeeOutputStream extends FilterOutputStream
{
	private OutputStream out1;
	private OutputStream out2;
	
	public TeeOutputStream(OutputStream stream1, OutputStream stream2)
	{
		super(stream1);
		out1 = stream1;
		out2 = stream2;
	}
	
	public void write(int b) throws IOException
	{
		out1.write(b);
		out2.write(b);
	}
	
	public void write(byte[] data, int offset, int length) throws IOException
	{
		out1.write(data, offset, length);
		out2.write(data, offset, length);
	}
	
	public void flush() throws IOException
	{
		out1.flush();
		out2.flush();
	}
	
	public void close() throws IOException
	{
		out1.close();
		out2.close();
	}
}