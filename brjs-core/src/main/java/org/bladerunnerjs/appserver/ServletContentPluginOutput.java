package org.bladerunnerjs.appserver;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.StaticContentPluginOutput;


public class ServletContentPluginOutput extends StaticContentPluginOutput
{

	private final ServletContext servletContext;
	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public ServletContentPluginOutput(App app, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		super( app, response.getOutputStream() );
		this.servletContext = servletContext;
		this.request = request;
		this.response = response;
	}
	
	@Override
	public void writeLocalUrlContentsToAnotherStream(String urlPath, OutputStream output) throws IOException {		
		try {
			if (urlPath.endsWith(".jsp")) {
				urlPath = (!urlPath.startsWith("/")) ? "/"+urlPath : urlPath;
    			CharResponseWrapper responseWrapper = new CharResponseWrapper(response);
    			servletContext.getRequestDispatcher(urlPath).include(request, responseWrapper);
    			IOUtils.copy(responseWrapper.getReader(), output);
    		} else {
    			super.writeLocalUrlContentsToAnotherStream(urlPath, output);
    		}
		} catch (ServletException ex) {
			throw new IOException(ex);
		}
	}
	
}
