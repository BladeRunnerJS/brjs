package com.caplin.cutlass.filter.thirdpartyfilter;

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

import com.caplin.cutlass.ServletModelAccessor;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;

import com.caplin.cutlass.CutlassConfig;

public class ThirdPartyResourceFilter implements Filter
{	
	private ServletContext servletContext;
	private BRJS brjs;
	private Logger logger;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		brjs = ServletModelAccessor.initializeAndGetModel(filterConfig.getServletContext());
		servletContext = filterConfig.getServletContext();
		logger = brjs.logger(LoggerType.FILTER, ThirdPartyResourceFilter.class);
	}
	
	@Override
	public void destroy() 
	{
		ServletModelAccessor.destroy();
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException
	{
		try
		{
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			BladerunnerUri bladerunnerUri = new BladerunnerUri(brjs, servletContext, httpRequest);
			String requestPath = bladerunnerUri.logicalPath;
			
			if(requestPath.matches("^thirdparty-libraries/[^/]+/.+") && !requestPath.contains(CutlassConfig.THIRDPARTY_BUNDLE_SUFFIX))
			{
				bladerunnerUri.logicalPath += CutlassConfig.THIRDPARTY_BUNDLE_SUFFIX;
				String redirectUrl = bladerunnerUri.getInternalPath();
				
				logger.debug("requestPath '" + bladerunnerUri.getInternalPath() + "' being forwarded to '" + redirectUrl + "'");
				
				httpRequest.getSession().getServletContext().getRequestDispatcher(redirectUrl).forward(httpRequest, httpResponse);
			}
			else
			{
				logger.debug("requestPath '" + bladerunnerUri.getInternalPath() + "' not being redirected by thirdparty resource filter");
				
				chain.doFilter(httpRequest, httpResponse);
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
