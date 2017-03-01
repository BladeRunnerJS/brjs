package org.bladerunnerjs.utility.reader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.junit.Test;

public class JsModuleExportsStrippingReaderTest {
	@Test
	public void codeNotContainingAModuleExportsIsCopiedThroughVerbatim() throws IOException {
		stripAfterModuleExportsAndAssertEquals(
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
		stripAfterModuleExportsAndAssertEquals(
			lines(
				"line1",
				"module.exports = {};",
				"line2"),
			lines(
				"line1",
				"module.exports ")
		);
	}
	
	@Test
	public void linesSucceedingModuleExportsLineAreDroppedWhenSpacesAreOmmitted() throws IOException {
		stripAfterModuleExportsAndAssertEquals(
			lines(
				"line1",
				"module.exports={};",
				"line2"),
			lines(
				"line1",
				"module.exports")
		);
	}
	
	@Test
	public void linesSucceedingExportsLineAreDropped() throws IOException {
		stripAfterModuleExportsAndAssertEquals(
			lines(
				"line1",
				"exports = {};",
				"line2"),
			lines(
				"line1",
				"exports ")
		);
	}
	
	@Test
	public void linesSucceedingExportsLineAreDroppedWhenSpacesAreOmmitted() throws IOException {
		stripAfterModuleExportsAndAssertEquals(
			lines(
				"line1",
				"exports={};",
				"line2"),
			lines(
				"line1",
				"exports")
		);
	}
	
	@Test
	public void linesPreceedingModuleExportsCanBeDroppedInstead() throws IOException {
		stripBeforeModuleExportsAndAssertEquals(
			lines(
				"line1",
				"module.exports = {};",
				"line2"),
			lines(
				"= {};",
				"line2")
		);
	}
	
	@Test
	public void linesPreceedingExportsCanBeDroppedInstead() throws IOException {
		stripBeforeModuleExportsAndAssertEquals(
			lines(
				"line1",
				"exports = {};",
				"line2"),
			lines(
				"= {};",
				"line2")
		);
	}
	
	private String lines(String... input) {
		return StringUtils.join(input, "\n");
	}
	
	private void stripAfterModuleExportsAndAssertEquals(String input, String expectedOutput) throws IOException {
		stripModuleExportsAndAssertEquals( input, expectedOutput, new JsModuleExportsStrippingReader(mockBRJSAndNodeProperties(), new StringReader(input)) );
	}
	
	private void stripBeforeModuleExportsAndAssertEquals(String input, String expectedOutput) throws IOException { 
		stripModuleExportsAndAssertEquals( input, expectedOutput, new JsModuleExportsStrippingReader(mockBRJSAndNodeProperties(), new StringReader(input), false) );
	}
	
	private BRJS mockBRJSAndNodeProperties() {
		NodeProperties mockNodeProperties = mock(NodeProperties.class);
		BRJS brjs = mock(BRJS.class);
		when(brjs.nodeProperties(anyString())).thenReturn(mockNodeProperties);
		when(mockNodeProperties.getTransientProperty(anyString())).thenReturn(new CharBufferPool());
		return brjs;
	}
	
	private void stripModuleExportsAndAssertEquals(String input, String expectedOutput, Reader reader) throws IOException {
		StringWriter stringWriter = new StringWriter();
		IOUtils.copy(reader, stringWriter);
		assertEquals( expectedOutput, stringWriter.toString() );
	}
}
