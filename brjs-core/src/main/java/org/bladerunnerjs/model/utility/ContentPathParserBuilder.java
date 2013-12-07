package org.bladerunnerjs.model.utility;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class ContentPathParserBuilder
{
	private final Map<String, String> contentForms = new LinkedHashMap<>();
	private final Map<String, String> tokens = new HashMap<>();
	private boolean builderIsMidSentence = false;
	
	public static final String NAME_TOKEN = "[^_/:*?\"]+";
	public static final String PATH_TOKEN = "[^:*?\"]+";
	
	public RequestFormNamer accepts(String contentForm)
	{
		builderIsMidSentence = true;
		return new ContentFormAppender(this, contentForms, tokens).and(contentForm);
	}
	
	public TokenValueSetter where(String tokenName)
	{
		builderIsMidSentence = true;
		return new TokenKeyAppender(this, tokens).and(tokenName);
	}
	
	public ContentPathParser build()
	{
		if(builderIsMidSentence) {
			throw new IllegalStateException("build() invoked while RequestParserBuilder was left mid-sentence");
		}
		
		return new ContentPathParser(contentForms, tokens);
	}
	
	public class ContentFormAppender
	{
		private final Map<String, String> contentForms;
		private Map<String, String> tokens;
		private final ContentPathParserBuilder builder;
		
		public ContentFormAppender(ContentPathParserBuilder builder, Map<String, String> contentForms, Map<String, String> tokens)
		{
			this.builder = builder;
			this.contentForms = contentForms;
			this.tokens = tokens;
		}
		
		public RequestFormNamer and(String contentForm)
		{
			builder.builderIsMidSentence = true;
			return new RequestFormNamer(contentForms, contentForm, this);
		}
		
		public TokenValueSetter where(String tokenName)
		{
			builder.builderIsMidSentence = true;
			return new TokenKeyAppender(builder, tokens).and(tokenName);
		}
	}
	
	public class RequestFormNamer
	{
		private final ContentFormAppender contentFormAppender;
		private final Map<String, String> contentForms;
		private String contentForm;
		
		public RequestFormNamer(Map<String, String> contentForms, String contentForm, ContentFormAppender contentFormNamer)
		{
			this.contentForms = contentForms;
			this.contentForm = contentForm;
			this.contentFormAppender = contentFormNamer;
		}
		
		public ContentFormAppender as(String contentFormName)
		{
			contentFormAppender.builder.builderIsMidSentence = false;
			contentForms.put(contentFormName, contentForm);
			return contentFormAppender;
		}
		
		public void setRequestForm(String contentForm) {
			this.contentForm = contentForm;
		}
	}
	
	public class TokenKeyAppender
	{
		private Map<String, String> tokens;
		private final ContentPathParserBuilder builder;
		
		public TokenKeyAppender(ContentPathParserBuilder builder, Map<String, String> tokens)
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
