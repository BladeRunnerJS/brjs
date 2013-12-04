package org.bladerunnerjs.model.utility;

import java.io.IOException;
import java.io.StringReader;
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
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class TagPluginUtility {

	private static final String NEW_LINE = String.format("%n");
	private static final String TAG_START = "<@";
	private static final String TAG_END = "@/>";
	private static final String XML_TAG_START = "<";
	private static final String XML_TAG_END = "/>";
	private static final Pattern tagPattern = Pattern.compile(TAG_START+"([A-Za-z][A-Za-z0-9._-]+)([ ]+[^\\s=]+=[^\\s=]+)*[ ]*"+TAG_END);
	
	public static void filterContent(String content, BundleSet bundleSet, Writer writer, RequestMode requestMode, String locale) throws IOException, NoTagHandlerFoundException, DocumentException
	{
		BRJS brjs = bundleSet.getBundlableNode().root();
		List<TagHandlerPlugin> tagHandlerPlugins = brjs.plugins().tagHandlers();
		
		Matcher matcher = tagPattern.matcher(content);
		StringBuffer result = new StringBuffer();
		
		while (matcher.find())
		{
			String replacement = handleTag(tagHandlerPlugins, bundleSet, requestMode, locale, matcher.group(0));
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

	private static String handleTag(List<TagHandlerPlugin> tagHandlerPlugins, BundleSet bundleSet, RequestMode requestMode, String locale, String tagContent) throws IOException, DocumentException
	{
		String xmlContent = StringUtils.replaceOnce(tagContent, TAG_START, XML_TAG_START);
		xmlContent = StringUtils.replaceOnce(xmlContent, TAG_END, XML_TAG_END);
		
		StringReader xmlContentReader = new StringReader(xmlContent);
		
        Document document;
        try
        {
        	document = new SAXReader().read(xmlContentReader);
        }
        catch (DocumentException ex)
        {
        	return tagContent;
        }
        Element root = document.getRootElement();
		
		try
		{
			return handleTagXml(tagHandlerPlugins, bundleSet, requestMode, locale, root);
		}
		catch (NoTagHandlerFoundException e)
		{
			//TODO: stop catching this exception when all tag handers have been moved to new style plugins
			return tagContent;
		}
	}

	private static String handleTagXml(List<TagHandlerPlugin> tagHandlerPlugins, BundleSet bundleSet, RequestMode requestMode, String locale, Element element) throws NoTagHandlerFoundException, IOException
	{
		StringWriter writer = new StringWriter();
		
		String tagName = element.getName();
		TagHandlerPlugin tagHandler = getTagHandlerForTag(tagHandlerPlugins, tagName);
		
		Map<String,String> attributes = getTagAttributes(element);
		
		writeTagContent(bundleSet, requestMode, locale, writer, tagHandler, attributes);
		
		return writer.toString();
	}

	public static void writeTagContent(BundleSet bundleSet, RequestMode requestMode, String locale, StringWriter writer, TagHandlerPlugin tagHandler, Map<String, String> attributes) throws IOException
	{
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
	}

	private static Map<String, String> getTagAttributes(Element element)
	{
		Map<String, String> attributes = new LinkedHashMap<String,String>();
		
		for (Object o : element.attributes())
		{
			Attribute attribute = (Attribute) o;
			attributes.put(attribute.getName(), attribute.getValue());
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
