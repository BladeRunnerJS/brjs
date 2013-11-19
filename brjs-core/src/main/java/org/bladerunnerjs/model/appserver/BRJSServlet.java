package org.bladerunnerjs.model.appserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bladerunnerjs.model.App;


public class BRJSServlet extends HttpServlet
{
	private static final long serialVersionUID = 1964608537461568895L;

	public static String SERVLET_PATH = "/brjs/*";
	
	
	public BRJSServlet(App app)
	{
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		resp.getWriter().write("BRJS Servlet");
	}
	
}
