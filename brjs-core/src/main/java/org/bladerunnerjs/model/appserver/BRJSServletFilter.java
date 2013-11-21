package org.bladerunnerjs.model.appserver;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BladerunnerUri;


public class BRJSServletFilter implements Filter
{

	private FilterConfig filterConfig;
	BRJSServletUtils servletUtils;
	
	//TODO: dont pass the app in - filters/servlets in web.xml must have default constructors
	public BRJSServletFilter(App app)
	{
		servletUtils = new BRJSServletUtils(app);
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		this.filterConfig = filterConfig;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		if ( !(request instanceof HttpServletRequest))
		{
			throw new ServletException(this.getClass().getSimpleName()+" can only handle HTTP requests.");
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;		
		
		BladerunnerUri bladerunnerUri = servletUtils.createBladeRunnerUri(filterConfig.getServletContext(), httpRequest);
		boolean brjsPluginCanHandleRequest = servletUtils.getContentPluginForRequest(bladerunnerUri) != null;
		
		if (brjsPluginCanHandleRequest && !BladerunnerUri.isBrjsUriRequest(httpRequest))
		{
			filterConfig.getServletContext().getRequestDispatcher("/brjs"+httpRequest.getRequestURI()).forward(httpRequest, response);
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
