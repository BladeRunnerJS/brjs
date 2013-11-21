package org.bladerunnerjs.model.appserver;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import org.eclipse.jetty.servlet.DefaultServlet;


public class BRJSServlet extends DefaultServlet
{
	private static final long serialVersionUID = 1964608537461568895L;

	private static final Pattern DASHBOARD_REDIRECT_REGEX = Pattern.compile("/");
	private static final Pattern VERSION_REGEX = Pattern.compile("/brjs/version/?");
	
	private BRJS brjs;
	private App app;
	
	public BRJSServlet(App app)
	{
		this.app = app;
		brjs = app.root();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String requestPath = req.getRequestURI();
		String pathRelativeToApp = StringUtils.substringAfter(requestPath, req.getContextPath());
		
		if (matchesRegex(pathRelativeToApp, DASHBOARD_REDIRECT_REGEX))
		{
			resp.sendRedirect("/dashboard/");
		}
		else if (matchesRegex(pathRelativeToApp, VERSION_REGEX))
		{
			resp.getWriter().print(brjs.versionInfo().getVersionNumber());
		}
		else
		{
			boolean foundHandler = passRequestToApropriateContentPlugin(req, resp);
			if (!foundHandler)
			{
				super.doGet(req, resp);
			}
		}
	}

	//TODO: this logic should be moved into the logical request handler
	private boolean passRequestToApropriateContentPlugin(HttpServletRequest req, HttpServletResponse resp) throws ServletException
	{
		BladerunnerUri bladerunnerUri = createBladeRunnerUri(req);
		
		for (ContentPlugin contentPlugin : brjs.allContentPlugins())
		{
			RequestParser requestParser = contentPlugin.getRequestParser();
			if ( requestParser.canParseRequest(bladerunnerUri) )
			{
				handleRequestUsingContentPlugin(bladerunnerUri, parse(requestParser, bladerunnerUri), contentPlugin, resp);
				return true;
			}
		}
		return false;
	}
	
	
	
	private ParsedRequest parse(RequestParser requestParser, BladerunnerUri bladerunnerUri) throws ServletException
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
	
	private void handleRequestUsingContentPlugin(BladerunnerUri requestUri, ParsedRequest parsedRequest, ContentPlugin contentPlugin, HttpServletResponse resp) throws ServletException
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

	private BladerunnerUri createBladeRunnerUri(HttpServletRequest req) throws ServletException
	{
		try
		{
			return new BladerunnerUri(brjs, getServletContext(), req);
		}
		catch (MalformedRequestException ex)
		{
			throw new ServletException(ex);
		}
	}
	
	private boolean matchesRegex(String string, Pattern regex)
	{
		Matcher matcher = regex.matcher(string);
		return matcher.matches();
	}
	
	private void sendErrorResponse(HttpServletResponse response, int code, Exception exception) throws ServletException
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
