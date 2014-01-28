package com.caplin.cutlass.filter.versionfilter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.ServletModelAccessor;

public class VersionRedirectFilter implements Filter
{
	private static final String VARY = "Vary";
	private static final String E_TAG = "ETag";
	private static final String EXPIRES = "Expires";
	private static final String CACHE_CONTROL = "Cache-Control";
	private static final String LAST_MODIFIED = "Last-Modified";
	
	private static final String IS_VERSION_REDIRECT = "is-version-redirect";
	private static final String VARY_HEADER_PAGE = "/index.html";
	private static final List<String> DO_NOT_CACHE_EXTENSIONS = Arrays.asList("jsp");

	public static final long maxAge = TimeUnit.DAYS.toSeconds(365);
	static final String headerDateFormat = "dd MMM yyyy kk:mm:ss z";
	
	private static final String cacheControl_allowCache = "max-age=" + maxAge + ", public, must-revalidate";
	private static final String cacheControl_noCache = "no-cache, must-revalidate";	
	
	private ServletContext servletContext;
	private BRJS brjs;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		brjs = ServletModelAccessor.initializeAndGetModel(filterConfig.getServletContext());
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
		if (!(request instanceof HttpServletRequest))
		{
			throw new ServletException("Can only process HttpServletRequest");
		}
		if (!(response instanceof HttpServletResponse))
		{
			throw new ServletException("Can only process HttpServletResponse");
		}
		
		try
		{
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			BladerunnerUri bladerunnerUri = new BladerunnerUri(brjs, servletContext, httpRequest);
			String versionedLogicalPath = new VersionRedirector().getRedirectedUrl(bladerunnerUri.logicalPath);
			
			if (versionedLogicalPath != null && !bladerunnerUri.logicalPath.equals(versionedLogicalPath))
			{
				httpRequest.setAttribute(IS_VERSION_REDIRECT, true);
				bladerunnerUri.logicalPath = versionedLogicalPath;
				servletContext.getRequestDispatcher(bladerunnerUri.getInternalPath()).forward(httpRequest, httpResponse);
			}
			else
			{
				if (httpRequest.getAttribute(IS_VERSION_REDIRECT) != null)
				{
					List<String> ignoredHeaders = Arrays.asList(LAST_MODIFIED, CACHE_CONTROL, EXPIRES, E_TAG);
					IgnoreHeadersResponseWrapper responseWrapper = new IgnoreHeadersResponseWrapper(httpResponse, ignoredHeaders);
					responseWrapper.forceSetHeader(LAST_MODIFIED, "");
					if (setPageToCache(bladerunnerUri.getInternalPath())) 
					{
						responseWrapper.forceSetHeader(CACHE_CONTROL, cacheControl_allowCache);
						responseWrapper.forceSetHeader(EXPIRES, getExpiresHeader());
					} else {
						responseWrapper.forceSetHeader(CACHE_CONTROL, cacheControl_noCache);
						responseWrapper.forceSetHeader(EXPIRES, "");
					}
					responseWrapper.forceSetHeader(E_TAG, "");
					chain.doFilter(request, responseWrapper);
				}
				else
				{
					List<String> ignoredHeaders = Arrays.asList(LAST_MODIFIED, E_TAG);
					IgnoreHeadersResponseWrapper responseWrapper = new IgnoreHeadersResponseWrapper(httpResponse, ignoredHeaders);
					if (addVaryHeader(bladerunnerUri.getInternalPath()))
					{
						responseWrapper.setHeader(VARY, "Cookie");
					}
					responseWrapper.forceSetHeader(E_TAG, "");
					chain.doFilter(request, responseWrapper);
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

	private boolean addVaryHeader(String requestPath)
	{
		return requestPath.endsWith("/") || requestPath.equals(VARY_HEADER_PAGE);
	}
	
	private String getExpiresHeader()
	{
		Date expdate = new Date();
		expdate.setTime(expdate.getTime() + TimeUnit.SECONDS.toMillis(maxAge));
		DateFormat df = new SimpleDateFormat(headerDateFormat);
		return df.format(expdate);
	}
	
	private boolean setPageToCache(String request)
	{
		boolean shouldCachePage = true;
		String extension = StringUtils.substringAfterLast(request, ".");
		if (DO_NOT_CACHE_EXTENSIONS.contains(extension)) 
		{
			shouldCachePage = false;
		}
		if (request.startsWith(CutlassConfig.SERVLET_PATH_PREFIX))
		{
			shouldCachePage = false;
		}
		return shouldCachePage;
	}

}