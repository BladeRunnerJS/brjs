package org.bladerunnerjs.model.utility;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.BRJS;
//import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;

public class TagPluginUtility {

	private static final String NEW_LINE = String.format("%n");
	private static final String attributePatternString = "(\\s+(\\w+)\\s*=\\s*\\\"([\\w]+)\\\")";
	private static final Pattern tagPattern = Pattern.compile("<@([A-Za-z][A-Za-z0-9._-]+)("+attributePatternString+"*)?[ ]*@/>");
	private static final Pattern attributePattern = Pattern.compile(attributePatternString);
	
	public static void filterContent(String content, BundleSet bundleSet, Writer writer, RequestMode requestMode, String locale) throws IOException, NoTagHandlerFoundException
	{
		BRJS brjs = bundleSet.getBundlableNode().root();
		List<TagHandlerPlugin> tagHandlerPlugins = brjs.plugins().tagHandlers();
		
		Matcher matcher = tagPattern.matcher(content);
		StringBuffer result = new StringBuffer();
		
		while (matcher.find())
		{
			String replacement = handleTag(tagHandlerPlugins, bundleSet, requestMode, locale, matcher.group(1), matcher.group(2));
			if (replacement != null)
			{
				matcher.appendReplacement(result, replacement);
			}
		}
		matcher.appendTail(result);
		
		String filteredContent = result.toString();
		if (filteredContent.endsWith(NEW_LINE)) // matcher.appendTail seems to append an extra \n that wasn't in the original content, so we remove it
		{
			filteredContent = StringUtils.substringBeforeLast(filteredContent, NEW_LINE);
		}
		
		writer.write(filteredContent);
		writer.flush();
	}

	private static String handleTag(List<TagHandlerPlugin> tagHandlerPlugins, BundleSet bundleSet, RequestMode requestMode, String locale, String tagName, String attributesContent) throws NoTagHandlerFoundException, IOException
	{
		StringWriter writer = new StringWriter();
		TagHandlerPlugin tagHandler = getTagHandlerForTag(tagHandlerPlugins, tagName);
		
		Map<String,String> attributes = getTagAttributes(attributesContent);
		
		if (requestMode == RequestMode.Dev)
		{
			tagHandler.writeDevTagContent(attributes, bundleSet, locale, writer);
		}
		else if (requestMode == RequestMode.Prod)
		{
			tagHandler.writeProdTagContent(attributes, bundleSet, locale, writer);		
		}
		else
		{
			throw new RuntimeException("Unsupported request mode '" + requestMode.toString() + "'.");
		}
		return writer.toString();
	}

	private static Map<String, String> getTagAttributes(String attributesContent)
	{
		Map<String, String> attributes = new LinkedHashMap<String,String>();
		
		if (attributesContent != null)
		{
    		Matcher matcher = attributePattern.matcher(attributesContent);
    		while (matcher.find())
    		{
    			for (int groupNum = 1; groupNum <= matcher.groupCount(); groupNum++)
    			{
    				String thisAttribute = matcher.group(groupNum);
    				
    				Matcher thisAttributeMatcher = attributePattern.matcher(thisAttribute);
    				
    				while (thisAttributeMatcher.find())
    	    		{
    					String key = thisAttributeMatcher.group(2).trim();
    					String value = thisAttributeMatcher.group(3).trim();
    					attributes.put(key, value);
    	    		}
    			}
    		}
		}
		
		return attributes;
	}

	private static TagHandlerPlugin getTagHandlerForTag(List<TagHandlerPlugin> tagHandlerPlugins, String tagName) throws NoTagHandlerFoundException
	{
		for (TagHandlerPlugin tagHandler : tagHandlerPlugins)
		{
			if (tagHandler.getTagName().equals(tagName))
			{
				return tagHandler;
			}
		}
		throw new NoTagHandlerFoundException(tagName);
	}
}
