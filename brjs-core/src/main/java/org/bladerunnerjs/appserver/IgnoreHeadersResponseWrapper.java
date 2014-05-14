package org.bladerunnerjs.appserver;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class IgnoreHeadersResponseWrapper extends HttpServletResponseWrapper
{
	private List<String> ignoredHeaders = new ArrayList<String>();
	
	public IgnoreHeadersResponseWrapper(HttpServletResponse response, List<String> ignoredHeaders)
	{
		super(response);
		for (String header : ignoredHeaders)
		{
			this.ignoredHeaders.add(header.toLowerCase());		
		}
	}
	
	@Override
	public void setDateHeader(String name, long date)
	{
		if (!ignoredHeaders.contains(name.toLowerCase()))
		{
			super.setDateHeader(name, date);
		}
	}
	
	@Override
	public void setHeader(String name, String value)
	{
		if (!ignoredHeaders.contains(name.toLowerCase()))
		{
			super.setHeader(name, value);
		}
	}
	
	@Override
	public void addHeader(java.lang.String name, java.lang.String value)
	{
		if (!ignoredHeaders.contains(name.toLowerCase()))
		{
			super.addHeader(name, value);
		}
	}
	
	@Override
	public void setIntHeader(java.lang.String name, int value)
	{
		if (!ignoredHeaders.contains(name.toLowerCase()))
		{
			super.setIntHeader(name, value);
		}
	}
	
	@Override
	public void addIntHeader(java.lang.String name, int value)
	{
		if (!ignoredHeaders.contains(name.toLowerCase()))
		{
			super.addIntHeader(name, value);
		}
	}
	
	public void forceSetHeader(String name, String value)
	{
		super.setHeader(name, value);
	}
	
	@Override
	public void flushBuffer()
	{
	}
}
