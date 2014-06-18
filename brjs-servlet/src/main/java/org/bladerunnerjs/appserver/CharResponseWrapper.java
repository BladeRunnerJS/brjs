package org.bladerunnerjs.appserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CharResponseWrapper extends HttpServletResponseWrapper
{
	private ByteArrayOutputStream byteArrayOutputStream;
	private ServletOutputStream servletOutputStream;
	private PrintWriter printWriter;
	
	public CharResponseWrapper(HttpServletResponse response) throws UnsupportedEncodingException
	{
		super(response);
		
		byteArrayOutputStream = new ByteArrayOutputStream();
		servletOutputStream = new ServletOutputStream()
		{
			@Override
			public void write(int i) throws IOException
			{
				byteArrayOutputStream.write(i);
			}
		};
		printWriter = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream, "UTF-8"));
	}
	
	@Override
	public ServletOutputStream getOutputStream()
	{
		return servletOutputStream;
	}
	
	@Override
	public PrintWriter getWriter()
	{
		return printWriter;
	}
	
	public Reader getReader() throws UnsupportedEncodingException
	{
		printWriter.flush();
		return new InputStreamReader(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), "UTF-8");
	}
}
