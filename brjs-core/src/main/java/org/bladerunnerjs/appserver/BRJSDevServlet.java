package org.bladerunnerjs.appserver;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.plugin.ResponseContent;


public class BRJSDevServlet extends HttpServlet {
	private static final long serialVersionUID = 1964608537461568895L;

	private static final String CONTENT_TYPE = "Content-Type";
	
	private App app;
	private ServletContext servletContext;
	private BRJS brjs;
	
	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		servletContext = config.getServletContext();
		
		try {
			ThreadSafeStaticBRJSAccessor.initializeModel( new File(servletContext.getRealPath("/")) );
		}
		catch (InvalidSdkDirectoryException e) {
			throw new ServletException(e);
		}
		
		try {
			brjs = ThreadSafeStaticBRJSAccessor.aquireModel();
			app = BRJSServletUtils.localeAppForContext(brjs, servletContext);
		}
		finally {
			ThreadSafeStaticBRJSAccessor.releaseModel();
		}
	}
	
	@Override
	public void destroy() {
		ThreadSafeStaticBRJSAccessor.destroy();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestPath = request.getRequestURI().replaceFirst("^" + request.getContextPath() + request.getServletPath() + "/", "");
		
		if (!requestPath.endsWith("/")) {
			String fileName = StringUtils.substringAfterLast(requestPath, "/");
			String mimeType = servletContext.getMimeType(fileName);
			if (mimeType != null) {
				response.setHeader(CONTENT_TYPE, mimeType);
			}
		}
		
		ThreadSafeStaticBRJSAccessor.aquireModel();
		UrlContentAccessor contentAccessor = new ServletContentAccessor(app, servletContext, request, response);
		try ( ResponseContent content = app.requestHandler().handleLogicalRequest(requestPath, contentAccessor); )
		{
			if (!response.isCommitted()) { // check the ServletContentAccessor hasnt been used to handle a request and sent headers
				content.write( response.getOutputStream() );
			}
		}
		catch (MalformedRequestException e) {
			response.sendError(400, e.getMessage());
		}
		catch (ResourceNotFoundException e) {
			response.sendError(404, e.getMessage());
		}
		catch (ContentProcessingException e) {
			response.sendError(500, e.getMessage());
		}
		catch (ModelOperationException e) {
			throw new ServletException(e);
		}
		finally {
			ThreadSafeStaticBRJSAccessor.releaseModel();
		} 
	}
	
}
