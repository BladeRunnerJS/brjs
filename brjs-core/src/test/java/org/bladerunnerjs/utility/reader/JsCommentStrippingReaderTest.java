package org.bladerunnerjs.utility.reader;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class JsCommentStrippingReaderTest
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
				"// comment 1",
				"// comment 2",
				"line 2"), 
			lines(
				"line 1",
				"",
				"",
				"line 2") 
		);
	}

	@Test
	public void singleLineCommentsCanAppearMidLine() throws IOException
	{
		stripCommentsAndAssertEquals( 
				lines(
					"line 1",
					"line 2 // a comment"), 
				lines(
					"line 1",
					"line 2 ") 
			);
	}

	@Test
	public void midLineCommentsDontEatTheNewLineCharacter() throws IOException
	{
		stripCommentsAndAssertEquals( 
				lines(
					"line 1",
					"line 2 // a comment\n"), 
				lines(
					"line 1",
					"line 2 \n") 
			);
	}

	@Test
	public void singleLineCommentsCanImmediatelyStartWithAForwardSlash() throws IOException
	{
		stripCommentsAndAssertEquals( 
				lines(
					"/// comment"), 
				lines(
					"") 
			);
	}

	@Test
	public void multiLineCommentsAreStripped() throws IOException
	{
		stripCommentsAndAssertEquals( 
				lines(
					"line 1",
					"/*",
					" * comment line 1",
					"* comment line 2",
					" */",
					"line 2"), 
				lines(
					"line 1",
					"",
					"line 2") 
			);
	}

	@Test
	public void jsdocCommentsArePreservedWhenConfigured() throws IOException
	{
		stripCommentsAndAssertEquals( 
				lines(
					"line 1",
					"/**",
					 " * jsdoc line 1",
					 " * jsdoc line 2",
					 " */",
					"line 2"), 
				lines(
					"line 1",
					"/**",
					 " * jsdoc line 1",
					 " * jsdoc line 2",
					 " */",
					"line 2") 
			);
	}

	@Test
	public void jsdocCommentsAreStrippedWhenConfigured() throws IOException
	{
		stripAllCommentsAndAssertEquals( 
				lines(
					"line 1",
					"/**",
					 " * jsdoc line 1",
					 " * jsdoc line 2",
					 " */",
					"line 2"), 
				lines(
					"line 1",
					"",
					"line 2") 
			);
	}

	@Test
	public void singleLineCommentsCanContainAsterisks() throws IOException
	{
		stripCommentsAndAssertEquals( 
			lines(
				"line 1",
				"// *",
				"line 2"), 
			lines(
				"line 1",
				"",
				"line 2") 
		);
	}

	@Test
	public void multiLineCommentsCanContainAsterisksAndSlashes() throws IOException
	{
		stripCommentsAndAssertEquals( 
			lines(
				"line 1",
				"/* * / */",
				"line 2 "),
			lines(
				"line 1",
				"",
				"line 2 ") 
		);
	}

	@Test
	public void singleLineCommentsCanBeEmbeddedInMultiLineComments() throws IOException
	{
		stripCommentsAndAssertEquals( 
			lines(
				"line 1 /* // single-line comment within a multi-line comment */",
				"line 2"),
			lines(
				"line 1 ",
				"line 2") 
		);
	}

	@Test
	public void stringContentsArentInterprettedAsComments() throws IOException
	{
		stripCommentsAndAssertEquals( 
			lines(
				"'// not a comment'",
                "\"// not a comment\"",
                "'/* not a comment */'",
                "\"/* not a comment */\"",
                "// a real comment"),
            lines(
				"'// not a comment'",
                "\"// not a comment\"",
                "'/* not a comment */'",
                "\"/* not a comment */\"",
                "")
		);
	}

	@Test
	public void escapingTheCloseStringCharacterDoesntFoolTheStripper() throws IOException
	{
		stripCommentsAndAssertEquals( 
			lines(
				"'\\'// not a comment'", // double / so the line reads: '\'// not a comment'
				"\"\\\"// not a comment\"", // triple / so the line reads: "\"// not a comment"
				"// a real comment"),
			lines(
				"'\\'// not a comment'", // double / so the line reads: '\'// not a comment'
				"\"\\\"// not a comment\"", // triple / so the line reads: "\"// not a comment"
				"")
		);
	}

	@Test
	public void multiLineSinglyQuotedStringsDontExist() throws IOException
	{
		stripCommentsAndAssertEquals( 
			lines(
				"'start of a string...",
				"// comment"),
            lines(
        		"'start of a string...",
				"")
		);
	}

	@Test
	public void multiLineDoublyQuotedStringsDontExist() throws IOException
	{
		stripCommentsAndAssertEquals( 
			lines(
				"\"start of a string...",
				"// comment"),
            lines(
        		"\"start of a string...",
				"")
		);
	}

	@Test
	public void literalRegularExpressionsEndingWithAnEscapedForwardSlashDoesntFoolTheStripper() throws IOException
	{
		stripCommentsAndAssertEquals( 
			lines(
				"var oRegExp = /match\\//g;"),  // double \ so the line reads: var oRegExp = /match\//g;
            lines(
        		"var oRegExp = /match\\//g;")
		);
	}

	@Test(expected = IOException.class)
	public void macLineEndingsShouldThowException() throws IOException
	{
		stripComments( 	"some content \r" + // \r is the line ending - so this would be 2 separate lines in a file
						"more content", true);
	}

	
	private String lines(String... input)
	{
		return StringUtils.join(input, "\n");
	}
	
	private void stripCommentsAndAssertEquals(String input, String expectedOutput) throws IOException
	{
		assertEquals( expectedOutput, stripComments(input, true) );
	}
	
	private void stripAllCommentsAndAssertEquals(String input, String expectedOutput) throws IOException
	{
		assertEquals( expectedOutput, stripComments(input, false) );
	}
	
	private String stripComments(String input, boolean preserveJsdoc) throws IOException
	{
		try(JsCommentStrippingReader commentStrippingReader = new JsCommentStrippingReader(new StringReader(input), preserveJsdoc);
		    StringWriter stringWriter = new StringWriter())
		{
			IOUtils.copy(commentStrippingReader, stringWriter);

			return stringWriter.toString();
		}
	}
	
}
