package org.bladerunnerjs.model.appserver;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bladerunnerjs.core.plugin.servlet.ServletPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;


public class BRJSServlet extends HttpServlet
{
	private static final long serialVersionUID = 1964608537461568895L;

	public class Messages {
		public static final String CANNOT_FIND_SERVLET_PLUGIN_MSG = "Cannot find ServletPlugin for request %s";		
	}

	public static final String SERVLET_PATH = "/*";

	private static final Pattern VERSION_REGEX = Pattern.compile("brjs/version/?");
	
	private BRJS brjs;
	
	public BRJSServlet(App app)
	{
		brjs = app.root();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String requestPath = req.getPathInfo();
		
		if (matchesRegex(requestPath, VERSION_REGEX))
		{
			resp.getWriter().print(brjs.versionInfo().getVersionNumber());
		}
		else
		{
			passRequestToApropriateServletPlugin(req, resp);
		}
	}

	//TODO: this logic should be moved into the logical request handler
	private void passRequestToApropriateServletPlugin(HttpServletRequest req, HttpServletResponse resp) throws ServletException
	{
		BladerunnerUri bladerunnerUri = createBladeRunnerUri(req);
		for (ServletPlugin servletPlugin : brjs.servletPlugins())
		{
			try
			{
				ParsedRequest parsedRequest = servletPlugin.getRequestParser().parse(bladerunnerUri);
				handleRequestUsingServletPlugin(parsedRequest, servletPlugin, resp);
				return;
			}
			catch (MalformedRequestException e)
			{
				// do nothing - this bundler can't handle this request
			}
		}
		sendErrorResponse( resp, 404, new ResourceNotFoundException( String.format(Messages.CANNOT_FIND_SERVLET_PLUGIN_MSG, req.getRequestURI()) ) );
	}
	
	
	
	private void handleRequestUsingServletPlugin(ParsedRequest parsedRequest, ServletPlugin servletPlugin, HttpServletResponse resp) throws ServletException
	{
		try
		{
			servletPlugin.handleRequest(parsedRequest, null, resp.getOutputStream());
		}
		catch (BundlerProcessingException ex)
		{
			sendErrorResponse(resp, 500, ex);
		}
		catch (IOException ex)
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
