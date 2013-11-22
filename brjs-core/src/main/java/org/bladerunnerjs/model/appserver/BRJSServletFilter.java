package org.bladerunnerjs.model.appserver;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;


public class BRJSServletFilter implements Filter
{

	private ServletContext servletContext;
	BRJSServletUtils servletUtils;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		servletContext = filterConfig.getServletContext();
		BRJS brjs = ServletModelAccessor.initializeModel(servletContext);
		servletUtils = new BRJSServletUtils(brjs);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		if ( !(request instanceof HttpServletRequest))
		{
			throw new ServletException(this.getClass().getSimpleName()+" can only handle HTTP requests.");
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;		
		
		BladerunnerUri bladerunnerUri = servletUtils.createBladeRunnerUri(servletContext, httpRequest);
		boolean brjsPluginCanHandleRequest = servletUtils.getContentPluginForRequest(bladerunnerUri) != null;
		
		if (brjsPluginCanHandleRequest && !BladerunnerUri.isBrjsUriRequest(httpRequest))
		{
			request.getRequestDispatcher("/brjs"+httpRequest.getRequestURI()).forward(httpRequest, response);
		}
		else
		{
			chain.doFilter(httpRequest, response);
		}
	}

	@Override
	public void destroy()
	{
	}

}
