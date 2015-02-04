package org.bladerunnerjs.utility.reader;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class XmlCommentStrippingDependenciesReaderTest
{

	@Test
	public void sourceWithNoCommentsIsLeftUntouched() throws IOException
	{
		stripCommentsAndAssertEquals( 
			lines(
				"line1",
				"line2" ), 
			lines(
				"line1",
				"line2" ) 
		);
	}

	@Test
	public void singleLineCommentsAreStripped() throws IOException
	{
		stripCommentsAndAssertEquals( 
			lines(
				"line 1",
				"<!-- comment 1 -->",
				"<!-- comment 2 -->",
				"line 2"), 
			lines(
				"line 1",
				"<!--",
				"<!--",
				"line 2") 
		);
	}

	@Test
	public void singleLineCommentsCanAppearMidLine() throws IOException
	{
		stripCommentsAndAssertEquals( 
				lines(
					"line 1",
					"line 2 <!-- a comment -->"), 
				lines(
					"line 1",
					"line 2 <!--") 
			);
	}

	@Test
	public void midLineCommentsDontEatTheNewLineCharacter() throws IOException
	{
		stripCommentsAndAssertEquals( 
				lines(
					"line 1",
					"line 2 <!-- a comment -->\n"), 
				lines(
					"line 1",
					"line 2 <!--\n") 
			);
	}

	@Test
	public void singleLineCommentsCanImmediatelyStartWithALeftAngledBracket() throws IOException
	{
		stripCommentsAndAssertEquals( 
				lines(
					"<!--< comment -->"), 
				lines(
					"<!--") 
			);
	}
	
	@Test
	public void commentsContainingCommentsAreFiltered() throws IOException
	{
		stripCommentsAndAssertEquals( 
				lines(
					"<!--  <!-- comment -->  -->"), 
				lines(
					"<!--  -->") 
			);
	}

	@Test
	public void multiLineCommentsAreStripped() throws IOException
	{
		stripCommentsAndAssertEquals( 
				lines(
					"line 1",
					"<!--",
					" * comment line 1",
					"* comment line 2",
					" -->",
					"line 2"), 
				lines(
					"line 1",
					"<!--",
					"line 2") 
			);
	}

	@Test
	public void multiLineCommentsCanContainComments() throws IOException
	{
		stripCommentsAndAssertEquals( 
				lines(
					"line 1",
					"<!--",
					" <!-- comment line 1",
					"* comment line 2 -->",
					" -->",
					"line 2"), 
				lines(
					"line 1",
					"<!--",
					" -->",
					"line 2") 
			);
	}
	
	@Test
	public void invalidCommentStringsAreLeftUntouched() throws IOException
	{
		stripCommentsAndAssertEquals( 
				lines(
						"<! -- not a comment -->",
						"<!-- a real comment -->"),
				lines(
						"<! -- not a comment -->",
						"<!--")
			);
	}
	
	@Test
	public void largeSourceWithNoCommentsIsLeftUntouched() throws IOException
	{
		stripCommentsAndAssertEquals( 
			lines(
				zeroPad(4090),
				"line1",
				"line2" ), 
			lines(
				zeroPad(4090),
				"line1",
				"line2" ) 
		);
	}
	
	@Test
	public void largeSourceWithCommentsAreStripped() throws IOException
	{
		stripCommentsAndAssertEquals( 
			lines(
				zeroPad(4090),
				"<!-- comment -->",
				"line 2"), 
			lines(
				zeroPad(4090),
				"<!--",
				"line 2") 
		);
	}
	
	
	
	private String zeroPad(int size) {
		return StringUtils.leftPad("", size, '0')+"\n";
	}

	private String lines(String... input)
	{
		return StringUtils.join(input, "\n");
	}
	
	private void stripCommentsAndAssertEquals(String input, String expectedOutput) throws IOException
	{
		assertEquals( expectedOutput, stripComments(input));
	}
	
	private String stripComments(String input) throws IOException
	{
		try(Reader reader = new XmlCommentStrippingDependenciesReader(new StringReader(input)); StringWriter stringWriter = new StringWriter())
		{
			IOUtils.copy(reader, stringWriter);
			return stringWriter.toString();
		}
	}
	
}
