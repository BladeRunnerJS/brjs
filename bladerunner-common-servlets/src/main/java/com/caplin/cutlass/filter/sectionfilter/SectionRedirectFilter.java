package com.caplin.cutlass.filter.sectionfilter;

import java.io.File;
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

public class SectionRedirectFilter implements Filter
{
	private SectionRedirectHandler handler;
	private ServletContext servletContext;
	private Logger logger;
	private BRJS brjs;
	private File contextDir;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		brjs = ServletModelAccessor.initializeModel(filterConfig.getServletContext());
		servletContext = filterConfig.getServletContext();
		contextDir = new File(servletContext.getRealPath("/"));
		logger = brjs.logger(LoggerType.FILTER, SectionRedirectFilter.class);
	}
	
	@Override
	public void destroy()
	{
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		try
		{
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			BladerunnerUri bladerunnerUri = new BladerunnerUri(brjs, servletContext, httpRequest);
			String requestPath = bladerunnerUri.getInternalPath();
			
			if (!pathRequestEndsWithSlashOrLastPathPartContainsDot(requestPath) && !requestPath.startsWith(CutlassConfig.SERVLET_PATH_PREFIX))
			{
				logger.debug("requestPath '" + requestPath + "' doesn't end with a '/' -- redirecting");
				
				bladerunnerUri.logicalPath += "/";
				httpResponse.sendRedirect(bladerunnerUri.getUri());
			}
			else 
			{
				String redirectUrl = getSectionRedirectHandler().getRedirectUrl(requestPath);
				
				if (!requestPath.equals(redirectUrl))
				{
					logger.debug("requestPath '" + requestPath + "' doesn't match redirect url '" + redirectUrl + "' -- forwarding");
					
					servletContext.getRequestDispatcher(redirectUrl).forward(httpRequest, httpResponse);
				}
				else
				{
					logger.debug("requestPath '" + requestPath + "' not being redirected by section redirect filter");
					
					chain.doFilter(httpRequest, httpResponse);
				}		
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
	
	private SectionRedirectHandler getSectionRedirectHandler()
	{
		// TODO: confirm that the only reason this weird bit of code actually exists is because the SectionRedirectHandler
		// determines the available list of aspects within it's constructor, which would prevent new aspects being added in dev
		if(isDevMode()) {
			SectionRedirectHandler threadLocalHandler = new SectionRedirectHandler(brjs, contextDir);
			return threadLocalHandler;
		}
		else if(handler == null){
			handler = new SectionRedirectHandler(brjs, contextDir);
		}
		return handler;
	}
	
	private boolean isDevMode()
	{
		if(servletContext.getAttribute(CutlassConfig.DEV_MODE_FLAG) != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean pathRequestEndsWithSlashOrLastPathPartContainsDot(String path)
	{
		if (path.endsWith("/")) {
			return true;
		}
		String[] pathSplit = path.split("/");
		String lastPathPart = pathSplit[pathSplit.length-1];
		if (lastPathPart.contains("."))
		{
			// this is a file request - return true
			return true;
		}
		return false;
	}
}