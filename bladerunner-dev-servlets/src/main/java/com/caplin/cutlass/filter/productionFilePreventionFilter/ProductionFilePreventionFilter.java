package com.caplin.cutlass.filter.productionFilePreventionFilter;

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

import org.apache.commons.lang3.ArrayUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.ServletModelAccessor;

public class ProductionFilePreventionFilter implements Filter
{
	private static final String FILTER_BYPASS_FOLDER = "unbundled-resources";
	private static final String[] ACCEPTABLE_FILENAMES = new String[] { "index.html", ".*\\.bundle" };
	
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
			ResponseCachingWrapper cachedResponse = getCacheOfAllOtherFilterResponses(request, response, chain);
			BladerunnerUri bladerunnerUri = new BladerunnerUri(brjs, servletContext, (HttpServletRequest) request);
			String fileRequest = bladerunnerUri.logicalPath;
			
			if (!isAcceptableRequest(fileRequest))
			{
				send404Error(response, fileRequest);
			}
			else
			{
				cachedResponse.writeCacheToResponse(response);
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

	private ResponseCachingWrapper getCacheOfAllOtherFilterResponses(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		ResponseCachingWrapper cachedResponse = new ResponseCachingWrapper(httpResponse);
		chain.doFilter(request, cachedResponse);
		return cachedResponse;
	}
	
	public boolean isAcceptableRequest(String requestPath)
	{
		if(requestPath.endsWith("/"))
		{
			return true;
		}

		String[] folderAndFileNames = requestPath.split("/");

		if (folderAndFileNames.length > 0)
		{
			String fileName = folderAndFileNames[folderAndFileNames.length - 1];
			if (isAcceptableFilename(fileName))
			{
				return true;
			}
			if (ArrayUtils.contains(folderAndFileNames, FILTER_BYPASS_FOLDER))
			{
				return true;
			}
		}
		
		if (requestPath.startsWith(CutlassConfig.SERVLET_PATH_PREFIX))
		{
			return true;
		}
		
		return false;
	}
	
	private void send404Error(ServletResponse response, String requestUrl) throws IOException
	{
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.sendError(404, "Access to the URL '" + requestUrl + "' denied. This URL will not be available in production.");
	}

	private boolean isAcceptableFilename(String fileName)
	{
		for (String fileNameRegex : ACCEPTABLE_FILENAMES)
		{
			if (fileName.matches(fileNameRegex))
			{
				return true;
			}
		}		
		return false;
	}
}
