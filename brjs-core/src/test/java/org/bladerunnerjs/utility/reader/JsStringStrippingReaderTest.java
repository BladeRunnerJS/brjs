package org.bladerunnerjs.utility.reader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
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
	
	private String lines(String... input)
	{
		return StringUtils.join(input, "\n");
	}
	
	private void stripStringsAndAssertEquals(String input, String expectedOutput) throws IOException
	{
		try(JsStringStrippingReader commentStrippingReader = new JsStringStrippingReader(new StringReader(input));
			    StringWriter stringWriter = new StringWriter())
			{
				IOUtils.copy(commentStrippingReader, stringWriter);
				assertEquals( expectedOutput, stringWriter.toString() );
			}
	}
	
}
