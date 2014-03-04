package com.caplin.cutlass.bundler.js;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.bundler.js.aliasing.AliasDefinition;
import com.caplin.cutlass.bundler.js.aliasing.AliasInformation;
import com.caplin.cutlass.bundler.js.aliasing.ScenarioAliases;

public class ClassesTrieTest
{
	private final ClassesTrie trie = new ClassesTrie();
	private final ScenarioAliases scenarioAliases = new ScenarioAliases();
	private final AliasDefinition aliasDefinition = new AliasDefinition( null, "a", "i" );
	private final List<String> classes = Arrays.asList("a.test.class", "a.test.class.again", "another.class");
	private final AliasInformation aliasInformation = new AliasInformation( "a", aliasDefinition, scenarioAliases );
	
	private boolean containsClass(String classname)
	{
		LetterNode node = trie.getRootNode();
		for(char character : classname.toCharArray())
		{
			node = node.find(character);
			if (node == null)
			{
				return false;
			}
		}
		return node.isIdentifierEnd();
	}
	
	@Before
	public void setup()
	{
		for (String classname : classes)
		{
			trie.addClass(classname);
		}
	}

	@Test
	public void testClassesAddedToTheTreeCanBeRetrieved() throws Exception
	{
		for (String classname : classes)
		{
			assertTrue(containsClass(classname));
		}
	}

	@Test
	public void testCheckingForNonExistantClassReturnsFalse() throws Exception
	{
		assertFalse(containsClass("a.nonexistant.class"));
	}

	@Test
	public void testAddingAnyStringToTree() throws Exception
	{
		trie.addClass("1234-abc#;;a");
		assertTrue(containsClass("1234-abc#;;a"));
	}
	
	@Test
	public void addedAliasInformationIsAvailableForRetrieval()
	{
		trie.addAlias( "the-alias", aliasInformation );
		
		assertEquals( aliasInformation, trie.getAliasInformation("the-alias") );
	}
	
	@Test
	public void aliasNodeIsSetAsAlias()
	{
		trie.addAlias( "abc", aliasInformation );
		
		LetterNode root = trie.getRootNode();
		LetterNode aliasNode = root.find( 'a' ).find( 'b' ).find( 'c' );
		
		assertTrue( aliasNode.isAlias() );
	}
}
