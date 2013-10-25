package com.caplin.cutlass.util;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UrlEchoServlet extends HttpServlet
{

	private static final long serialVersionUID = 4074453656811606334L;

	public UrlEchoServlet()
	{
	}
	
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
	{
		Writer out;
		try
		{
			String queryStringSuffix = (request.getQueryString() == null) ? "" : "?" + request.getQueryString();
			out = response.getWriter();
			out.write(request.getRequestURI() + queryStringSuffix);
			out.flush();
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

}