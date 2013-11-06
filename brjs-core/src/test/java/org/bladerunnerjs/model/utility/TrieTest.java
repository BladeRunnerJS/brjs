package org.bladerunnerjs.model.utility;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class TrieTest
{

	Trie<String> trie;
	
	String test_object_1;
	String test_object_2;
	String test_object_3;
	String test_object_4;
	
	
	@Before
	public void setup()
	{
		trie = new Trie<String>();
		test_object_1 = "test_object_1";
		test_object_2 = "test_object_2";
		test_object_3 = "test_object_3";
		test_object_4 = "test_object_4";
	}
	
	@Test
	public void testAddingToTrie()
	{
		trie.add("1234-abc#;;a", test_object_1);
		assertEquals(test_object_1, trie.get("1234-abc#;;a"));
	}
	
	@Test
	public void testCorrectObjectsReturnedFromUsingReader() throws IOException
	{
		trie.add("test_object_1", test_object_1);
		trie.add("test_object_2", test_object_2);
		trie.add("test_object_3", test_object_3);
		trie.add("test_object_4", test_object_4);
		
		StringReader reader = new StringReader("here is some text, test_object_1 is here too.\n"+
				"and also test_object_2\n"+
				"more stuff. 138t912109\n"+
				"\n"+
				"test_object 3 isnt here, its not spelt correctly\n"+
				"and finally test_object_4");
		
		List<String> foundObjects = trie.getMatches(reader);
		assertEquals(3, foundObjects.size());
		assertEquals(test_object_1, foundObjects.get(0));
		assertEquals(test_object_2, foundObjects.get(1));
		assertEquals(test_object_4, foundObjects.get(2));
	}
	
}
