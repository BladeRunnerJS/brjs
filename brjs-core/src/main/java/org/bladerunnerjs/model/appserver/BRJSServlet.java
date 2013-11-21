package org.bladerunnerjs.model.appserver;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.eclipse.jetty.servlet.DefaultServlet;


public class BRJSServlet extends DefaultServlet
{
	private static final long serialVersionUID = 1964608537461568895L;

	private static final Pattern VERSION_REGEX = Pattern.compile("/brjs/version/?");
	
	BRJS brjs;
	BRJSServletUtils servletUtils;
	
	//TODO: dont pass the app in - filters/servlets in web.xml must have default constructors
	public BRJSServlet(App app)
	{
		servletUtils = new BRJSServletUtils(app);
		brjs = app.root();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String requestPath = request.getRequestURI();
		String pathRelativeToApp = StringUtils.substringAfter(requestPath, request.getContextPath());

		if (matchesRegex(pathRelativeToApp, VERSION_REGEX))
		{
			response.getWriter().print(brjs.versionInfo().getVersionNumber());
		}
		else
		{
			boolean foundHandler = servletUtils.passRequestToApropriateContentPlugin(getServletContext(), request, response);
			if (!foundHandler)
			{
				servletUtils.sendErrorResponse(response, 404, new ResourceNotFoundException("No content plugin could be found for the request: " + requestPath) );
			}
		}
	}

	private boolean matchesRegex(String string, Pattern regex)
	{
		Matcher matcher = regex.matcher(string);
		return matcher.matches();
	}
	
}
