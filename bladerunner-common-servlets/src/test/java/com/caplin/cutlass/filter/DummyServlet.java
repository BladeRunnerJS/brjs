package com.caplin.cutlass.filter;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caplin.cutlass.bundler.exception.UnknownBundlerException;

@SuppressWarnings("serial")
public class DummyServlet extends HttpServlet
{
	private String responseText;
	private int responseCode;
	private String contentType;

	public DummyServlet()
	{
		resetResponse();
	}

	public void setResponseText(String responseText)
	{
		this.responseText = responseText;
	}

	public void setResponseCode(int responseCode)
	{
		this.responseCode = responseCode;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public void resetResponse()
	{
		responseText = "OK";
		responseCode = 200;
		contentType = "text/plain";
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
	{
		Writer out;
		try
		{
			response.setStatus(responseCode);
			response.setContentType(contentType);
			out = response.getWriter();
			out.write(responseText);
			out.flush();
		}
		catch (IOException ex)
		{
			throw new UnknownBundlerException(ex);
		}
	}

}