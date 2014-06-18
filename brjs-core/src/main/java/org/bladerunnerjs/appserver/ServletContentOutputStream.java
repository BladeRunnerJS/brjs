package org.bladerunnerjs.appserver;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.StaticContentOutputStream;


public class ServletContentOutputStream extends StaticContentOutputStream
{

	private final ServletContext servletContext;
	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public ServletContentOutputStream(App app, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		super( app, response.getOutputStream() );
		this.servletContext = servletContext;
		this.request = request;
		this.response = response;
	}
	
	@Override
	public String getLocalUrlContents(String urlPath) throws IOException {		
		try {
			if (urlPath.endsWith(".jsp")) {
				urlPath = (!urlPath.startsWith("/")) ? "/"+urlPath : urlPath;
    			CharResponseWrapper responseWrapper = new CharResponseWrapper(response);
    			servletContext.getRequestDispatcher(urlPath).include(request, responseWrapper);
    			return IOUtils.toString(responseWrapper.getReader());
    		} else {
    			return super.getLocalUrlContents(urlPath);
    		}
		} catch (ServletException ex) {
			throw new IOException(ex);
		}
	}
	
	@Override
	public void writeLocalUrlContents(String url) throws IOException {
		IOUtils.write( getLocalUrlContents(url), this );
	}
	
}
