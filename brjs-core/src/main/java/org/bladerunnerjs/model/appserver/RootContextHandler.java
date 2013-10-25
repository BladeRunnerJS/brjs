package org.bladerunnerjs.model.appserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.bladerunnerjs.model.sinbin.CutlassConfig;


public class RootContextHandler extends AbstractHandler
{

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		String redirectUrl = getRedirectUrl(target);
		if (!redirectUrl.equals("")) 
		{
			response.sendRedirect(redirectUrl);			
		} else {
			/* \n allowed since the error is wrapped in a <pre> tag */
			response.sendError(404, CutlassConfig.APP_404_MESSAGE);
		}
	}

	private String getRedirectUrl(String url)
	{
		if (url.equals("") || url.equals("/"))
		{
			return CutlassConfig.BLADERUNNER_DASHBOARD_PATH;
		}
		return "";
	}

}
