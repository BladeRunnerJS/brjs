package com.caplin.cutlass.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BladerunnerFilter
{
	public void init()
	{
		// do nothing
	}
	
	public void destroy()
	{
		// do nothing
	}
	
	public boolean doFilterRequest(String requestPath, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		return true;
	}
	
	public boolean doFilterResponseHeaders(String requestPath, HttpServletRequest request, HttpServletResponse response, ResponseType responseType) throws Exception
	{
		return true;
	}
	
	public boolean willProcessResponseBodyCharacterData(String requestPath, HttpServletRequest request, HttpServletResponse response)
	{
		return false;
	}
	
	public boolean willProcessResponseBodyBinaryData(String requestPath, HttpServletRequest request, HttpServletResponse response)
	{
		return false;
	}
	
	public void doFilterResponseBodyCharacterData(HttpServletRequest request, String inputCharacters, ResponseWriter outputStream) throws Exception
	{
		// do nothing
	}
	
	// TODO
	public void doFilterResponseBodyBinaryData() throws Exception
	{
		// do nothing
	}
}
