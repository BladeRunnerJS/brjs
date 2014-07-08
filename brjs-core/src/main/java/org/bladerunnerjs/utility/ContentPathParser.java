package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.eclipse.jetty.util.URIUtil;


public class ContentPathParser
{
	private final Map<String, String> requestForms;
	private final Map<String, Pattern> tokens;
	private final Map<String, Pattern> requestFormPatterns;
	private final Map<String, List<String>> requestFormTokens;
	
	public ContentPathParser(Map<String, String> requestForms, Map<String, String> tokens)
	{
		this.requestForms = requestForms;
		this.tokens = generateTokenPatterns(tokens);
		this.requestFormPatterns = generateRequestFormPatterns(requestForms, tokens);
		this.requestFormTokens = generateRequestFormTokens(requestForms);
	}
	
	public List<String> getRequestForms()
	{
		return new ArrayList<>(requestForms.values());
	}
	
	public String createRequest(String requestFormName, String... args) throws MalformedTokenException
	{
		String requestForm = requestForms.get(requestFormName);
		List<String> tokens = requestFormTokens.get(requestFormName);
		
		if (tokens == null) throw new IllegalArgumentException("request form name, "+requestFormName+", hasn't been registed");
		if(args.length != tokens.size()) throw new IllegalArgumentException("wrong number of arguments provided");
		
		int i = 0;
		for(String arg : args)
		{
			String token = tokens.get(i++);
			
			validateRequestToken(token, arg);
			
			// use StringUtils so we dont do a regex replace incase 'arg' contains $ which causes it to fail
			requestForm = StringUtils.replace(requestForm, "<" + token + ">", arg);
		}
		
		return requestForm;
	}
	
	public boolean canParseRequest(String requestPath) {
		try
		{
			parse(requestPath);
			return true;
		}
		catch (MalformedRequestException | IndexOutOfBoundsException e)
		{
			return false;
		}
	}
	
	public ParsedContentPath parse(String request) throws MalformedRequestException
	{
		int lastMatchPos = 0;
		
		for (String requestFormName : requestFormPatterns.keySet())
		{
			Pattern requestFormPattern = requestFormPatterns.get(requestFormName);
			Matcher requestMatcher = requestFormPattern.matcher(request);

			if(requestMatcher.lookingAt())
			{
				if ((requestMatcher.start() == 0) && (requestMatcher.end() == request.length()))
				{
					ParsedContentPath contentPath = new ParsedContentPath(requestFormName);
					List<String> tokens = requestFormTokens.get(requestFormName);

					for (int gi = 0; gi < requestMatcher.groupCount() && gi < tokens.size(); ++gi)
					{
						String property = requestMatcher.group(gi + 1);
						try {
							String decodedProperty = URIUtil.decodePath(property);
							contentPath.properties.put(tokens.get(gi), decodedProperty);
						} catch (Exception ex) {
							/* TODO: investigate whether we need this - if the URL fails to decode should we really catch the exception? */ 
							contentPath.properties.put(tokens.get(gi), property);							
						}
					}

					return contentPath;
				}
				else if ((requestMatcher.start() == 0) && (requestMatcher.end() > lastMatchPos))
				{
					lastMatchPos = requestMatcher.end();
				}
				else
				{
				}
			}
			else
			{
				int requestLastMatchPos = getLastMatchPos(request, requestMatcher);
				
				if(requestLastMatchPos > lastMatchPos)
				{
					lastMatchPos = requestLastMatchPos;
				}
			}
		}

		MalformedRequestException ex = new MalformedRequestException(request, "Request did not match " + requestFormPatterns);
		ex.setCharacterNumber(lastMatchPos + 1);
		throw ex;
	}
	
	private int getLastMatchPos(String request, Matcher requestMatcher)
	{
		int endRegion = request.length() - 1;
		
		do
		{
			requestMatcher.region(0, endRegion);
			
			if(requestMatcher.lookingAt() || requestMatcher.hitEnd())
			{
				return endRegion;
			}
		}
		while (--endRegion > 0);
		
		return 0;
	}
	
	private String convertRequestFormToPattern(String requestForm) {
		return requestForm.replaceAll("([.?*+()\\[\\]])", "\\\\$1");
	}
	
	private String convertTokenToPattern(String token) {
		return token.replaceAll("\\(", "(?:");
	}
	
	private Map<String, Pattern> generateTokenPatterns(Map<String, String> tokens) {
		Map<String, Pattern> tokenPatterns = new HashMap<>();
		
		for(String tokenName : tokens.keySet()) {
			String tokenPattern = tokens.get(tokenName);
			tokenPatterns.put(tokenName, Pattern.compile(tokenPattern));
		}
		
		return tokenPatterns;
	}
	
	private Map<String, Pattern> generateRequestFormPatterns(Map<String, String> requestForms, Map<String, String> tokens)
	{
		Map<String, Pattern> requestFormPatterns = new LinkedHashMap<>();
		
		for (String requestFormName : requestForms.keySet())
		{
			String requestForm = requestForms.get(requestFormName);
			String tokenizedRequestForm = convertRequestFormToPattern(requestForm);
			
			for (String token : tokens.keySet())
			{
				tokenizedRequestForm = tokenizedRequestForm.replaceAll("<" + token + ">", "(" + convertTokenToPattern(tokens.get(token)) + ")");
			}
			
			requestFormPatterns.put(requestFormName, Pattern.compile(tokenizedRequestForm));
		}
		
		return requestFormPatterns;
	}
	
	private Map<String, List<String>> generateRequestFormTokens(Map<String, String> requestForms)
	{
		Map<String, List<String>> requestFormTokens = new HashMap<>();
		Pattern tokenPattern = Pattern.compile("<([^>]+)>");
		
		for (String requestFormName : requestForms.keySet())
		{
			String requestForm = requestForms.get(requestFormName);
			List<String> tokens = new ArrayList<String>();
			Matcher tokenMatcher = tokenPattern.matcher(requestForm);
			
			while (tokenMatcher.find())
			{
				tokens.add(tokenMatcher.group(1));
			}
			
			requestFormTokens.put(requestFormName, tokens);
		}
		
		return requestFormTokens;
	}
	
	private void validateRequestToken(String tokenName, String tokenValue) throws MalformedTokenException {
		Pattern tokenPattern = tokens.get(tokenName);
		Matcher tokenMatcher = tokenPattern.matcher(tokenValue);
		
		if(!tokenMatcher.matches()) {
			// TODO: we need a test for this
			throw new MalformedTokenException(tokenName, tokenValue, tokenPattern);
		}
	}
}
