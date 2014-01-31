package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.utility.RelativePathUtility;


public class BladerunnerUri
{
	public static final String BRJS_URL_PREFIX = "/brjs";
	private static final Pattern pathPattern = Pattern.compile(";[^;]+$");
	private final BRJS brjs;

	/**
	 * The path for the webapp context.
	 * Will always start and end with a <tt>/</tt>.
	 * e.g. <ul>
	 *    <li>/myapp/</li>
	 * </ul>
	 */
	public String contextPath;

	/**
	 * The url path parameter.
	 * Will be either the empty string or start with a <tt>;</tt>.
	 */
	public String pathParameter;

	/**
	 * The url query string.
	 * Will be either the empty string or start with a <tt>?</tt>.
	 * e.g. <ul>
	 *    <li>?major=boris&cameron=worried</li>
	 * </ul>
	 */
	public String queryString;

	/**
	 * The path to a bundle scope (i.e. a workbench, aspect or test).
	 * Will be either the empty string or start and end with a <tt>/</tt>.
	 * e.g. <ul>
	 *    <li>/fx-bladeset/blades/ticket/workbench/</li>
	 *    <li>/mobile-aspect/</li>
	 * </ul>
	 */
	public String scopePath;

	/**
	 * The path to the resource within the scope.  May or may not be a bundle.
	 * Will not start with a <tt>/</tt>.
	 * e.g. <ul>
	 *    <li>js/js.bundle</li>
	 *    <li>index.html</li>
	 * </ul>
	 */
	public String logicalPath;

	public BladerunnerUri(BRJS brjs, ServletContext context, HttpServletRequest request) throws MalformedRequestException
	{
		this.brjs = brjs;
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String servletPath = request.getServletPath();
		String requestPath = "";
		
		if ((servletPath != null) && servletPath.equals(BRJS_URL_PREFIX))
		{
			requestPath = StringUtils.substringAfter(requestUri, contextPath+BRJS_URL_PREFIX);
		}
		else
		{
			requestPath = StringUtils.substringAfter(requestUri, contextPath);
		}
		
		processUri(new File(context.getRealPath("/")), contextPath, requestPath, request.getQueryString());
	}
	
	public BladerunnerUri(BRJS brjs, File contextDir, String requestPath) throws MalformedRequestException
	{
		this.brjs = brjs;
		
		App app = brjs.locateAncestorNodeOfClass(contextDir, App.class);
		
		if (app == null)
		{
			throw new MalformedRequestException(contextDir.getAbsolutePath(), "Unable to calculate App node for the contextDir dir: " + contextDir.getAbsolutePath());
		}
		
		String fullRequestPath = "/"+RelativePathUtility.get(app.dir(), contextDir);
		fullRequestPath = (fullRequestPath + requestPath).replace("//", "/");
		
		processUri(app.dir(), "/"+app.getName(), fullRequestPath, null);
	}
	
	
	/**
	 * This is method should only be used for testing. DO NOT use this in plugin code, use a different constructor instead.
	 * 
	 * @Deprecated - marked as deprecated to trigger a compiler warning if it's used
	 */
	@Deprecated
	public BladerunnerUri(BRJS brjs, File contextRoot, String contextPath, String requestPath, String queryString) throws MalformedRequestException
	{
		this.brjs = brjs;
		processUri(contextRoot, contextPath, requestPath, queryString);
	}

	public String getUri()
	{
		String queryStringSuffix = (queryString == null) ? "" : "?" + queryString;
		
		return contextPath + scopePath + logicalPath + pathParameter + queryStringSuffix;
	}
	
	public String getInternalPath()
	{
		return scopePath + logicalPath;
	}
	
	private void processUri(File contextRoot, String contextPath, String requestPath, String queryString) throws MalformedRequestException
	{		
		if (!contextRoot.exists() || !contextRoot.isDirectory())
		{
			throw new MalformedRequestException(contextPath + requestPath, "Error calculating root directory. Calculated root path " + contextRoot.getPath() + " either does not exist or is not a directory.");
		}
		
		this.contextPath = contextPath;
		pathParameter = getPathParameter(requestPath);
		requestPath = requestPath.substring(0, requestPath.length() - pathParameter.length());
		this.queryString = queryString;
		
		File nominalDir = new File(contextRoot, requestPath);
		Node requestContextNode = locateRequestContextNode(nominalDir);
		
		if(requestContextNode == null)
		{
			// This happens only if the request is not in a real SDK.
			// e.g. for our tests.

			if(requestPath.length() > 0 && requestPath.charAt(0) == '/')
			{
				scopePath = "/";
				logicalPath = requestPath.substring(1);
			}
			else
			{
				scopePath = "";
				logicalPath = requestPath;
			}
		}
		else
		{
			scopePath = calculateScopePath(requestContextNode.dir(), contextRoot);
			logicalPath = StringUtils.substringAfter(requestPath, scopePath);
		}
	}
	
	private String calculateScopePath(File requestContextDir, File contextRoot)
	{
		if (requestContextDir.getAbsolutePath().contains(contextRoot.getAbsolutePath()))
		{
			return StringUtils.substringAfter(requestContextDir.getAbsolutePath(), contextRoot.getAbsolutePath()).replace("\\", "/") + "/";			
		}
		try
		{
			return StringUtils.substringAfter(requestContextDir.getCanonicalPath(), contextRoot.getCanonicalPath()).replace("\\", "/") + "/";
		}
		catch (IOException ex)
		{
			throw new RuntimeException("Unable to calculate scope path for request using canonical paths", ex);			
		}
	}

	private String getPathParameter(String requestUri)
	{
		Matcher pathMatcher = pathPattern.matcher(requestUri);
		return (pathMatcher.find()) ? pathMatcher.group(0) : "";
	}
	
	private Node locateRequestContextNode(File file)
	{
		Node node = brjs.locateFirstAncestorNode(file);
		Node bundlableNode = null;
		
		while((node != null) && (bundlableNode == null))
		{
			if (node instanceof BundlableNode || node instanceof Blade || node instanceof Bladeset)
			{
				bundlableNode = node;
			}
			else
			{
				node = node.parentNode();
			}
		}
		
		return bundlableNode;
	}

	public static boolean isBrjsUriRequest(HttpServletRequest request)
	{
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();
		return requestUri.startsWith(contextPath+BRJS_URL_PREFIX+"/");
	}
}
