package org.bladerunnerjs.utility;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.TagHandlerPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class TagPluginUtility {

	private static final String NEW_LINE = String.format("%n");
	private static final String TAG_START = "<@";
	private static final String TAG_END = "@[ ]*/[ ]*>";
	private static final String XML_TAG_START = "<";
	private static final String XML_TAG_END = "/>";
	private static final Pattern tagPattern = Pattern.compile(TAG_START+"([A-Za-z][A-Za-z0-9._-]+)([ ]+[^\\s=]+=[^\\s=]+)*[ ]*"+TAG_END);
	
	public static void filterContent(String content, BundleSet bundleSet, Writer writer, RequestMode requestMode, Locale locale, String version) throws IOException, NoTagHandlerFoundException
	{
		List<TagHandlerPlugin> tagHandlerPlugins = bundleSet.bundlableNode().root().plugins().tagHandlerPlugins();
		StringBuffer result = new StringBuffer();
		
		TagMatchHandler tagMatchHandler = new TagMatchHandler() {
			@Override
			public void handleTagMatch(Matcher matcher, TagMatch tagMatch) throws IOException, NoTagHandlerFoundException
			{
				TagHandlerPlugin tagHandler = getTagHandlerForTag(tagHandlerPlugins, tagMatch.tag);
				String replacement = getTagReplacement(bundleSet, requestMode, locale, version, tagHandler, tagMatch.attributes);
				matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
			}
			@Override
			public void handleUnprocessableTagMatch(Matcher matcher, String tagContent)
			{
				matcher.appendReplacement(result, tagContent);
			}
			@Override
			public void handleMatcherTail(Matcher matcher)
			{
				matcher.appendTail(result);
			}
		};
		
		findAndHandleTagMatches(content, bundleSet, requestMode, locale, tagMatchHandler);
		
		String filteredContent = result.toString();
		if (filteredContent.endsWith(NEW_LINE)) // matcher.appendTail seems to append an extra \n that wasn't in the original content, so we remove it
		{
			filteredContent = StringUtils.substringBeforeLast(filteredContent, NEW_LINE);
		}
		
		writer.write(filteredContent);
		writer.flush();
	}

	public static Map<String, Map<String, String>> getUsedTagsAndAttributes(String content, BundleSet bundleSet, RequestMode requestMode, Locale locale) throws IOException, NoTagHandlerFoundException
	{
		List<TagHandlerPlugin> tagHandlerPlugins = bundleSet.bundlableNode().root().plugins().tagHandlerPlugins();
		
		Map<String,Map<String,String>> tagsAndAttributes = new LinkedHashMap<>();
		
		TagMatchHandler tagMatchHandler = new TagMatchHandler() {
			@Override
			public void handleTagMatch(Matcher matcher, TagMatch tagMatch) throws IOException, NoTagHandlerFoundException
			{
				getTagHandlerForTag(tagHandlerPlugins, tagMatch.tag); // check the tag is valid
				if (!tagsAndAttributes.containsKey(tagMatch.tag)) {
					tagsAndAttributes.put(tagMatch.tag, new LinkedHashMap<>());
				}
				tagsAndAttributes.get(tagMatch.tag).putAll( tagMatch.attributes );
			}
			@Override
			public void handleUnprocessableTagMatch(Matcher matcher, String tagContent)
			{
			}
			@Override
			public void handleMatcherTail(Matcher matcher)
			{
			}
		};
		
		findAndHandleTagMatches(content, bundleSet, requestMode, locale, tagMatchHandler);
		
		return tagsAndAttributes;
	}
	
	private static void findAndHandleTagMatches(String content, BundleSet bundleSet, RequestMode requestMode, Locale locale, TagMatchHandler tagMatchHandler) throws IOException, NoTagHandlerFoundException {
		List<TagHandlerPlugin> tagHandlerPlugins = bundleSet.bundlableNode().root().plugins().tagHandlerPlugins();
		
		Matcher matcher = tagPattern.matcher(content);
		while (matcher.find())
		{
			String tagContent = matcher.group(0);
			try
			{
				TagMatch tagMatch = handleTag(tagHandlerPlugins, bundleSet, requestMode, locale, tagContent);
				tagMatchHandler.handleTagMatch(matcher, tagMatch);
			}
			catch (ParserConfigurationException | SAXException e)
			{
				tagMatchHandler.handleUnprocessableTagMatch(matcher, tagContent);
			}
		}
		tagMatchHandler.handleMatcherTail(matcher);
	}
	
	private static TagMatch handleTag(List<TagHandlerPlugin> tagHandlerPlugins, BundleSet bundleSet, RequestMode requestMode, Locale locale, String tagContent) throws ParserConfigurationException, SAXException, IOException
	{
		String xmlContent = StringUtils.replaceOnce(tagContent, TAG_START, XML_TAG_START);
		xmlContent = xmlContent.replaceFirst(TAG_END, XML_TAG_END);
		
		Document document;
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder domParser = builderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
		domParser.setErrorHandler(new DocumentBuilderErrorParser(bundleSet.bundlableNode().root().logger(TagPluginUtility.class)));
		
		domParser.setErrorHandler(new SilentDomParserErrorHandler(bundleSet.bundlableNode().root()));
		
		document = domParser.parse(stream);
		Element root = document.getDocumentElement();
		
		return new TagMatch(root.getNodeName(), getTagAttributes(root));
	}

	private static String getTagReplacement(BundleSet bundleSet, RequestMode requestMode, Locale locale, String version, TagHandlerPlugin tagHandler, Map<String, String> attributes) throws IOException
	{
		StringWriter writer = new StringWriter();
		if (requestMode == RequestMode.Dev || requestMode == RequestMode.Prod)
		{
			tagHandler.writeTagContent(attributes, bundleSet, requestMode, locale, writer, version);
		}
		else
		{
			throw new RuntimeException("Unsupported request mode '" + requestMode.toString() + "'.");
		}
		return writer.toString();
	}

	private static Map<String, String> getTagAttributes(Element element)
	{
		Map<String, String> attributes = new LinkedHashMap<String,String>();
		NamedNodeMap sourceAttributes = element.getAttributes();
		
		for(int i = 0; i < sourceAttributes.getLength(); ++i) {
			Attr attribute = (Attr) sourceAttributes.item(i);
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
	
	
	static class SilentDomParserErrorHandler implements ErrorHandler {
		private Logger logger;
		public SilentDomParserErrorHandler(BRJS brjs) {
			this.logger = brjs.logger(TagPluginUtility.class);
		}
		@Override
		public void warning(SAXParseException exception) throws SAXException
		{
			logException(exception);
			throw exception;	
		}
		@Override
		public void error(SAXParseException exception) throws SAXException
		{
			logException(exception);
			throw exception;
		}
		@Override
		public void fatalError(SAXParseException exception) throws SAXException
		{
			logException(exception);
			throw exception;	
		}
		private void logException(SAXException ex) {
			logger.debug("Error while attempting to replace tags for tag handlers; %s", ex.toString());
		}
	}
	
	
	private static class TagMatch {
		String tag;
		Map<String, String> attributes;
		TagMatch(String tag, Map<String,String> attributes) {
			this.tag = tag;
			this.attributes = attributes;
		}
	}
	private interface TagMatchHandler {
		void handleTagMatch(Matcher matcher, TagMatch tagMatch) throws IOException, NoTagHandlerFoundException;
		void handleUnprocessableTagMatch(Matcher matcher, String tagContent);
		void handleMatcherTail(Matcher matcher);
	}
	
}
