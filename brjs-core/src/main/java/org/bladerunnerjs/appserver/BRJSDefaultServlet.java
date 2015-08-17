package org.bladerunnerjs.appserver;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Dispatcher;
import org.eclipse.jetty.servlet.DefaultServlet;

/**
 * A custom default servlet so that only BRJS URLs are handled by default. This is to ensure that requests can be made in development
 * that ultimately can't be made in production.
 * 
 * Hands off to the superclass (the Jetty implementation of the default servlet) to handle error pages.
 */
public class BRJSDefaultServlet extends DefaultServlet {
	private static final long serialVersionUID = 5300386968439230012L;
	
	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (isInternalErrorPageRedirect(req)) {
			super.doGet(req, resp);
		} else {
			sendError(resp);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		if (isInternalErrorPageRedirect(req)) {
			super.doDelete(req, resp);
		} else {
			sendError(resp);
		}
	}
	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		if (isInternalErrorPageRedirect(req)) {
			super.doHead(req, resp);
		} else {
			sendError(resp);
		}
	}
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		if (isInternalErrorPageRedirect(req)) {
			super.doOptions(req, resp);
		} else {
			sendError(resp);
		}
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		if (isInternalErrorPageRedirect(req)) {
			super.doPost(req, resp);
		} else {
			sendError(resp);
		}
	}
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		if (isInternalErrorPageRedirect(req)) {
			super.doPut(req, resp);
		} else {
			sendError(resp);
		}
	}
	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		if (isInternalErrorPageRedirect(req)) {
			super.doTrace(req, resp);
		} else {
			sendError(resp);
		}
	}
	
	private boolean isInternalErrorPageRedirect(HttpServletRequest request) {
		return (request.getAttribute(Dispatcher.FORWARD_REQUEST_URI) != null && 
				request.getAttribute(Dispatcher.ERROR_STATUS_CODE) != null &&
				request.getAttribute(Dispatcher.ERROR_REQUEST_URI) != null &&
				request.getAttribute(Dispatcher.ERROR_SERVLET_NAME) != null);
	}
	
	private void sendError(HttpServletResponse resp) throws IOException {
		resp.sendError(HttpServletResponse.SC_NOT_FOUND, "To ensure the development environment is as identical as possible to production BladeRunnerJS will not handle "
				+ "this request and not other servlets are configured to handle it. If you wish to serve up additional content at this URL you must either add a plugin to BRJS or"
				+ "configure a custom servlet to handle the request.");
	}

	
}
