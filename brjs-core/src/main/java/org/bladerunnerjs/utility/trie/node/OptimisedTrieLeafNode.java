package org.bladerunnerjs.utility.trie.node;

import java.util.List;
import java.util.regex.Pattern;

public class OptimisedTrieLeafNode<T> extends AbstractOptimisedTrieNode<T>
{
	
	private char character;
	private T value;
	private Pattern matchPattern;
	
	public OptimisedTrieLeafNode(char character, T value, char primarySeperator, List<Character> seperators, Pattern matchPattern)
	{
		super(primarySeperator, seperators);
		this.value = value;
		this.character = character;
		this.matchPattern = matchPattern;
	}

	@Override
	public char getChar()
	{
		return character;
	}
	
	@Override
	public T getValue()
	{
		return value;
	}

	@Override
	public Pattern getMatchPattern()
	{
		return matchPattern;
	}
	
}
