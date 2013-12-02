package org.bladerunnerjs.model.appserver;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.core.plugin.content.ContentPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;

public class BRJSServletUtils
{
	private BRJS brjs;
	
	public BRJSServletUtils(BRJS brjs)
	{
		this.brjs = brjs;
	}
	
	public ContentPlugin getContentPluginForRequest(BladerunnerUri bladerunnerUri) {
		ContentPlugin potentialContentPlugin = brjs.plugins().contentProvider(bladerunnerUri);
		ContentPlugin contentPlugin = null;
		
		if((potentialContentPlugin != null) && potentialContentPlugin.getContentPathParser().canParseRequest(bladerunnerUri)) {
			contentPlugin = potentialContentPlugin;
		}
		
		return contentPlugin;
	}
	
	public boolean passRequestToApropriateContentPlugin(ServletContext context, HttpServletRequest req, HttpServletResponse resp) throws ServletException
	{
		BladerunnerUri bladerunnerUri = createBladeRunnerUri(context, req);
		
		ContentPlugin contentPlugin = getContentPluginForRequest(bladerunnerUri);
		if (contentPlugin != null)
		{
			ContentPathParser requestParser = contentPlugin.getContentPathParser();
			handleRequestUsingContentPlugin(bladerunnerUri, parse(requestParser, bladerunnerUri), contentPlugin, resp);
			return true;
		}
		return false;
	}

	private ParsedContentPath parse(ContentPathParser requestParser, BladerunnerUri bladerunnerUri) throws ServletException
	{
		try
		{
			return requestParser.parse(bladerunnerUri);
		}
		catch (MalformedRequestException ex)
		{
			throw new ServletException(ex);
		}
	}
	
	private void handleRequestUsingContentPlugin(BladerunnerUri requestUri, ParsedContentPath parsedRequest, ContentPlugin contentPlugin, HttpServletResponse resp) throws ServletException
	{
		try
		{
			BundlableNode bundlableNode = getBundableNodeForRequest(requestUri, resp);
			contentPlugin.writeContent(parsedRequest, bundlableNode.getBundleSet(), resp.getOutputStream());
		}
		catch (MalformedRequestException ex)
		{
			sendErrorResponse(resp, 404, ex);
		}
		catch (BundlerProcessingException ex)
		{
			sendErrorResponse(resp, 500, ex);
		}
		catch (IOException ex)
		{
			sendErrorResponse(resp, 500, ex);
		}
		catch (ModelOperationException ex)
		{
			sendErrorResponse(resp, 500, ex);
		}
	}

	public App getAppForRequest(BladerunnerUri requestUri, HttpServletResponse resp) throws ServletException, MalformedRequestException
	{
		String appName = StringUtils.substringAfter(requestUri.contextPath, "/");
		if (appName.endsWith("/"))
		{
			appName = StringUtils.substringBeforeLast(appName, "/");
		}
		App app = brjs.app(appName);
		
		
		if (!app.dirExists())
		{
			app = brjs.systemApp(appName);
			if (!app.dirExists())
			{
				throw new MalformedRequestException(requestUri.getUri(), "App '"+app.getName()+"' not found.");
			}
		}
	
		return app;
	}
	
	public BundlableNode getBundableNodeForRequest(BladerunnerUri requestUri, HttpServletResponse resp) throws ServletException, MalformedRequestException
	{
		App app = getAppForRequest(requestUri, resp);
		File baseDir = app.file(requestUri.scopePath);
		BundlableNode bundlableNode = app.root().locateFirstBundlableAncestorNode(baseDir);
		return bundlableNode;
	}

	public BladerunnerUri createBladeRunnerUri(ServletContext context, HttpServletRequest req) throws ServletException
	{
		try
		{
			return new BladerunnerUri(brjs, context, req);
		}
		catch (MalformedRequestException ex)
		{
			throw new ServletException(ex);
		}
	}
	
	public void sendErrorResponse(HttpServletResponse response, int code, Exception exception) throws ServletException
	{
		sendErrorResponse(response, code, exception.toString());
	}
	
	private void sendErrorResponse(HttpServletResponse response, int code, String message) throws ServletException
	{
		try {
			if (!response.isCommitted())
			{
				response.sendError(code, message);
			}
		}
		catch (IOException ex)
		{
			throw new ServletException(ex);
		}
	}
}