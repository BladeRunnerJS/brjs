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
				"{}")
		);
	}
	
	@Test
	public void sourceInsideNestedBracesIsRemoved() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"some content",
				"{ abc { more content } }"),
			lines(
				"some content",
				"{}")
		);
	}
	
	@Test
	public void sourceAfterBracesIsNotRemoved() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"{ code block } some content"),
			lines(
				"{} some content")
		);
	}
	
	@Test
	public void sourceBeforeBracesIsNotRemoved() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				"{ code block } some content"),
			lines(
				"{} some content")
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
				"{}")
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
				"{}",
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
				"{}")
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
				"(function() {",
				"some code...",
				"})()")
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
				";(function() {",
				"some code...",
				"})()")
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
				")()")
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
				"})()")
			);
	}
	
	@Test
	public void codeBlocksInSelfExecutingFunctionsInTheMiddleOfAClassAreStripped() throws IOException
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
				")()")
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
					"})()")
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
				"}")
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
				")()")
			);
	}
	
	@Test
	public void largeSourceWithCodeBlocksAreStripped() throws IOException
	{
		stripCodeBlocksAndAssertEquals(
			lines(
				zeroPad(4090),
				"some content",
				"{ more content }"),
			lines(
				zeroPad(4090),
				"some content",
				"{}")
		);
	}
	
	@Test
	public void largeSourceWithCodeWithSelfExecutingFunctionsAreNotStripped() throws Exception {
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
				")()")
			);
	}
	
	@Test
	public void functionsInInlineMapsInsideOfASelfExeuctingCodeBlockAreNotStripped() throws Exception {
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
				")()")
			);
	}
	
	@Test
	public void constructorsInInlineMapsInsideOfASelfExeuctingCodeBlockAreNotStripped() throws Exception {
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
				")()")
			);
	}
	
	@Test
	public void requiresInsideInlineMapsAreNotStripped() throws Exception {
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
				")()")
			);
	}
	
	@Test
	public void codeOutsideOfCodeBlocksCanBeOptionalStrippedInstead() throws Exception {
		stripCodeOutsideCodeBlocksAndAssertEquals(
			lines(
					"(function() {",
					"filtered code...",
					"function FOO() {",
					"unfiltered code...",
					"}",
					")()"),
			lines(
					"",
					"unfiltered code...",
					"}")
		);
	}
	
	private String zeroPad(int size) {
		return StringUtils.leftPad("", size, '0')+"\n";
	}
	
	private String lines(String... input)
	{
		return StringUtils.join(input, "\n");
	}
	
	private void stripCodeBlocksAndAssertEquals(String input, String expectedOutput) throws IOException
	{
		CharBufferPool pool = new CharBufferPool();
		stripCodeBlocksAndAssertEquals(input, expectedOutput, new JsCodeBlockStrippingDependenciesReader(new StringReader(input), pool));
	}
	
	private void stripCodeOutsideCodeBlocksAndAssertEquals(String input, String expectedOutput) throws IOException
	{
		CharBufferPool pool = new CharBufferPool();
		stripCodeBlocksAndAssertEquals(input, expectedOutput, new JsCodeBlockStrippingDependenciesReader(new StringReader(input), pool, new JsCodeBlockStrippingDependenciesReader.MoreThanPredicate(0)));
	}
	
	private void stripCodeBlocksAndAssertEquals(String input, String expectedOutput, Reader reader) throws IOException {
		StringWriter stringWriter = new StringWriter();
		IOUtils.copy(reader, stringWriter);
		assertEquals( expectedOutput, stringWriter.toString() );
	}
	
	
}
