package org.bladerunnerjs.utility.reader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


public class JsCodeBlockStrippingDependenciesReaderTest
{

	@Test
	public void sourceWithNoBraces() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"some content",
				"more content"),
			lines(
				"some content",
				"more content"),
			lines(
				"")
		);
	}
	
	@Test
	public void sourceCodeInsideBraces() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"some content",
				"{ more content }"),
			lines(
				"some content",
				"{}"),
    		lines(
				" more content }")
		);
	}
	
	@Test
	public void sourceInsideNestedBraces() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"some content",
				"{ abc { more content } }"),
			lines(
				"some content",
				"{}"),
			lines(
				" abc { more content } }")
		);
	}
	
	@Test
	public void sourceInsideNestedBracesWithCodeAfter() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"some content",
				"{ abc { more content } } yet more content"),
			lines(
				"some content",
				"{} yet more content"),
			lines(
				" abc { more content } }")
		);
	}
	
	
	@Test
	public void sourceAfterBraces() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"{ code block } some content"),
			lines(
				"{} some content"),
			lines(
				" code block }")
		);
	}
	
	@Test
	public void sourceBeforeBraces() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"{ code block } some content"),
			lines(
				"{} some content"),
			lines(
				" code block }")
		);
	}
	
	@Test
	public void codeBlocksSpanningMultipleLines() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"{",
				"multi",
				"line",
				"code block",
				"}"),
			lines(
				"{}"),
			lines(
				"",
				"multi",
				"line",
				"code block",
				"}")
		);
	}
	
	@Test
	public void contentAfterMultipleLineCodeBlock() throws IOException
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
				"{}",
				"some content"),
			lines(
				"",
				"multi",
				"line",
				"code block",
				"}")
		);
	}
	
	@Test
	public void contentBeforeMultipleLineCodeBlocks() throws IOException
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
				"{}"),
			lines(
				"",
				"multi",
				"line",
				"code block",
				"}")
		);
	}
	
	@Test
	public void selfExecutingFunctionsAtTheStartOfAClass() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"(function() {",
				"some code...",
				"})()"),
			lines(
				"(function() {",
				"some code...",
				"})()"),
			lines(
				"")
		);
	}
	
	@Test
	public void selfExecutingFunctionsPrefixedByASemicolonAtTheStartOfAClass() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				";(function() {",
				"some code...",
				"})()"),
			lines(
				";(function() {",
				"some code...",
				"})()"),
			lines(
				"")
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
				"(function() {",
				"some code...",
				"function() {}",
				")()"),
			lines(
				"",
				"  inner code block...",
				"}")
		);
	}
	
	@Test
	public void selfExecutingFunctionsCanAppearAnywhereInTheClass() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"some code...",
				"(function() {",
				"some more code...",
				"})()"),
			lines(
				"some code...",
				"(function() {",
				"some more code...",
				"})()"),
			lines(
				"")
			);
	}
	
	@Test
	public void codeBlocksInSelfExecutingFunctionsInTheMiddleOfAClass() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"some code...",
				"(function() {",
				"some more code...",
				"function() {",
				"  inner code block...",
				"}",
				")()"),
			lines(
				"some code...",
				"(function() {",
				"some more code...",
				"function() {}",
				")()"),
			lines(
				"",
				"  inner code block...",
				"}")
		);
	}
	
	@Test
	public void selfExecutingFunctionsCanHaveArguments() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
				lines(
					"(function(arg1, arg2) {",
					"some code...",
					"})()",
					"some more code...",
					"(function(arg1,arg3) {",
					"yet more code...",
					"})()"),
				lines(
					"(function(arg1, arg2) {",
					"some code...",
					"})()",
					"some more code...",
					"(function(arg1,arg3) {",
					"yet more code...",
					"})()"),
				lines(
    				"")
			);
	}
	
	@Test
	public void alternativeSelfExecutingFunctionFormatsAreSupported() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"new function() {",
				"some code...",
				"}"),
			lines(
				"new function() {",
				"some code...",
				"}"),
			lines(
				"")
			);
	}
	
	@Test
	public void selfExecutingFunctionsCanBeImmediatelyWithinAnotherSelefExecutingFunction() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"(function() {",
				"(function() {",
				"some code...",
				"})()",
				")()"),
			lines(
				"(function() {",
				"(function() {",
				"some code...",
				"})()",
				")()"),
			lines(
				"")
			);
	}
	
	@Test
	public void largeSourceWithCodeBlocks() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				zeroPad(4090),
				"some content",
				"{ more content }"),
			lines(
				zeroPad(4090),
				"some content",
				"{}"),
			lines(
				" more content }")
		);
	}
	
	@Test
	public void largeSourceWithCodeWithSelfExecutingFunctions() throws Exception {
		stripCodeBlocksAndAssertEquals(
			lines(
				zeroPad(4090),
				"(function() {",
				"(function() {",
				"some code...",
				"})()",
				")()"),
			lines(
				zeroPad(4090),
				"(function() {",
				"(function() {",
				"some code...",
				"})()",
				")()"),
			lines(
				"")
			);
	}
	
	@Test
	public void functionsInInlineMapsInsideOfASelfExeuctingCodeBlock() throws Exception {
		stripCodeBlocksAndAssertEquals(
			lines(
				"(function() {",
				"var someMap = {",
				" key: some.function()",
				"}",
				")()"),
			lines(
				"(function() {",
				"var someMap = {",
				" key: some.function()",
				"}",
				")()"),
			lines(
				"")
			);
	}
	
	@Test
	public void constructorsInInlineMapsInsideOfASelfExeuctingCodeBlock() throws Exception {
		stripCodeBlocksAndAssertEquals(
			lines(
				"(function() {",
				"var someMap = {",
				" key: new some.Class()",
				" key: new (some.Class())",
				"}",
				")()"),
			lines(
				"(function() {",
				"var someMap = {",
				" key: new some.Class()",
				" key: new (some.Class())",
				"}",
				")()"),
			lines(
				"")
			);
	}
	
	@Test
	public void requiresInsideInlineMaps() throws Exception {
		stripCodeBlocksAndAssertEquals(
			lines(
				"(function() {",
				"var someMap = {",
				" key: require('package/subpkg/Class')",
				" key: require('package/subpkg/Class').function()",
				"}",
				")()"),
			lines(
				"(function() {",
				"var someMap = {",
				" key: require('package/subpkg/Class')",
				" key: require('package/subpkg/Class').function()",
				"}",
				")()"),
			lines(
				"")
			);
	}
	
	@Test
	public void sourceInsideManyNestedBracesDoesntCauseIndexOutOfBounds() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				StringUtils.leftPad("", 4090, '{'),
				"content",
				StringUtils.leftPad("", 4090, '}')),
			lines(
				"{}"),
			lines(
				StringUtils.leftPad("", 4089, '{'), // 4089 is correct - the first { is swallowed by the reader
				"content",
				StringUtils.leftPad("", 4090, '}'))
		);
	}
	
	
	
	private String zeroPad(int size) {
		return StringUtils.leftPad("", size, '0')+"\n";
	}
	
	private String lines(String... input)
	{
		return StringUtils.join(input, "\n");
	}
	
	private void stripCodeBlocksAndAssertEquals(String input, String expectedInsideCodeBlocksOutput, String expectedOutsideCodeBlocksOutput) throws IOException {
		CharBufferPool pool = new CharBufferPool();
		stripCodeBlocksAndAssertEquals("Got an incorrect value for outside code block filtering", input, expectedInsideCodeBlocksOutput, new JsCodeBlockStrippingDependenciesReader(new StringReader(input), pool));
		stripCodeBlocksAndAssertEquals("Got an incorrect value for inside code block filtering", input, expectedOutsideCodeBlocksOutput, new JsCodeBlockStrippingDependenciesReader(new StringReader(input), pool, new JsCodeBlockStrippingDependenciesReader.MoreThanPredicate(0)));
	}
	
	private void stripCodeBlocksAndAssertEquals(String failMessage, String input, String expectedOutput, Reader reader) throws IOException {
		StringWriter stringWriter = new StringWriter();
		IOUtils.copy(reader, stringWriter);
		assertEquals( failMessage, expectedOutput, stringWriter.toString() );
	}
	
	
}
