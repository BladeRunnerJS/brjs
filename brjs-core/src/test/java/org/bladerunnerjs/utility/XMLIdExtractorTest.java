package org.bladerunnerjs.utility;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.plugin.plugins.bundlers.xml.XMLIdExtractor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class XMLIdExtractorTest
{
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testSingleQuotedID()
	{
		String input = "<xxx id='testSingleQuotedID'></xxx>";
		List<String> actual = getResults(input);
		List<String> expected = Arrays.asList("testSingleQuotedID");
		assertEquals(actual, expected);
	}
	
	@Test
	public void testDoubleQuotedID()
	{
		String input = "<xxx id=\"testDoubleQuotedID\"></xxx>";
		List<String> actual = getResults(input);
		List<String> expected = Arrays.asList("testDoubleQuotedID");
		assertEquals(actual, expected);
	}
	
	@Test
	public void testNested()
	{
		String input = "<yyy><xxx id='testNested'></xxx></yyy>";
		List<String> actual = getResults(input);
		List<String> expected = Arrays.asList("testNested");
		assertEquals(actual, expected);
	}
	
	@Test
	public void testMulti()
	{
		String input = "<xxx id='testMulti1'></xxx><xxx id='testMulti2'></xxx>";
		List<String> actual = getResults(input);
		List<String> expected = Arrays.asList("testMulti1", "testMulti2");
		assertEquals(actual, expected);
	}
	
	
	private List<String> getResults(String input) {
		XMLIdExtractor extractor = new XMLIdExtractor();
		Reader reader = new StringReader(input);
		List<String> xmlIds = extractor.getXMLIds(reader);
		return xmlIds;
	}
	
	@Test
	public void testNoSpaceBeforeId()
	{
		String input = "<xxx gridid='testSingleQuotedID'></xxx>";
		List<String> actual = getResults(input);
		List<String> expected = Arrays.asList();
		assertEquals(actual, expected);
	}
	
}
