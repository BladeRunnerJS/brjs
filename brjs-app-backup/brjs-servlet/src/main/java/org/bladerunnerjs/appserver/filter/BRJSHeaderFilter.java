package org.bladerunnerjs.appserver.filter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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

import org.bladerunnerjs.appserver.util.LockedHeaderResponseWrapper;

public class BRJSHeaderFilter implements Filter {
	
	public static final String OUTPUT_ENCODING = "UTF-8";

	private static final Pattern VERSION_REGEX = Pattern.compile("/v/[0-9]+/");
	
	private static final String EXPIRES = "Expires";
	private static final String CACHE_CONTROL = "Cache-Control";
	
	private static final int MAX_AGE = 365;
	private static final String HEADER_DATE_FORMAT = "dd MMM yyyy kk:mm:ss z";
	private static final String CACHE_CONTROL_ALLOW_CACHE = "max-age=" + TimeUnit.DAYS.toSeconds(MAX_AGE) + ", public, must-revalidate";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// do nothing
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		
		response.setCharacterEncoding(OUTPUT_ENCODING);		
		
		if (VERSION_REGEX.matcher(request.getRequestURI()).find()) 
		{
			response.setHeader(CACHE_CONTROL, CACHE_CONTROL_ALLOW_CACHE);
			response.setHeader(EXPIRES, getExpiresHeader(MAX_AGE));
			LockedHeaderResponseWrapper responseWrapper = new LockedHeaderResponseWrapper(response, Arrays.asList(CACHE_CONTROL, EXPIRES));
			chain.doFilter(request, responseWrapper);
		} 
		else 
		{
			// leave unversioned requests untouched incase they are requests for custom servlets
			chain.doFilter(request, response);
		}
	}
	
	@Override
	public void destroy() {
		// do nothing
	}
	
	
	private String getExpiresHeader(int expires) {
		Date expdate = new Date();
		expdate.setTime(expdate.getTime() + TimeUnit.SECONDS.toMillis(expires));
		DateFormat df = new SimpleDateFormat(HEADER_DATE_FORMAT);
		return df.format(expdate);
	}
}
