package org.bladerunnerjs.utility.reader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


public class JsCodeBlockStrippingReaderTest
{

	@Test
	public void sourceWithNoBracesIfLeftUntouched() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"some content",
				"more content"),
			lines(
				"some content",
				"more content")
		);
	}
	
	@Test
	public void sourceInsideBracesIsRemoved() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"some content",
				"{ more content }"),
			lines(
				"some content",
				"")
		);
	}
	
	@Test
	public void sourceInsidenestedBracesIsRemoved() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"some content",
				"{ abc { more content } }"),
			lines(
				"some content",
				"")
		);
	}
	
	@Test
	public void sourceAfterBracesIsNotRemoved() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"{ code block } some content"),
			lines(
				" some content")
		);
	}
	
	@Test
	public void sourceBeforeBracesIsNotRemoved() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"{ code block } some content"),
			lines(
				" some content")
		);
	}
	
	@Test
	public void codeBlocksSpanningMultipleLinesAreRemoved() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"{",
				"multi",
				"line",
				"code block",
				"}"),
			lines(
				"")
		);
	}
	
	@Test
	public void contentAfterMultipleLineCodeBlocksIsNotRemoved() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"{",
				"multi",
				"line",
				"code block",
				"}",
				"some content"),
			lines(
				"",
				"some content")
		);
	}
	
	@Test
	public void contentBeforeMultipleLineCodeBlocksIsNotRemoved() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"some content",
				"{",
				"multi",
				"line",
				"code block",
				"}"),
			lines(
				"some content",
				"")
		);
	}
	
	@Test
	public void selfExecutingFunctionsAtTheStartOfAClassAreNotStripped() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
				lines(
					"(function() {",
					"some code...",
					"})()"),
				lines(
					"(function() ",
					"some code...",
					")()")
			);
	}
	
	@Test
	public void selfExecutingFunctionsPrefixedByASemicolonAtTheStartOfAClassAreNotStripped() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
				lines(
					";(function() {",
					"some code...",
					"})()"),
				lines(
					"(function() ",
					"some code...",
					")()")
			);
	}
	
	@Test
	public void codeBlocksInSelfExecutingFunctionsAreStripped() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
				lines(
					"(function() {",
					"some code...",
					"function() {",
					"  inner code block...",
					"}",
					")()"),
				lines(
					"(function() ",
					"some code...",
					"function() ",
					")()")
			);
	}
	
	
	private String lines(String... input)
	{
		return StringUtils.join(input, "\n");
	}
	
	private void stripCodeBlocksAndAssertEquals(String input, String expectedOutput) throws IOException
	{
		try(Reader reader = new JsCodeBlockStrippingReader(new StringReader(input));
			    StringWriter stringWriter = new StringWriter())
			{
				IOUtils.copy(reader, stringWriter);
				assertEquals( expectedOutput, stringWriter.toString() );
			}
	}
	
}
