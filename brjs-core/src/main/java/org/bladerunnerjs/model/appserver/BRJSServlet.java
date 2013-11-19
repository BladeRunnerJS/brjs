package org.bladerunnerjs.model.appserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;


public class BRJSServlet extends HttpServlet
{
	
	public class Messages {
		public static final String UNABLE_HANDLE_URL_ERROR = "Unable to handle request for URL %s";
	}
	
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
		PrintWriter writer = resp.getWriter();
		
		if (matchesRegex(requestPath, VERSION_REGEX))
		{
			writer.print(brjs.versionInfo().getVersionNumber());
		}
		else
		{
			attemptToMatchRequestToServletPlugin(req.getRequestURI(), requestPath, writer);
		}
	}
	
	
	private void attemptToMatchRequestToServletPlugin(String requestUrl, String requestPathRelativeToApp, PrintWriter writer) throws ServletException, IOException
	{
		throw new ServletException( String.format(Messages.UNABLE_HANDLE_URL_ERROR, requestUrl) );
	}

	private boolean matchesRegex(String string, Pattern regex)
	{
		Matcher matcher = regex.matcher(string);
		return matcher.matches();
	}
}
