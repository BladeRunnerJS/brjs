package org.bladerunnerjs.appserver;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;


public class BRJSDevServletFilter implements Filter {
	public static final String IGNORE_REQUEST_ATTRIBUTE = "brjs-ignore-request";
	
	private ServletContext servletContext;
	private BRJS brjs;
	private App app;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			servletContext = filterConfig.getServletContext();
			ThreadSafeStaticBRJSAccessor.initializeModel( new File(servletContext.getRealPath("/")) );
			
			try {
				brjs = ThreadSafeStaticBRJSAccessor.aquireModel();
				app = BRJSServletUtils.localeAppForContext(brjs, servletContext);
			}
			finally {
				ThreadSafeStaticBRJSAccessor.releaseModel();
			}
		}
		catch (InvalidSdkDirectoryException e) {
			throw new ServletException(e);
		}
	}
	
	@Override
	public void destroy() {
		ThreadSafeStaticBRJSAccessor.destroy();
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String servletPath = request.getServletPath();
		String requestPath = request.getRequestURI().replaceFirst("^" + request.getContextPath() + "/", "");
		
		if (requestShouldHaveASlashAppended(requestPath, app)) {
			/* this is done here rather than in the model for several reasons:
			 *  - this is closer to how apps in production would behave
			 *  - the model shouldn't have any concept of 'welcome' pages
			 *  - changing the model would mean that the build-app utils now need to understand the new redirection behavior 
			 */
			response.sendRedirect(request.getRequestURI()+"/");
		} else if (!servletPath.equals("/brjs") && app.requestHandler().canHandleLogicalRequest(requestPath) && request.getAttribute(IGNORE_REQUEST_ATTRIBUTE) == null) {
			request.getRequestDispatcher("/brjs/" + requestPath).forward(request, response);
		}
		else {
			chain.doFilter(request, response);
		}
	}

	private boolean requestShouldHaveASlashAppended(String requestPath, App app) throws ServletException
	{
		if (requestPath.endsWith("/")) {
			return false;
		}
		
		boolean appCanHandleRequestWithAppendedSlash = app.requestHandler().canHandleLogicalRequest(requestPath+"/");
		if (Pattern.matches("^[a-zA-Z0-9_]+$", requestPath) && appCanHandleRequestWithAppendedSlash) {
			return true; // /app/aspect was requested
		}
		if (requestPath.endsWith("/workbench") && appCanHandleRequestWithAppendedSlash) {
			return true; // a workbench without a trailing / was requested
		}
		
		String lastPartOfUrl = StringUtils.substringAfterLast(requestPath, "/");
		if (Pattern.matches(Locale.LANGUAGE_AND_COUNTRY_CODE_FORMAT, lastPartOfUrl) && appCanHandleRequestWithAppendedSlash) {
			for (Locale supportedLocale : getAppLocales(app)) {
				if (supportedLocale.toString().equals(lastPartOfUrl)) {
					return true; // a locale without a trailing / was requested
				}
			}
		}
		return false;
	}

	private Locale[] getAppLocales(App app) throws ServletException
	{
		try
		{
			return app.appConf().getLocales();
		}
		catch (ConfigException ex)
		{
			throw new ServletException(ex);
		}
	}
	
}
