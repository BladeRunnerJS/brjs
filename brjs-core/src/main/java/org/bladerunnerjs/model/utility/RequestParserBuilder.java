package org.bladerunnerjs.model.utility;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bladerunnerjs.model.RequestParser;


public class RequestParserBuilder
{
	private final Map<String, String> requestForms = new LinkedHashMap<>();
	private final Map<String, String> tokens = new HashMap<>();
	private boolean builderIsMidSentence = false;
	
	public static final String NAME_TOKEN = "[^_/:*?\"]+";
	public static final String PATH_TOKEN = "[^:*?\"]+";
	
	public RequestFormNamer accepts(String requestForm)
	{
		builderIsMidSentence = true;
		return new RequestFormAppender(this, requestForms, tokens).and(requestForm);
	}
	
	public TokenValueSetter where(String tokenName)
	{
		builderIsMidSentence = true;
		return new TokenKeyAppender(this, tokens).and(tokenName);
	}
	
	public RequestParser build()
	{
		if(builderIsMidSentence) {
			throw new IllegalStateException("build() invoked while RequestParserBuilder was left mid-sentence");
		}
		
		return new RequestParser(requestForms, tokens);
	}
	
	public class RequestFormAppender
	{
		private final Map<String, String> requestForms;
		private Map<String, String> tokens;
		private final RequestParserBuilder builder;
		
		public RequestFormAppender(RequestParserBuilder builder, Map<String, String> requestForms, Map<String, String> tokens)
		{
			this.builder = builder;
			this.requestForms = requestForms;
			this.tokens = tokens;
		}
		
		public RequestFormNamer and(String requestForm)
		{
			builder.builderIsMidSentence = true;
			return new RequestFormNamer(requestForms, requestForm, this);
		}
		
		public TokenValueSetter where(String tokenName)
		{
			builder.builderIsMidSentence = true;
			return new TokenKeyAppender(builder, tokens).and(tokenName);
		}
	}
	
	public class RequestFormNamer
	{
		private final RequestFormAppender requestFormAppender;
		private final Map<String, String> requestForms;
		private String requestForm;
		
		public RequestFormNamer(Map<String, String> requestForms, String requestForm, RequestFormAppender requestFormNamer)
		{
			this.requestForms = requestForms;
			this.requestForm = requestForm;
			this.requestFormAppender = requestFormNamer;
		}
		
		public RequestFormAppender as(String requestFormName)
		{
			requestFormAppender.builder.builderIsMidSentence = false;
			requestForms.put(requestFormName, requestForm);
			return requestFormAppender;
		}
		
		public void setRequestForm(String requestForm) {
			this.requestForm = requestForm;
		}
	}
	
	public class TokenKeyAppender
	{
		private Map<String, String> tokens;
		private final RequestParserBuilder builder;
		
		public TokenKeyAppender(RequestParserBuilder builder, Map<String, String> tokens)
		{
			this.builder = builder;
			this.tokens = tokens;
		}
		
		public TokenValueSetter and(String tokenKey)
		{
			builder.builderIsMidSentence = true;
			return new TokenValueSetter(this, tokens, tokenKey);
		}
	}
	
	public class TokenValueSetter
	{
		private TokenKeyAppender tokenKeyAppender;
		private Map<String, String> tokens;
		private String key;
		
		public TokenValueSetter(TokenKeyAppender tokenKeyAppender, Map<String, String> tokens, String tokenKey)
		{
			this.tokenKeyAppender = tokenKeyAppender;
			this.tokens = tokens;
			this.key = tokenKey;
		}
		
		public TokenKeyAppender hasForm(String tokenValue)
		{
			tokenKeyAppender.builder.builderIsMidSentence = false;
			tokens.put(key, tokenValue);
			return tokenKeyAppender;
		}
	}
}
