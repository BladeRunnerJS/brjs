package org.bladerunnerjs.utility.reader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class JsPostModuleExportsStrippingReaderTest {
	@Test
	public void codeNotContainingAModuleExportsIsCopiedThroughVerbatim() throws IOException {
		stripCommentsAndAssertEquals(
			lines(
				"line1",
				"line2"),
			lines(
				"line1",
				"line2")
		);
	}
	
	@Test
	public void linesSucceedingModuleExportsLineAreDropped() throws IOException {
		stripCommentsAndAssertEquals(
			lines(
				"line1",
				"module.exports = {};",
				"line2"),
			lines(
				"line1",
				"module.exports ")
		);
	}
	
	private String lines(String... input) {
		return StringUtils.join(input, "\n");
	}
	
	private void stripCommentsAndAssertEquals(String input, String expectedOutput) throws IOException {
		assertEquals( expectedOutput, stripComments(input, true) );
	}
	
	private String stripComments(String input, boolean preserveJsdoc) throws IOException {
		try(Reader reader = new JsPostModuleExportsStrippingReader(new StringReader(input), new CharBufferPool());
			StringWriter stringWriter = new StringWriter()) {
			IOUtils.copy(reader, stringWriter);
			return stringWriter.toString();
		}
	}
}
