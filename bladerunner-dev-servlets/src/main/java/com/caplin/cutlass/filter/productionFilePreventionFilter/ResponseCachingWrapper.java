package com.caplin.cutlass.filter.productionFilePreventionFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ResponseCachingWrapper extends HttpServletResponseWrapper {
	
	private ByteArrayOutputStream cache;
	private PrintWriter writer;
	private ServletOutputStream stream;

	public ResponseCachingWrapper(HttpServletResponse response)
	{
		super(response);
		cache = new ByteArrayOutputStream();
	}
	
	@Override
	public ServletOutputStream getOutputStream() throws IOException
	{
		if (stream == null)
		{
			stream = new ServletOutputStream()
			{
				@Override
				public void write(int i) throws IOException
				{
					cache.write(i);
				}
				
			};
		}
		return stream;
	}

	@Override
	public PrintWriter getWriter() throws IOException
	{
		if (writer == null)
		{
			writer = new PrintWriter(cache);
		}
		return writer;
	}

	@Override()
	public void setContentLength(int len)
	{
		// setContentLength() is irreversible and will commit the response.
	}

	public void writeCacheToResponse(ServletResponse response) throws IOException
	{
		ServletOutputStream out = response.getOutputStream();
		out.write(cache.toByteArray());
		out.close();
	}
}
