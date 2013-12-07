package org.bladerunnerjs.model.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.model.utility.ContentPathParser;
import org.bladerunnerjs.model.utility.ContentPathParserBuilder;

public class ContentPathParserTest
{
	private ContentPathParserBuilder builder;
	private ContentPathParser parser;
	
	@Before
	public void setUp()
	{
		builder = new ContentPathParserBuilder();
	}
	
	@Test(expected=IllegalStateException.class)
	public void throwsExceptionIfAsNotUsedAfterAccepts() throws MalformedRequestException
	{
		builder.accepts("request");
		parser = builder.build();
	}
	
	@Test(expected=IllegalStateException.class)
	public void throwsExceptionIfAsNotUsedAfterAcceptsAfterAnd() throws MalformedRequestException
	{
		builder.accepts("request").as("content-form").and("request2");
		parser = builder.build();
	}
	
	@Test(expected=IllegalStateException.class)
	public void throwsExceptionIfHasFormNotUsedAfterWhere() throws MalformedRequestException
	{
		builder.accepts("request/<foo>").as("content-form").where("foo");
		parser = builder.build();
	}
	
	@Test(expected=IllegalStateException.class)
	public void throwsExceptionIfHasFormNotUsedAfterWhereAfterAnd() throws MalformedRequestException
	{
		builder.accepts("request/<foo>/<bar>").as("content-form").where("foo").hasForm("the-form").and("bar");
		parser = builder.build();
	}
	
	@Test
	public void simpleRequest() throws MalformedRequestException
	{
		builder.accepts("request").as("content-form");
		parser = builder.build();
		
		ParsedContentPath request = parser.parse("request");
		assertEquals("1a", 0, request.properties.size());
	}
	
	@Test
	public void simpleRequestExceptionHasCorrectDetails()
	{
		builder.accepts("request").as("content-form");
		parser = builder.build();
		
		try
		{
			parser.parse("request-invalid");
			fail("exception expected");
		}
		catch (MalformedRequestException e)
		{
			assertEquals("1a", "request-invalid", e.getRequest());
			assertEquals("1b", 8, e.getCharacterNumber());
		}
		
		try
		{
			parser.parse("invalid-from-start-request");
			fail("exception expected");
		}
		catch (MalformedRequestException e)
		{
			assertEquals("2a", "invalid-from-start-request", e.getRequest());
			assertEquals("2b", 1, e.getCharacterNumber());
		}
	}
	
	@Test
	public void dualOptionRequest() throws MalformedRequestException
	{
		builder.accepts("request-one").as("request-one-form").and("request-two").as("request-two-form");
		parser = builder.build();
		
		ParsedContentPath request = parser.parse("request-one");
		assertEquals("1a", 0, request.properties.size());
		
		request = parser.parse("request-two");
		assertEquals("1a", 0, request.properties.size());
	}
	
	@Test
	public void dualOptionRequestExceptionHasCorrectDetails()
	{
		builder.accepts("request-one").as("request-one-form").and("request-two").as("request-two-form");
		parser = builder.build();
		
		try
		{
			parser.parse("request-three");
			fail("exception expected");
		}
		catch (MalformedRequestException e)
		{
			assertEquals("1a", "request-three", e.getRequest());
			assertEquals("1b", 10, e.getCharacterNumber());
		}
	}
	
	@Test
	public void requestContainingParametersIsProcessedCorrectly() throws MalformedRequestException
	{
		builder.accepts("request/<token1>/<token2>").as("content-form").where("token1").hasForm("[0-9]+").and("token2").hasForm("[a-z]+");
		parser = builder.build();
		
		ParsedContentPath request = parser.parse("request/33/foo");
		assertEquals("1a", 2, request.properties.size());
		assertEquals("1b", "33", request.properties.get("token1"));
		assertEquals("1c", "foo", request.properties.get("token2"));
	}
	
	@Test
	public void incorrectlyTypedParameterRequestsCauseException()
	{
		builder.accepts("request/<token1>/<token2>").as("content-form").where("token1").hasForm("[0-9]+").and("token2").hasForm("[a-z]+");
		parser = builder.build();
		
		try
		{
			parser.parse("request/foo/33");
			fail("exception expected");
		}
		catch (MalformedRequestException e)
		{
			assertEquals("1a", "request/foo/33", e.getRequest());
			assertEquals("1b", 9, e.getCharacterNumber());
		}
	}
	
	@Test(expected=MalformedRequestException.class)
	public void dotsAreEscaped() throws MalformedRequestException
	{
		builder.accepts("a.b").as("XXX");
		parser = builder.build();
		parser.parse("axb");
	}
	
	@Test(expected=MalformedRequestException.class)
	public void questionMarksAreEscaped() throws MalformedRequestException
	{
		builder.accepts("ab?").as("XXX");
		parser = builder.build();
		parser.parse("a");
	}
	
