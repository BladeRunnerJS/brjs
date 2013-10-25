package com.caplin.cutlass.filter.production;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.sinbin.CutlassConfig;
import com.caplin.cutlass.bundler.parser.RequestParserFactory;

public class ValidRequestForBundledResourceFilter implements Filter {

	private Map<String, RequestParser> requestParsers;
	
	public ValidRequestForBundledResourceFilter()
	{
		requestParsers = new HashMap<String, RequestParser>();
		requestParsers.put("css.bundle", RequestParserFactory.createCssBundlerRequestParser());
		requestParsers.put("i18n.bundle", RequestParserFactory.createI18nBundlerRequestParser());		
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException 
	{	
		String requestPath = new URL(((HttpServletRequest) request).getRequestURL().toString()).getPath();
		String bundlePathFromSectionRoot = getBundlePathFromSectionRoot(requestPath);
		if(isValidBundledResourcePath(bundlePathFromSectionRoot))
		{
			StatusExposingCachingServletResponseWrapper responseWrapper = new StatusExposingCachingServletResponseWrapper((HttpServletResponse) response);
			chain.doFilter(request, responseWrapper);
			
			if(responseWrapper.getStatus() == 404) {
				responseWrapper.setStatus(200);
				responseWrapper.setContentLength(0);
				responseWrapper.resetBuffer();
			}
			else {
				responseWrapper.writeCacheToResponse();
			}
		}
		else
		{
			chain.doFilter(request, response);
		}
	}
	
	private boolean isValidBundledResourcePath(String bundlePathFromSectionRoot)
	{
		RequestParser requestParser = getRequestParserForRequest(bundlePathFromSectionRoot);
		if(requestParser == null) {
			return false;
		}
		try {
			requestParser.parse(bundlePathFromSectionRoot);
		} catch (MalformedRequestException e) {
			return false;
		}
		return true;
	}

	protected String getBundlePathFromSectionRoot(String requestPath) 
	{
		Pattern pattern = Pattern.compile(".+\\"+CutlassConfig.ASPECT_SUFFIX+"/(.*)");
		Matcher matcher = pattern.matcher(requestPath);
		if(matcher.find()) {
			return matcher.group(1);
		}
		else {
			return "";
		}
		
	}

	private RequestParser getRequestParserForRequest(String requestPath)
	{
		String requestFilename = StringUtils.substringAfterLast(requestPath, "/");
		for (String bundlerFileExtension : requestParsers.keySet())
		{
			if (requestFilename.equals(bundlerFileExtension) || requestFilename.endsWith("_" + bundlerFileExtension))
			{
				return requestParsers.get(bundlerFileExtension);
			}
		}
		return null;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
