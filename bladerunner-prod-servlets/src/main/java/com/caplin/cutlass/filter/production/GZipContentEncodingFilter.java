package com.caplin.cutlass.filter.production;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;

import com.caplin.cutlass.ServletModelAccessor;

public class GZipContentEncodingFilter implements Filter
{
	private ServletContext servletContext;
	private BRJS brjs;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		brjs = ServletModelAccessor.initializeModel(filterConfig.getServletContext());
		servletContext = filterConfig.getServletContext();
	}
	
	@Override
	public void destroy()
	{
		ServletModelAccessor.destroy();
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		try
		{
			BladerunnerUri bladerunnerUri = new BladerunnerUri(brjs, servletContext, (HttpServletRequest) request);
			String requestURI = bladerunnerUri.logicalPath;
			
			if(requestURI.endsWith(".bundle") && !requestURI.endsWith("image.bundle"))
			{
				StatusExposingCachingServletResponseWrapper responseWrapper = new StatusExposingCachingServletResponseWrapper((HttpServletResponse) response);
				chain.doFilter(request, responseWrapper);
				if(responseWrapper.getStatus() == 200 && responseWrapper.getContentLength() != 0)
				{
					((HttpServletResponse) response).setHeader("Content-Encoding" , "gzip");
				}
				responseWrapper.writeCacheToResponse();			
			}
			else
			{
				chain.doFilter(request, response);
			}
		}
		catch(IOException|ServletException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}
	}
}
