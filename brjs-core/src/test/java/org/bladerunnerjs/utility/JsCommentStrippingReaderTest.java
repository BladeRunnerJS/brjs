package org.bladerunnerjs.utility;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.utility.JsCommentStrippingReader;
import org.junit.Test;

public class JsCommentStrippingReaderTest
{

	private static final String TEST_ROOT = "src/test/resources/JsCommentStrippingReaderTest";

	@Test
	public void sourceWithNoCommentsIsLeftUntouched() throws IOException
	{
		FileAccessor accessor = new FileAccessor("source-with-no-comments-is-left-untouched");

		assertEquals(accessor.get("input-and-output.js"), stripComments(accessor.getFile("input-and-output.js")));
	}

	@Test
	public void singleLineCommentsAreStripped() throws IOException
	{
		FileAccessor accessor = new FileAccessor("single-line-comments-are-stripped");

		assertEquals(accessor.get("output.js"), stripComments(accessor.getFile("input.js")));
	}

	@Test
	public void singleLineCommentsCanAppearMidLine() throws IOException
	{
		FileAccessor accessor = new FileAccessor("single-line-comments-can-appear-mid-line");

		assertEquals(accessor.get("output.js"), stripComments(accessor.getFile("input.js")));
	}

	@Test
	public void midLineCommentsDontEatTheNewLineCharacter() throws IOException
	{
		FileAccessor accessor = new FileAccessor("mid-line-comment-dont-eat-the-new-line-character");

		assertEquals(accessor.get("output.js"), stripComments(accessor.getFile("input.js")));
	}

	@Test
	public void singleLineCommentsCanImmediatelyStartWithAForwardSlash() throws IOException
	{
		FileAccessor accessor = new FileAccessor("single-line-comments-can-immediately-start-with-a-forward-slash");

		assertEquals(accessor.get("output.js"), stripComments(accessor.getFile("input.js")));
	}

	@Test
	public void multiLineCommentsAreStripped() throws IOException
	{
		FileAccessor accessor = new FileAccessor("multi-line-comments-are-stripped");

		assertEquals(accessor.get("output.js"), stripComments(accessor.getFile("input.js")));
	}

	@Test
	public void jsdocCommentsArePreservedWhenConfigured() throws IOException
	{
		FileAccessor accessor = new FileAccessor("jsdoc-comments-are-preserved-when-configured");

		assertEquals(accessor.get("input-and-output.js"), stripComments(accessor.getFile("input-and-output.js")));
	}

	@Test
	public void jsdocCommentsAreStrippedWhenConfigured() throws IOException
	{
		FileAccessor accessor = new FileAccessor("jsdoc-comments-are-stripped-when-configured");

		assertEquals(accessor.get("output.js"), stripAllComments(accessor.getFile("input.js")));
	}

	@Test
	public void singleLineCommentsCanContainAsterisks() throws IOException
	{
		FileAccessor accessor = new FileAccessor("single-line-comments-can-contain-asterisks");

		assertEquals(accessor.get("output.js"), stripComments(accessor.getFile("input.js")));
	}

	@Test
	public void multiLineCommentsCanContainAsterisksAndSlashes() throws IOException
	{
		FileAccessor accessor = new FileAccessor("multi-line-comments-can-contain-asterisks-and-slashes");

		assertEquals(accessor.get("output.js"), stripComments(accessor.getFile("input.js")));
	}

	@Test
	public void singleLineCommentsCanBeEmbeddedInMultiLineComments() throws IOException
	{
		FileAccessor accessor = new FileAccessor("single-line-comments-can-be-embedded-in-multi-line-comments");

		assertEquals(accessor.get("output.js"), stripComments(accessor.getFile("input.js")));
	}

	@Test
	public void stringContentsArentInterprettedAsComments() throws IOException
	{
		FileAccessor accessor = new FileAccessor("string-contents-arent-interpretted-as-comments");

		assertEquals(accessor.get("output.js"), stripComments(accessor.getFile("input.js")));
	}

	@Test
	public void escapingTheCloseStringCharacterDoesntFoolTheStripper() throws IOException
	{
		FileAccessor accessor = new FileAccessor("escaping-the-close-string-character-doesnt-fool-the-stripper");

		assertEquals(accessor.get("output.js"), stripComments(accessor.getFile("input.js")));
	}

	@Test
	public void multiLineSinglyQuotedStringsDontExist() throws IOException
	{
		FileAccessor accessor = new FileAccessor("multi-line-singly-quoted-strings-dont-exist");

		assertEquals(accessor.get("output.js"), stripComments(accessor.getFile("input.js")));
	}

	@Test
	public void multiLineDoublyQuotedStringsDontExist() throws IOException
	{
		FileAccessor accessor = new FileAccessor("multi-line-doubly-quoted-strings-dont-exist");

		assertEquals(accessor.get("output.js"), stripComments(accessor.getFile("input.js")));
	}

	@Test
	public void literalRegularExpressionsEndingWithAnEscapedForwardSlashDoesntFoolTheStripper() throws IOException
	{
		FileAccessor accessor = new FileAccessor("literal-regular-expressions-ending-with-an-escaped-forward-slash-doesnt-fool-the-stripper");

		assertEquals(accessor.get("input-and-output.js"), stripComments(accessor.getFile("input-and-output.js")));
	}

	@Test(expected = IOException.class)
	public void macLineEndingsShouldThowException() throws IOException
	{
		FileAccessor accessor = new FileAccessor("mac-line-endings-should-throw-runtime-exception");
		stripComments(accessor.getFile("input.js"));
	}

	private String stripComments(File source) throws IOException
	{
		return stripComments(source, true);
	}

	private String stripAllComments(File source) throws IOException
	{
		return stripComments(source, false);
	}

	private String stripComments(File source, boolean preserveJsdoc) throws IOException
	{
		try(JsCommentStrippingReader commentStrippingReader = new JsCommentStrippingReader(new FileReader(source), preserveJsdoc);
		    StringWriter stringWriter = new StringWriter())
		{
			IOUtils.copy(commentStrippingReader, stringWriter);

			return stringWriter.toString();
		}
	}

	private class FileAccessor
	{
		private File testResourceDir;

		public FileAccessor(String testName)
		{
			testResourceDir = new File(TEST_ROOT, testName);
		}

		public String get(String fileName) throws IOException
		{
			return FileUtils.readFileToString(this.getFile(fileName), String.valueOf(Charset.defaultCharset()));
		}

		public File getFile(String fileName) throws IOException
		{
			return new File(testResourceDir, fileName);
		}
	}
}
