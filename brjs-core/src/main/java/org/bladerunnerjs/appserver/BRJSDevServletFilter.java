package org.bladerunnerjs.appserver;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;


public class BRJSDevServletFilter implements Filter {
	private ServletContext servletContext;
	private BRJS brjs;
	private App app;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			servletContext = filterConfig.getServletContext();
			BRJSThreadSafeModelAccessor.initializeModel(servletContext);
			
			try {
				brjs = BRJSThreadSafeModelAccessor.aquireModel();
				app = brjs.locateAncestorNodeOfClass(new File(servletContext.getRealPath(".")), App.class);
			}
			finally {
				BRJSThreadSafeModelAccessor.releaseModel();
			}
		}
		catch (InvalidSdkDirectoryException e) {
			throw new ServletException(e);
		}
	}
	
	@Override
	public void destroy() {
		BRJSThreadSafeModelAccessor.destroy();
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String servletPath = request.getServletPath();
		String requestPath = request.getRequestURI().replaceFirst("^" + request.getContextPath(), "");
		
		if(!servletPath.equals("/brjs") && app.canHandleLogicalRequest(requestPath)) {
			request.getRequestDispatcher("/brjs" + requestPath).forward(request, response);
		}
		else {
			chain.doFilter(request, response);
		}
	}
}
