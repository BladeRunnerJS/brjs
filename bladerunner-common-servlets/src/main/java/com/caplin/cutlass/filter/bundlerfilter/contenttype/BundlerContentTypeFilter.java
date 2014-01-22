package com.caplin.cutlass.filter.bundlerfilter.contenttype;

import java.io.IOException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.caplin.cutlass.ServletModelAccessor;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;

import com.caplin.cutlass.CutlassConfig;

public class BundlerContentTypeFilter implements Filter
{
	private static Map<String,String> contentTypeMap;
	private static Map<String,String> imageContentTypeMap;
	
	private ServletContext servletContext;
	private BRJS brjs;
	private Logger logger;
	
	//TODO: we shouldnt be setting the mime type for thirdparty files - this breaks the webserver since the server cant set the mime type
	
	public BundlerContentTypeFilter()
	{
		contentTypeMap = new HashMap<String,String>();
		contentTypeMap.put("txt", "text/plain");
		contentTypeMap.put("xml", "application/xml");
		contentTypeMap.put("css", "text/css");
		contentTypeMap.put("js", "text/javascript");
		contentTypeMap.put("i18n", "text/javascript");
		contentTypeMap.put("html", "text/html");
		
		imageContentTypeMap = new HashMap<String, String>();
		imageContentTypeMap.put("svg", "image/svg+xml");
		imageContentTypeMap.put("ico", "image/vnd.microsoft.icon");
		imageContentTypeMap.put("cur", "image/vnd.microsoft.icon");
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		brjs = ServletModelAccessor.initializeModel(filterConfig.getServletContext());
		servletContext = filterConfig.getServletContext();
		logger = brjs.logger(LoggerType.BUNDLER, BundlerContentTypeFilter.class);
	}
	
	@Override
	public void destroy() 
	{	
		ServletModelAccessor.destroy();
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		try
		{
			BladerunnerUri bladerunnerUri = new BladerunnerUri(brjs, servletContext, (HttpServletRequest) request);
			String requestURI = bladerunnerUri.logicalPath;
			
			if(requestURI.endsWith(".bundle"))
			{
				String contentType = getContentType(requestURI);
				response.setContentType(contentType);
				
				logger.debug("setting bundler content type to '" + contentType + "'");
			}
			chain.doFilter(request, response);
		}
		catch(IOException|ServletException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}
	}
	
	public String getContentType(String path)
	{
		String bundleType = getBundleType(path);
		if(bundleType.equals("image.bundle"))
		{
			return getImageContentType(path);
		}
		else
		{
			String fileType = StringUtils.substringBeforeLast(bundleType, ".");
			return getContentTypeForFileType(fileType);
		}
	}
	
	public String getContentTypeForFileType(String fileType)
	{
		String dummyFileName = "name."+fileType;
		String contentType = URLConnection.guessContentTypeFromName(dummyFileName);
		if (contentType != null)
		{
			return contentType;
		}
		return contentTypeMap.get(fileType);
	}
	
	private String getImageContentType(String path)
	{
		int lastUnderscoreIndex = path.lastIndexOf('_');
		if(lastUnderscoreIndex > 0)
		{
			String image = path.substring(0, lastUnderscoreIndex);
			String imageExtension = getImageExtension(image);
			String contentType = imageContentTypeMap.get(imageExtension);
			if(contentType != null)
			{
				return contentType;
			}
			return URLConnection.guessContentTypeFromName(image);
		}
		return null;
	}

	private String getImageExtension(String image)
	{
		int lastDotIndex = image.lastIndexOf(".");
		if(lastDotIndex > 0 && lastDotIndex < image.length() - 1)
		{
			return image.substring(lastDotIndex + 1);
		}
		return "";
	}

	private String getBundleType(String path)
	{
		if (path.endsWith(CutlassConfig.THIRDPARTY_BUNDLE_SUFFIX))
		{
			String filename = StringUtils.substringAfterLast(path, "/");
			filename = StringUtils.substringBeforeLast(filename, CutlassConfig.THIRDPARTY_BUNDLE_SUFFIX);
			return StringUtils.substringAfterLast(filename, ".");
		}
		
		int lastSlashIndex = path.lastIndexOf('/');
		int lastUnderscoreIndex = path.lastIndexOf('_');
		
		int lastIndex = Math.max(lastSlashIndex, lastUnderscoreIndex);
		return path.substring(lastIndex + 1);
	}
}
