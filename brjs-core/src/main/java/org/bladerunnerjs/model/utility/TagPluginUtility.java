package org.bladerunnerjs.model.utility;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.BRJS;
//import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;

public class TagPluginUtility {

	private static final String attributePatternString = "(\\s+(\\w+)\\s*=\\s*\\\"([\\w]+)\\\")";
	private static final Pattern tagPattern = Pattern.compile("<@([\\w]+)("+attributePatternString+"*)?[ ]*/>");
	private static final Pattern attributePattern = Pattern.compile(attributePatternString);
	
	public static void filterContent(String content, BundleSet bundleSet, Writer writer, RequestMode opMode, String locale) throws IOException, NoTagHandlerFoundException
	{
		BRJS brjs = bundleSet.getBundlableNode().root();
		List<TagHandlerPlugin> tagHandlerPlugins = brjs.tagHandlers();
		
		Matcher matcher = tagPattern.matcher(content);
		StringBuffer result = new StringBuffer();
		
		while (matcher.find())
		{
			String replacement = handleTag(tagHandlerPlugins, bundleSet, opMode, locale, matcher.group(1), matcher.group(2));
			if (replacement != null)
			{
				matcher.appendReplacement(result, replacement);
			}
		}
		matcher.appendTail(result);
		
		writer.write(result.toString());
	}

	private static String handleTag(List<TagHandlerPlugin> tagHandlerPlugins, BundleSet bundleSet, RequestMode opMode, String locale, String tagName, String attributesContent) throws NoTagHandlerFoundException, IOException
	{
		StringWriter writer = new StringWriter();
		TagHandlerPlugin tagHandler = getTagHandlerForTag(tagHandlerPlugins, tagName);
		
		Map<String,String> attributes = getTagAttributes(attributesContent);
		
		if (opMode == RequestMode.Dev)
		{
			tagHandler.writeDevTagContent(attributes, bundleSet, locale, writer);
		}
		else if (opMode == RequestMode.Prod)
		{
			tagHandler.writeProdTagContent(attributes, bundleSet, locale, writer);		
		}
		else
		{
			throw new RuntimeException("Unsupported request mode '" + opMode.toString() + "'.");
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