	@Test(expected=MalformedRequestException.class)
	public void wildcardsAreEscaped() throws MalformedRequestException
	{
		builder.accepts("ab*").as("content-form");
		parser = builder.build();
		parser.parse("abb");
	}
	
	@Test
	public void allRegularExpressionCharactersCanBeUsedAsNormalCharacters() throws MalformedRequestException
	{
		builder.accepts(".?*+()[]").as("content-form");
		parser = builder.build();
		parser.parse(".?*+()[]");
	}
	
	@Test
	public void validWindowsCharactersAllowedByNameToken() throws MalformedRequestException
	{
		builder.accepts("<name>").as("XXX").where("name").hasForm(ContentPathParserBuilder.NAME_TOKEN);
		parser = builder.build();
		parser.parse("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890,.!;#�$%^&@~()+-=[]{}'");
	}
	
	@Test
	public void useOfCapturingGroupsInTokensDoesntPreventSubsequentTokensFromBeingRetrieved() throws Exception {
		builder.accepts("<token1>/<token2>").as("content-form")
			.where("token1").hasForm("foo(bar)?").and("token2").hasForm("baz");
		parser = builder.build();
		ParsedContentPath contentPath = parser.parse("foo/baz");
		
		assertEquals("foo", contentPath.properties.get("token1"));
		assertEquals("baz", contentPath.properties.get("token2"));
	}
	
	@Test(expected=MalformedRequestException.class)
	public void forwardSlashNotAllowedByNameToken() throws MalformedRequestException
	{
		builder.accepts("<name>").as("content-form").where("name").hasForm(ContentPathParserBuilder.NAME_TOKEN);
		parser = builder.build();
		parser.parse("/");
	}
	
	// TODO: couldn't figure out how to make a backslash part of a negated character set in java
//	@Test(expected=MalformedBundlerRequestException.class)
//	public void backwardSlashNotAllowedByNameToken() throws MalformedBundlerRequestException
//	{
//		parser.accepts("<name>").where("name").hasForm(RequestParserBuilder.NAME_TOKEN);
//		parser.initialize();
//		parser.parse("\\");
//	}
	
	@Test(expected=MalformedRequestException.class)
	public void colonNotAllowedByNameToken() throws MalformedRequestException
	{
		builder.accepts("<name>").as("content-form").where("name").hasForm(ContentPathParserBuilder.NAME_TOKEN);
		parser = builder.build();
		parser.parse(":");
	}
	
	@Test(expected=MalformedRequestException.class)
	public void wildcardNotAllowedByNameToken() throws MalformedRequestException
	{
		builder.accepts("<name>").as("content-form").where("name").hasForm(ContentPathParserBuilder.NAME_TOKEN);
		parser = builder.build();
		parser.parse("*");
	}
	
	@Test(expected=MalformedRequestException.class)
	public void questionMarkNotAllowedByNameToken() throws MalformedRequestException
	{
		builder.accepts("<name>").as("content-form").where("name").hasForm(ContentPathParserBuilder.NAME_TOKEN);
		parser = builder.build();
		parser.parse("?");
	}
	
	@Test(expected=MalformedRequestException.class)
	public void doubleQuotesNotAllowedByNameToken() throws MalformedRequestException
	{
		builder.accepts("<name>").as("content-form").where("name").hasForm(ContentPathParserBuilder.NAME_TOKEN);
		parser = builder.build();
		parser.parse("\"");
	}
	
	@Test
	public void createRequestWorks() throws Exception
	{
		builder.accepts("request/<token1>/<token2>").as("content-form").where("token1").hasForm("[0-9]+").and("token2").hasForm("[a-z]+");
		parser = builder.build();
		
		assertEquals("request/123/abc", parser.createRequest("content-form", "123", "abc"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createRequestThrowsExceptionWhenNotEnoughArgsAreProvided() throws Exception
	{
		builder.accepts("request/<token>").as("content-form").where("token").hasForm("[a-z]+");
		parser = builder.build();
		
		parser.createRequest("content-form");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createRequestThrowsExceptionWhenTooManyArgsAreProvided() throws Exception
	{
		builder.accepts("request/<token>").as("content-form").where("token").hasForm("[a-z]+");
		parser = builder.build();
		
		parser.createRequest("content-form", "abc", "xyz");
	}
	
	@Test(expected=MalformedTokenException.class)
	public void createRequestWorksThrowsExceptionWhenIncorrectlyTypedArgsAreProvided() throws Exception
	{
		builder.accepts("request/<token>").as("content-form").where("token").hasForm("[a-z]+");
		parser = builder.build();
		
		parser.createRequest("content-form", "123");
	}
}
