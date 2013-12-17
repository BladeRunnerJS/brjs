package org.bladerunnerjs.model.utility;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.List;

import org.bladerunnerjs.utility.EmptyTrieKeyException;
import org.bladerunnerjs.utility.Trie;
import org.bladerunnerjs.utility.TrieKeyAlreadyExistsException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TrieTest
{

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	Trie<TestObject> trie;
	
	TestObject test_object_1;
	TestObject test_object_2;
	TestObject test_object_3;
	TestObject test_object_4;
	TestObject test_object_1_extraStuff;
	
	
	@Before
	public void setup()
	{
		trie = new Trie<TestObject>();
		test_object_1 = new TestObject("test.object.1");
		test_object_2 = new TestObject("test.object.2");
		test_object_3 = new TestObject("test.object.3");
		test_object_4 = new TestObject("test.object.4");
		test_object_1_extraStuff = new TestObject("test.object.1.extraStuff");
	}
	
	@Test
	public void addingToTrie() throws Exception
	{
		trie.add("1234-abc#;;a", test_object_1);
		assertEquals(test_object_1, trie.get("1234-abc#;;a"));
	}
	
	@Test
	public void cantAddEmptyValue() throws Exception
	{
		exception.expect(EmptyTrieKeyException.class);
		trie.add("", test_object_1);
	}
	
	@Test
	public void emptyReaderReturnsEmptyList() throws Exception
	{
		trie.add("test.object.1", test_object_1);
		
		StringReader reader = new StringReader("");
		
		List<TestObject> foundObjects = trie.getMatches(reader);
		assertEquals(0, foundObjects.size());
	}
	
	@Test
	public void correctObjectsReturnedFromUsingReader() throws Exception
	{
		trie.add("test.object.1", test_object_1);
		trie.add("test.object.2", test_object_2);
		trie.add("test.object.3", test_object_3);
		trie.add("test.object.4", test_object_4);
		
		StringReader reader = new StringReader("here is some text, test.object.1 is here too.\n"+
				"and also test.object.2\n"+
				"more stuff. 138t912109\n"+
				"\n"+
				"test.object 3 isnt here, it's not spelt correctly\n"+
				"and finally test.object.4");
		
		List<TestObject> foundObjects = trie.getMatches(reader);
		assertEquals(3, foundObjects.size());
		assertEquals(test_object_1, foundObjects.get(0));
		assertEquals(test_object_2, foundObjects.get(1));
		assertEquals(test_object_4, foundObjects.get(2));
	}
	
	@Test
	public void occurancesWithPrefixDontMatch() throws Exception
	{
		trie.add("test.object.1", test_object_1);
		
		StringReader reader = new StringReader("abcd testtest.object.1 1234");
		
		List<TestObject> foundObjects = trie.getMatches(reader);
		assertEquals(0, foundObjects.size());
	}
	
	@Test
	public void occurancesWithSuffixDontMatch() throws Exception
	{
		trie.add("test.object.1", test_object_1);
		
		StringReader reader = new StringReader("abcd test.object.111 1234");
		
		List<TestObject> foundObjects = trie.getMatches(reader);
		assertEquals(0, foundObjects.size());
	}
	
	@Test
	public void occurancesWithSuffixAndPrefixDontMatch() throws Exception
	{
		trie.add("test.object.1", test_object_1);
		
		StringReader reader = new StringReader("abcd testtest.object.123 1234");
		
		List<TestObject> foundObjects = trie.getMatches(reader);
		assertEquals(0, foundObjects.size());
	}
	
	@Test
	public void matcherIsGreedy() throws Exception
	{
		trie.add("test.object.1", test_object_1);
		trie.add("test.object.1.extraStuff", test_object_1_extraStuff);
		
		StringReader reader = new StringReader("abcd test.object.1.extraStuff 1234");
		
		List<TestObject> foundObjects = trie.getMatches(reader);
		assertEquals(1, foundObjects.size());
		assertEquals(test_object_1_extraStuff, foundObjects.get(0));
	}
	
	@Ignore
	@Test
	public void addingQuotesToATrieShouldntCauseOtherKeysToNotBeMatched() throws Exception
	{
		trie.add("foo", test_object_1);
		trie.add("'bar'", test_object_2);
		
		StringReader reader = new StringReader("'foo'");
		
		List<TestObject> foundObjects = trie.getMatches(reader);
		assertEquals(1, foundObjects.size());
		assertEquals(test_object_1, foundObjects.get(0));
	}
	
	@Test
	public void throwsExceptionIfKeyHasAlreadyBeenAdded() throws Exception
	{
		exception.expect(TrieKeyAlreadyExistsException.class);
		exception.expectMessage("The key 'test.object.1' already exists");
		
		trie.add("test.object.1", test_object_1);
		trie.add("test.object.1", test_object_1_extraStuff);
	}
	
	
	@Test
	public void keysCanHaveNonAlphanumericChars() throws Exception
	{
		trie.add("o.1", test_object_1);
		trie.add("o/2", test_object_2);
		trie.add("o*3", test_object_3);
		trie.add("o_4", test_object_4);
		
		StringReader reader = new StringReader("o.1 o/2 o*3 o_4");
		
		List<TestObject> foundObjects = trie.getMatches(reader);
		assertEquals(4, foundObjects.size());
		assertEquals(test_object_1, foundObjects.get(0));
		assertEquals(test_object_2, foundObjects.get(1));
		assertEquals(test_object_3, foundObjects.get(2));
		assertEquals(test_object_4, foundObjects.get(3));
	}
	
	/* TestObject so the Trie is using Objects to ensure the same object instance is returned, but with a toString() that returns a name to help debugging */
	class TestObject {
		String name;
		public TestObject(String name) { this.name = name; }
		public String toString() { return name; }
	}
	
}
