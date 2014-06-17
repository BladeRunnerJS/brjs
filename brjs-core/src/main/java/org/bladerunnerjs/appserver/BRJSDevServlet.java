package org.bladerunnerjs.appserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.utility.PageAccessor;
import org.bladerunnerjs.utility.RelativePathUtility;


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
			BRJSThreadSafeModelAccessor.initializeModel(servletContext);
		}
		catch (InvalidSdkDirectoryException e) {
			throw new ServletException(e);
		}
		
		try {
			brjs = BRJSThreadSafeModelAccessor.aquireModel();
			app = brjs.locateAncestorNodeOfClass(new File(servletContext.getRealPath("/")), App.class);
			
			if(app == null) {
 				throw new ServletException("Unable to calculate app for Servlet. Context path for expected app was '" + servletContext.getRealPath("/") + "'.");
 			}
		}
		finally {
			BRJSThreadSafeModelAccessor.releaseModel();
		}
	}
	
	@Override
	public void destroy() {
		BRJSThreadSafeModelAccessor.destroy();
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
		
		try {
			BRJSThreadSafeModelAccessor.aquireModel();
			app.handleLogicalRequest(requestPath, response.getOutputStream(), new BRJSPageAccessor(request, response));
		}
		catch (MalformedRequestException | ResourceNotFoundException | ContentProcessingException e) {
			throw new ServletException(e);
		}
		finally {
			BRJSThreadSafeModelAccessor.releaseModel();
		}
	}
	
	private class BRJSPageAccessor implements PageAccessor {
		private final HttpServletRequest request;
		private final HttpServletResponse response;
		
		public BRJSPageAccessor(HttpServletRequest request, HttpServletResponse response) {
			this.request = request;
			this.response = response;
		}
		
		@Override
		public String getIndexPage(File indexPage) throws IOException {
			try {
				String requestPath = "/" + RelativePathUtility.get(app.dir(), indexPage);
				return getRequestPath(requestPath);
			}
			catch (ServletException ex) {
				throw new IOException(ex);
			}
		}
		
		private String getRequestPath(String requestPath) throws IOException, UnsupportedEncodingException, ServletException {
			if (requestPath.endsWith(".jsp")) {
    			CharResponseWrapper responseWrapper = new CharResponseWrapper(response);
    			servletContext.getRequestDispatcher(requestPath).include(request, responseWrapper);
    			
    			return IOUtils.toString(responseWrapper.getReader());
			}
			File requestPathFile = new File(servletContext.getRealPath("/")+requestPath);
			return IOUtils.toString( new FileInputStream(requestPathFile) );
		}
	}
}
