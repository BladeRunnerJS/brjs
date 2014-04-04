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
