package org.bladerunnerjs.appserver.filter;

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

public class FileExtensionFilter implements Filter {
	private ServletContext servletContext;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		servletContext = filterConfig.getServletContext();
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String requestPath = path(request.getServletPath()) + path(request.getPathInfo());
		File resourceFile = new File(servletContext.getRealPath(requestPath));

		if (!requestPath.matches("\\.[a-zA-Z0-9]+$") && !resourceFile.exists()) {
			if (new File(resourceFile.getAbsolutePath() + ".html").isFile()) {
				request.getRequestDispatcher(requestPath + ".html").forward(request, response);
			}
			else if (new File(resourceFile.getAbsolutePath() + ".jsp").isFile()) {
				request.getRequestDispatcher(requestPath + ".jsp").forward(request, response);
			} else {
				chain.doFilter(request, response);				
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		// do nothing
	}

	private String path(String str) {
		return (str == null) ? "" : str;
	}
}
