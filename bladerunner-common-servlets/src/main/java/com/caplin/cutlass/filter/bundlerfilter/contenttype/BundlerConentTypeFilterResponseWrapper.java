package com.caplin.cutlass.filter.bundlerfilter.contenttype;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class BundlerConentTypeFilterResponseWrapper extends HttpServletResponseWrapper
{
	private int contentLength;

	public BundlerConentTypeFilterResponseWrapper(HttpServletResponse response)
	{
		super(response);
	}

	@Override()
	public void setContentLength(int len)
	{
		contentLength = len;
	}
	
	public int getContentLength()
	{
		return contentLength;
	}
	
	@Override
	public void flushBuffer()
	{
	}
	
}
