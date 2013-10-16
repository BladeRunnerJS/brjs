package com.caplin.cutlass.bundler.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import com.caplin.cutlass.EncodingAccessor;

public class BundlerFileReader extends Reader
{
	private boolean readStarted;
	private InputStreamReader inputStreamReader;
	private PushbackInputStream pushbackInputStream;
	
	public BundlerFileReader(File file) throws FileNotFoundException, UnsupportedEncodingException
	{
		FileInputStream fileInputStream = new FileInputStream(file);
		pushbackInputStream = new PushbackInputStream(fileInputStream, 3);
		inputStreamReader = new InputStreamReader(pushbackInputStream, EncodingAccessor.getDefaultInputEncoding());
	}
	
	@Override
	public int read(char[] cbuf, int offset, int length) throws IOException
	{
		if(!readStarted)
		{
			byte[] buffer = new byte[3];
			int readBytes = pushbackInputStream.read(buffer, 0, 3);
			if (bufferDoesnContainBom(buffer) && readBytes != -1)
			{
				pushbackInputStream.unread(buffer, 0, readBytes);
			}
			readStarted = true;
		}
		return inputStreamReader.read(cbuf, offset, length);
	}

	private boolean bufferDoesnContainBom(byte[] buffer)
	{
		return buffer[0] != (byte) 0xef && buffer[1] != (byte) 0xbb && buffer[2] != (byte) 0xbf;
	}

	@Override
	public void close() throws IOException
	{
		inputStreamReader.close();
	}
}