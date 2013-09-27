package com.caplin.appserver.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class GZipResponseWrapper extends HttpServletResponseWrapper
{
	private final static String UTF_8_CHARACTER_SET = "UTF-8";

	private final HttpServletResponse wrappedResponse;
	private ServletOutputStream stream = null;
	private PrintWriter writer = null;

	public GZipResponseWrapper(HttpServletResponse wrappedResponse)
	{
		super(wrappedResponse);
		this.wrappedResponse = wrappedResponse;
	}

	private ServletOutputStream createOutputStream() throws IOException
	{
		return new GZipOutputStream(wrappedResponse);
	}

	public void finishResponse()
	{
		try
		{
			if (writer != null)
			{
				writer.close();
			}
			else if (stream != null)
			{
				stream.close();
			}
		}
		catch (IOException e)
		{
			// ignore this - there is not a lot that we can do with it
		}
	}

	@Override
	public void flushBuffer() throws IOException
	{
		if (stream != null)
		{
			stream.flush();
		}
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException
	{
		if (writer != null)
		{
			throw new IllegalStateException("getWriter() has already been called!");
		}

		if (stream == null)
		{
			stream = createOutputStream();
		}

		return stream;
	}

	@Override
	public PrintWriter getWriter() throws IOException
	{
		if (writer == null)
		{
			if (stream != null)
			{
				throw new IllegalStateException("getOutputStream() has already been called!");
			}

			stream = createOutputStream();
			writer = new PrintWriter(new OutputStreamWriter(stream, UTF_8_CHARACTER_SET));
		}

		return writer;
	}

	@Override
	public void setContentLength(int length)
	{
	}
}
