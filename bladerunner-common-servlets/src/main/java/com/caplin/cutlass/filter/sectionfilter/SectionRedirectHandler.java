package com.caplin.cutlass.filter.sectionfilter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.model.BRJS;
import com.caplin.cutlass.CutlassConfig;

public class SectionRedirectHandler
{
	private static final String DEFAULT_ASPECT = "/"+CutlassConfig.DEFAULT_ASPECT_NAME+CutlassConfig.ASPECT_SUFFIX+"/";
	
	private final Set<String> aspects = new HashSet<String>();
	private Pattern directoryPattern;
	private final Logger logger;

	public SectionRedirectHandler(BRJS brjs, File contextDir)
	{
		logger = brjs.logger(LoggerType.FILTER, SectionRedirectHandler.class);
		List<String> directories = new ArrayList<String>();
		
		File[] children = contextDir.listFiles();
		children = (children.length > 0) ? children : new File[0];
		
		for (File child : children)
		{
			if (child.isDirectory() && !child.isHidden())
			{
				directories.add(child.getName());
				
				if(child.getName().endsWith(CutlassConfig.ASPECT_SUFFIX))
				{
					// Strip off the "-aspect".
					String aspect = child.getName();
					aspects.add(aspect.substring(0, aspect.length() - CutlassConfig.ASPECT_SUFFIX.length()));
				}
			}
		}
		
		logger.debug("init - found the following aspects: " + StringUtils.join(aspects,", "));
		
		directoryPattern = Pattern.compile("/(" + StringUtils.join(directories, "|") + ")/.*");
	}

	/**
	 * Returns the URL for the specified request given the sections that this
	 * handler was initialised with. 
	 * 
	 * examples: 
	 *  /						 -> /default-aspect/
	 *	/app1/					-> /app1/default-aspect/
	 *	/app1/xml.bundle		  -> /app1/default-aspect/xml.bundle
	 *	/app1/css/noir_css.bundle -> /app1/default-aspect/css/noir_css.bundle
	 *	/app1/mobile/xml.bundle   -> /app1/mobile-aspect/xml.bundle
	 * 
	 * @param requestUrl The initial request to be redirected.
	 * @return The URL that this request should be redirected to.
	 */
	public String getRedirectUrl(String requestUrl)
	{
		if(requestUrl.contains(CutlassConfig.SERVLET_PATH_PREFIX))
		{
			logger.debug("requestUrl '"+requestUrl+"' starts with '"+CutlassConfig.SERVLET_PATH_PREFIX+"' - doing redirect for servlet url");
			return getServletRedirectUrl(requestUrl);
		}
		if (directoryPattern.matcher(requestUrl).matches())
		{
			logger.debug("requestUrl '"+requestUrl+"' already contains an aspect url - no need to redirect");
			return requestUrl;
		}
		
		String aspect = extractAspect(requestUrl);
		if (aspect == null)
		{
			logger.debug("requestUrl '"+requestUrl+"' doesnt contain an aspect - prepending default aspect");
			// No aspect specified, so use the default section.
			return DEFAULT_ASPECT + requestUrl.substring(1);
		}
		
		// url contains an aspect name but no '-aspect' - add in the '-aspect'
		String endUrl = requestUrl.substring(getEndOfAspect(requestUrl));
		return "/" + aspect + CutlassConfig.ASPECT_SUFFIX + (endUrl.startsWith("/") ? "" : "/") + endUrl;
	}

	private String getServletRedirectUrl(String requestUrl)
	{
		int indexOfSubpathStartingWithServlet = requestUrl.indexOf(CutlassConfig.SERVLET_PATH_PREFIX);
		String returnUrl = requestUrl.substring(indexOfSubpathStartingWithServlet);
		return returnUrl;
	}

	private String extractAspect(String requestUrl)
	{
		String extractedAspect = requestUrl.substring(1, getEndOfAspect(requestUrl));
		
		for (String aspect : aspects)
		{
			if (extractedAspect.equals(aspect) || extractedAspect.equals(aspect + CutlassConfig.ASPECT_SUFFIX))
			{
				return aspect;
			}
		}
		return null;
	}
	
	private int getEndOfAspect(String requestUrl)
	{
		int lastPos = requestUrl.indexOf("/", 1);
		if (lastPos == -1)
		{
			return requestUrl.length();
		}
		return lastPos;
	}
}