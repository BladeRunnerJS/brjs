package org.bladerunnerjs.appserver.filter;

import java.io.EOFException;
import java.io.IOException;
import java.lang.StringBuilder;
import java.io.Reader;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bladerunnerjs.appserver.util.CommitedResponseCharResponseWrapper;
import org.bladerunnerjs.appserver.util.ExceptionThrowingMissingTokenHandler;
import org.bladerunnerjs.appserver.util.JndiTokenFinder;
import org.bladerunnerjs.appserver.util.TokenReplacingReader;

public class TokenisingServletFilter implements Filter
{
	private JndiTokenFinder tokenFinder;
	
	public static final String IGNORE_REQUEST_ATTRIBUTE = "brjs-dont-tokenise-request";
	
	private static final String LOCALE_REGEX = "/|/[a-z]{2}|/[a-z]{2}_[A-Z]{2}";
	private static final String DEFAULT_FILE_EXTENSION_REGEX = "js|xml|json|html|htm|jsp";
	
	private String appName;
	private Pattern processUrlPattern;
	
	public TokenisingServletFilter() throws ServletException
	{
	}
	
	/* this should only be used for testing */
	public TokenisingServletFilter(JndiTokenFinder tokenFinder) throws ServletException
	{
		this.tokenFinder = tokenFinder;
	}

	@Override
	public void init(FilterConfig filterConfig)
	{
		String[] pathSplit = filterConfig.getServletContext().getContextPath().split("/");
		appName = pathSplit[pathSplit.length - 1];
		
		String regexParam = filterConfig.getInitParameter("extensionRegex");
		String fileExtensionRegex = (regexParam != null) ? regexParam : DEFAULT_FILE_EXTENSION_REGEX;
		processUrlPattern = Pattern.compile("^.*("+LOCALE_REGEX+"|\\.("+fileExtensionRegex+"))$");
	}
	
	@Override
	public void destroy()
	{
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		if (shouldProcessResponse(request))
		{
			ServletOutputStream out = response.getOutputStream();
			CommitedResponseCharResponseWrapper responseWrapper = new CommitedResponseCharResponseWrapper((HttpServletResponse) response);
			chain.doFilter(request, responseWrapper);
			
			try
			{
				if (!response.isCommitted()) { // only write the content if the headers havent been commited (an error code hasnt been sent)
					Reader streamTokeniserReader = getStreamTokeniser(responseWrapper.getReader());
					
					char[] charArray = new char[8 * 1024];
				    StringBuilder filteredDataStringBuilder = new StringBuilder();
				    int numCharsRead;
				    while ((numCharsRead = streamTokeniserReader.read(charArray, 0, charArray.length)) != -1) {
				        filteredDataStringBuilder.append(charArray, 0, numCharsRead);
				    }
				    byte[] filteredData = filteredDataStringBuilder.toString().getBytes();
					
					response.setContentLength(filteredData.length);
					out.write(filteredData);
					response.flushBuffer();
				}
			}
			catch(EOFException e) {
				// the browser has closed it's connection early -- re-throw
				throw e;
			}
			catch(Exception e)
			{
				throw new ServletException(e);
			}
		}
		else
		{
			chain.doFilter(request, response);
		}
	}

	private boolean shouldProcessResponse(ServletRequest request)
	{
		HttpServletRequest theRequest = (HttpServletRequest) request;
		String requestUrl = theRequest.getRequestURL().toString();
		
		return processUrlPattern.matcher(requestUrl).matches() && request.getAttribute(IGNORE_REQUEST_ATTRIBUTE) == null;
	}
	
	private Reader getStreamTokeniser(Reader reader) throws ServletException {
		if (tokenFinder == null) {
			try {
				tokenFinder = new JndiTokenFinder();
			} catch(NamingException ex) {
				throw new ServletException("Error getting context for JNDI lookups. (" + ex + ")", ex);
			}
		}
		return new TokenReplacingReader(appName, tokenFinder, reader, new ExceptionThrowingMissingTokenHandler());
	}
	
}
