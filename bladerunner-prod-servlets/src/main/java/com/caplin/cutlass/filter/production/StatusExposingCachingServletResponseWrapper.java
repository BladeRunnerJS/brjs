package com.caplin.cutlass.filter.production;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class StatusExposingCachingServletResponseWrapper extends HttpServletResponseWrapper {

	private ByteArrayOutputStream cache;
	private PrintWriter writer;
	private ServletOutputStream stream;
	private HttpServletResponse response;
	private int httpStatus;
	private int contentLength;

	public StatusExposingCachingServletResponseWrapper(HttpServletResponse response)
	{
		super(response);
		this.response = response;
		cache = new ByteArrayOutputStream();
		httpStatus = 200;
	}

	@Override
	public void sendError(int sc) throws IOException
	{
		httpStatus = sc;
	}

	@Override
	public void sendError(int sc, String msg) throws IOException
	{
		httpStatus = sc;
	}

	@Override
	public void setStatus(int sc)
	{
		httpStatus = sc;
		super.setStatus(sc);
	}
	
	@Override
	public void setStatus(int status, String string)
	{
		super.setStatus(status, string);
		httpStatus = status;
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
		contentLength = len;
	}

	public int getStatus() 
	{
		return httpStatus;
	}
	
	public int getContentLength()
	{
		return contentLength;
	}
	
	public void writeCacheToResponse() throws IOException
	{
		response.setStatus(httpStatus);
		response.setContentLength(contentLength);
		ServletOutputStream out = response.getOutputStream();
		out.write(cache.toByteArray());
		out.close();
	}
	
	@Override
	public void flushBuffer()
	{
	}

}

