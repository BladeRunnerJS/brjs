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
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.utility.ContentPathParser;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.bundler.parser.RequestParserFactory;

public class ValidRequestForBundledResourceFilter implements Filter {

	private Map<String, ContentPathParser> contentPathParsers;
	
	public ValidRequestForBundledResourceFilter()
	{
		contentPathParsers = new HashMap<String, ContentPathParser>();
		contentPathParsers.put("css.bundle", RequestParserFactory.createCssBundlerContentPathParser());
		contentPathParsers.put("i18n.bundle", RequestParserFactory.createI18nBundlerContentPathParser());		
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
		ContentPathParser contentPathParser = getRequestParserForRequest(bundlePathFromSectionRoot);
		if(contentPathParser == null) {
			return false;
		}
		try {
			contentPathParser.parse(bundlePathFromSectionRoot);
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

	private ContentPathParser getRequestParserForRequest(String contentPath)
	{
		String contentFilename = StringUtils.substringAfterLast(contentPath, "/");
		for (String bundlerFileExtension : contentPathParsers.keySet())
		{
			if (contentFilename.equals(bundlerFileExtension) || contentFilename.endsWith("_" + bundlerFileExtension))
			{
				return contentPathParsers.get(bundlerFileExtension);
			}
		}
		return null;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
