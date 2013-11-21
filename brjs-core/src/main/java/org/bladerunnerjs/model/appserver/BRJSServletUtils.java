package org.bladerunnerjs.model.appserver;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bladerunnerjs.core.plugin.servlet.ContentPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;

public class BRJSServletUtils
{

	public BRJS brjs;
	public App app;

	public BRJSServletUtils(App app)
	{
		this.app = app;
		brjs = app.root();
	}

	
	ContentPlugin getContentPluginForRequest(BladerunnerUri bladerunnerUri)
	{
		for (ContentPlugin contentPlugin : brjs.allContentPlugins())
		{
			RequestParser requestParser = contentPlugin.getRequestParser();
			if ( requestParser.canParseRequest(bladerunnerUri) )
			{
				return contentPlugin;
			}
		}
		return null;
	}
	
	
	boolean passRequestToApropriateContentPlugin(ServletContext context, HttpServletRequest req, HttpServletResponse resp) throws ServletException
	{
		BladerunnerUri bladerunnerUri = createBladeRunnerUri(context, req);
		
		ContentPlugin contentPlugin = getContentPluginForRequest(bladerunnerUri);
		if (contentPlugin != null)
		{
			RequestParser requestParser = contentPlugin.getRequestParser();
			handleRequestUsingContentPlugin(bladerunnerUri, parse(requestParser, bladerunnerUri), contentPlugin, resp);
			return true;
		}
		return false;
	}

	ParsedRequest parse(RequestParser requestParser, BladerunnerUri bladerunnerUri) throws ServletException
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
	
	void handleRequestUsingContentPlugin(BladerunnerUri requestUri, ParsedRequest parsedRequest, ContentPlugin contentPlugin, HttpServletResponse resp) throws ServletException
	{
		try
		{
			File baseDir = new File(app.dir(), requestUri.scopePath);
			BundlableNode bundlableNode = app.root().locateFirstBundlableAncestorNode(baseDir);
			contentPlugin.writeContent(parsedRequest, bundlableNode.getBundleSet(), resp.getOutputStream());
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

	BladerunnerUri createBladeRunnerUri(ServletContext context, HttpServletRequest req) throws ServletException
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
	
	
	
	void sendErrorResponse(HttpServletResponse response, int code, Exception exception) throws ServletException
	{
		try {
			response.sendError(code, exception.toString());
		}
		catch (IOException ex)
		{
			throw new ServletException(ex);
		}
	}
	
}