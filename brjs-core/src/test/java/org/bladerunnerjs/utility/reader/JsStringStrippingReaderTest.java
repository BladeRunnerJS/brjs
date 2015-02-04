package org.bladerunnerjs.utility.reader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


public class JsStringStrippingReaderTest
{

	@Test
	public void sourceWithNoStringsIfLeftUntouched() throws IOException
	{
		stripStringsAndAssertEquals(
			lines(
				"some content",
				"more content"),
			lines(
				"some content",
				"more content")
		);
	}
	
	/* double quout scenarios */
	
	@Test
	public void sourceWithDoubleQuotedStringsIsRemoved() throws IOException
	{
		stripStringsAndAssertEquals(
			lines(
				"not a string",
				"\"is a string\""),
			lines(
				"not a string",
				"")
		);
	}
	
	@Test
	public void doubleQuotedStringContainingASingleQuotedStringIsCompletelyRemoved() throws IOException
	{
		stripStringsAndAssertEquals(
			lines(
				"\"is a 'quoted' string\""),
			lines(
				"")
		);
	}

	@Test
	public void sourceAfterADoublyQuotedStringsIsNotRemoved() throws IOException
	{
		stripStringsAndAssertEquals(
			lines(
				"\"is a string\" more non string stuff"),
			lines(
				" more non string stuff")
		);
	}
	
	@Test
	public void sourceBeforeADoublyQuotedStringsIsNotRemoved() throws IOException
	{
		stripStringsAndAssertEquals(
			lines(
				"non string stuff \"is a string\""),
			lines(
				"non string stuff ")
		);
	}
	
	@Test
	public void doubleQuotedStringBlocksAreTerminatedOnANewline() throws IOException
	{
		stripStringsAndAssertEquals(
			lines(
				"\"unclosed string...",
				"new content"),
			lines(
				"new content")
		);
	}
	
	
	/* single quout scenarios */
	
	@Test
	public void sourceWithSingleQuotedStringsIsRemoved() throws IOException
	{
		stripStringsAndAssertEquals(
			lines(
				"not a string",
				"'is a string'"),
			lines(
				"not a string",
				"")
		);
	}
	
	@Test
	public void sourceAfterASinglyQuotedStringsIsNotRemoved() throws IOException
	{
		stripStringsAndAssertEquals(
			lines(
				"'is a string' more non string stuff"),
			lines(
				" more non string stuff")
		);
	}
	
	@Test
	public void sourceBeforeASinglyQuotedStringsIsNotRemoved() throws IOException
	{
		stripStringsAndAssertEquals(
			lines(
				"non string stuff \"is a string\""),
			lines(
				"non string stuff ")
		);
	}
	
	@Test
	public void singleQuotedStringContainingADoubleQuotedStringIsCompletelyRemoved() throws IOException
	{
		stripStringsAndAssertEquals(
			lines(
				"'is a \"quoted\" string'"),
			lines(
				"")
		);
	}
	
	@Test
	public void singleeQuotedStringBlocksAreTerminatedOnANewline() throws IOException
	{
		stripStringsAndAssertEquals(
			lines(
				"'unclosed string...",
				"new content"),
			lines(
				"new content")
		);
	}
	
	@Test
	public void largeSourceWithStringsAreStripped() throws IOException
	{
		stripStringsAndAssertEquals(
				lines(
					zeroPad(4090),
					"'unclosed string...",
					"new content"),
				lines(
					zeroPad(4090),
					"new content")
			);
	}
	
	
	
	private String zeroPad(int size) {
		return StringUtils.leftPad("", size, '0')+"\n";
	}
	
	private String lines(String... input)
	{
		return StringUtils.join(input, "\n");
	}
	
	private void stripStringsAndAssertEquals(String input, String expectedOutput) throws IOException
	{
		try(Reader reader = new JsStringStrippingReader(new StringReader(input));
			    StringWriter stringWriter = new StringWriter())
			{
				IOUtils.copy(reader, stringWriter);
				assertEquals( expectedOutput, stringWriter.toString() );
			}
	}
	
}
