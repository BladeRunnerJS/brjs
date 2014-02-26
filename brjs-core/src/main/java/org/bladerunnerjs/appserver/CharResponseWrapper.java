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
	private String characterEncoding = "UTF-8";
	
	public CharResponseWrapper(HttpServletResponse response)
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
	}
	
	@Override
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
		super.setCharacterEncoding(characterEncoding);
	}
	
	@Override
	public void setContentType(String contentType) {
		super.setContentType(contentType);
		
		if(contentType.contains(";charset=")) {
			this.characterEncoding = getCharacterEncoding();
		}
	}
	
	@Override
	public void setHeader(String headerName, String headerValue) {
		super.setHeader(headerName, headerValue);
		
		if((headerName.equals("Content-Type")) && headerValue.contains(";charset=")) {
			this.characterEncoding = getCharacterEncoding();
		}
	}
	
	@Override
	public ServletOutputStream getOutputStream()
	{
		return servletOutputStream;
	}
	
	@Override
	public PrintWriter getWriter() throws UnsupportedEncodingException
	{
		if(printWriter == null) {
			printWriter = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream, characterEncoding));
		}
		
		return printWriter;
	}
	
	public Reader getReader() throws UnsupportedEncodingException
	{
		if(printWriter != null) {
			printWriter.flush();
		}
		
		return new InputStreamReader(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), characterEncoding);
	}
	
	@Override
	public void flushBuffer()
	{
	}
}
