package org.bladerunnerjs.model.appserver;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;


public class BRJSServlet extends HttpServlet
{
	private static final long serialVersionUID = 1964608537461568895L;

	public static final String SERVLET_PATH = "/brjs/*";
	private static final Pattern VERSION_REGEX = Pattern.compile("/version/?");
	
	private App app;
	private BRJS brjs;
	
	public BRJSServlet(App app)
	{
		this.app = app;
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
			try
			{
				BladerunnerUri bladerunnerUri = new BladerunnerUri(brjs, getServletContext(), req);
				app.handleLogicalRequest(bladerunnerUri, resp.getOutputStream());
			}
			catch (MalformedRequestException e)
			{
				throw new ServletException(e);
			}
			catch (ResourceNotFoundException e)
			{
				throw new ServletException(e);
			}
			catch (BundlerProcessingException e)
			{
				throw new ServletException(e);
			}
		}
	}

	private boolean matchesRegex(String string, Pattern regex)
	{
		Matcher matcher = regex.matcher(string);
		return matcher.matches();
	}
}
