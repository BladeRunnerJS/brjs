package org.bladerunnerjs.appserver;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BRJSServletFilter implements Filter {
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String E_TAG = "ETag";
	private static final String EXPIRES = "Expires";
	private static final String CACHE_CONTROL = "Cache-Control";
	private static final String LAST_MODIFIED = "Last-Modified";
	
	private static final long MAX_AGE = TimeUnit.DAYS.toSeconds(365);
	private static final String HEADER_DATE_FORMAT = "dd MMM yyyy kk:mm:ss z";
	
	private static final String CACHE_CONTROL_ALLOW_CACHE = "max-age=" + MAX_AGE + ", public, must-revalidate";
	private static final String CACHE_CONTROL_NO_CACHE = "no-cache, must-revalidate";
	
	private static final Pattern TEXT_REQUEST_PATTERN = Pattern.compile(".*\\.(js|html|xml|css)$");
	private static final List<String> LOCKED_HEADERS = Arrays.asList(LAST_MODIFIED, CACHE_CONTROL, EXPIRES, E_TAG);
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// do nothing
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		LockedHeaderResponseWrapper responseWrapper = new LockedHeaderResponseWrapper(response, LOCKED_HEADERS);
		
		setContentTypeHeaders(request, responseWrapper);
		setCachingHeaders(request, responseWrapper);
		
		chain.doFilter(request, responseWrapper);
	}
	
	@Override
	public void destroy() {
		// do nothing
	}
	
	private void setContentTypeHeaders(HttpServletRequest request, HttpServletResponse response) {
		String requestPath = request.getRequestURI();
		
		if(TEXT_REQUEST_PATTERN.matcher(requestPath).matches()) {
			response.setCharacterEncoding("UTF-8");
			
			if(requestPath.endsWith(".js")) {
				response.setHeader(CONTENT_TYPE, "application/javascript");
			}
		}
	}
	
	private void setCachingHeaders(HttpServletRequest request, LockedHeaderResponseWrapper responseWrapper) {
		responseWrapper.forceSetHeader(LAST_MODIFIED, "");
		
		if(request.getRequestURI().matches("/v/[0-9]+/")) {
			responseWrapper.forceSetHeader(CACHE_CONTROL, CACHE_CONTROL_ALLOW_CACHE);
			responseWrapper.forceSetHeader(EXPIRES, getExpiresHeader());
		}
		else {
			responseWrapper.forceSetHeader(CACHE_CONTROL, CACHE_CONTROL_NO_CACHE);
			responseWrapper.forceSetHeader(EXPIRES, "");
		}
		responseWrapper.forceSetHeader(E_TAG, "");
	}
	
	private String getExpiresHeader() {
		Date expdate = new Date();
		expdate.setTime(expdate.getTime() + TimeUnit.SECONDS.toMillis(MAX_AGE));
		DateFormat df = new SimpleDateFormat(HEADER_DATE_FORMAT);
		return df.format(expdate);
	}
}
