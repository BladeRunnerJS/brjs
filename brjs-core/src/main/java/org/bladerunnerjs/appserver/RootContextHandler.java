package org.bladerunnerjs.appserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


public class RootContextHandler extends AbstractHandler
{
	private static final String APP_404_MESSAGE = "The requested application was not found. If you have recently created or imported an app " +
		" you may need to wait a few seconds for the app to be started.";
	private static final String BLADERUNNER_DASHBOARD_PATH = "/dashboard/";
	
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		String redirectUrl = getRedirectUrl(target);
		if (!redirectUrl.equals("")) 
		{
			response.sendRedirect(redirectUrl);			
		} else {
			/* \n allowed since the error is wrapped in a <pre> tag */
			response.sendError(404, APP_404_MESSAGE);
		}
	}

	private String getRedirectUrl(String url)
	{
		if (url.equals("") || url.equals("/"))
		{
			return BLADERUNNER_DASHBOARD_PATH;
		}
		return "";
	}

}
