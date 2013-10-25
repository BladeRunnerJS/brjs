package com.caplin.appserver.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpHeaders;

public class GZipOutputStream extends ServletOutputStream
{
	private final static String GZIP_ENCODING_HEADER_VALUE = "gzip";
	
	private final HttpServletResponse httpServletResponse;
	private final ServletOutputStream outputStream;
	private final ByteArrayOutputStream byteArrayOutputStream;
	private final GZIPOutputStream gZipOutputStream;
	
	private boolean closed = false;

	public GZipOutputStream(HttpServletResponse response) throws IOException
	{
		super();
		
		httpServletResponse = response;
		outputStream = response.getOutputStream();
		byteArrayOutputStream = new ByteArrayOutputStream();
		gZipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
	}

	public void close() throws IOException
	{
		if (closed)
		{
			throw new IOException("Cannot close an output stream that has already been closed");
		}
		gZipOutputStream.finish();
	
		byte[] bytes = byteArrayOutputStream.toByteArray();
		
		// add the appropriate headers for the gzip compression
		httpServletResponse.addHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(bytes.length)); 
		httpServletResponse.addHeader(HttpHeaders.CONTENT_ENCODING, GZIP_ENCODING_HEADER_VALUE);
		
		// finish writing the stream
		outputStream.write(bytes);
		outputStream.flush();
		outputStream.close();
		
		closed = true;
	}

	public void flush() throws IOException
	{
		if (closed)
		{
			throw new IOException("Cannot flush a closed output stream");
		}
		gZipOutputStream.flush();
	}

	public void write(int b) throws IOException
	{
		if (closed)
		{
			throw new IOException("Cannot write to a closed output stream");
		}
		gZipOutputStream.write((byte) b);
	}

	public void write(byte b[]) throws IOException
	{
		write(b, 0, b.length);
	}

	public void write(byte b[], int off, int len) throws IOException
	{
		if (closed)
		{
			throw new IOException("Cannot write to a closed output stream");
		}
		gZipOutputStream.write(b, off, len);
	}

	public boolean closed()
	{
		return closed;
	}

	public void reset()
	{
	}
}
