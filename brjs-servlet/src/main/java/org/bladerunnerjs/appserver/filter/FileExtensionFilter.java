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

		if(!resourceFile.exists()) {
			File htmlResourceFile = new File(resourceFile.getAbsolutePath() + ".html");
			File jspResourceFile = new File(resourceFile.getAbsolutePath() + ".jsp");

			if((htmlResourceFile.exists()) && (htmlResourceFile.isFile())) {
				request.getRequestDispatcher(requestPath + ".html").forward(request, response);
			}
			else if((jspResourceFile.exists()) && (jspResourceFile.isFile())) {
				request.getRequestDispatcher(requestPath + ".jsp").forward(request, response);
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// do nothing
	}

	private String path(String str) {
		return (str == null) ? "" : str;
	}
}
