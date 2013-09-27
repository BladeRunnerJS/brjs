package com.caplin.cutlass.filter.bundlerfilter;

import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;

import com.caplin.cutlass.conf.AppConf;
import com.caplin.cutlass.request.LocaleHelper;

public class BundlerTokenProcessor
{
	private final Pattern tagPattern = Pattern.compile("<@([^@]+)@\\s*/>");
	private final Pattern attributePattern = Pattern.compile("\\s+(.+)\\s*=\\s*\\\"([^\\\"]+)\\\"");
	private final Map<String, TokenProcessor> processors = new HashMap<String, TokenProcessor>();
	
	public void addTokenProcessor(String tokenName, TokenProcessor handler)
	{
		processors.put(tokenName, handler);
	}
	
	public StringBuffer replaceTokens(AppConf appConf, HttpServletRequest request, Reader content) throws TokenProcessorException
	{
		StringWriter writer = new StringWriter();
		
		String locale = "";
		String browser = "";
		try
		{
			locale = LocaleHelper.getLocaleFromRequest(appConf, request);
			browser = BrowserCssHelper.getBrowser(request.getHeader(HttpHeaders.USER_AGENT));
			IOUtils.copy(content, writer); 
		}
		catch (Exception e)
		{
			throw new TokenProcessorException(e);
		}
		
		StringBuffer result = new StringBuffer();
		Matcher matcher = tagPattern.matcher(writer.getBuffer());
		
		while (matcher.find())
		{
			String replacement = parseTokenContent(locale, browser, matcher.group(1).trim(), matcher.group(0));
			if (replacement != null)
			{
				matcher.appendReplacement(result, replacement);
			}
		}
		matcher.appendTail(result);
		
		return result;
	}
	
	private String parseTokenContent(String locale, String browser, String tokenContent, String fullTokenLiteral) throws TokenProcessorException
	{
		String tokenName = tokenContent.split("\\s")[0];
		
		TokenProcessor processor = processors.get(tokenName);
		if (processor == null)
		{
			throw new TokenProcessorException("An error has ocurred when processing token \"" 
					+ fullTokenLiteral + "\": no token processor was found for this token.");
		}
		
		String processedToken = null;
		
		
		try
		{
			processedToken = processor.process(locale, browser, getAttributes(tokenContent));
		}
		catch(TokenProcessorException e)
		{
			throw new TokenProcessorException("An error has ocurred when processing token \"" 
												+ fullTokenLiteral + "\": " + e.getMessage(), e);
		}
		return processedToken;
	}
	
	private Map<String, String> getAttributes(String tokenContent)
	{
		Map<String, String> attributes = new HashMap<String, String>();
		Matcher matcher = attributePattern.matcher(tokenContent);
		while (matcher.find())
		{
			attributes.put(matcher.group(1).trim(), matcher.group(2).trim());
		}
		
		return attributes;
	}
}
