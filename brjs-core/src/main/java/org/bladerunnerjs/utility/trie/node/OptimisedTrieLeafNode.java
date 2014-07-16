package org.bladerunnerjs.utility.trie.node;

import java.util.List;

public class OptimisedTrieLeafNode<T> extends AbstractOptimisedTrieNode<T>
{
	
	private char character;
	private T value;
	
	public OptimisedTrieLeafNode(char character, T value, char primarySeperator, List<Character> seperators)
	{
		super(primarySeperator, seperators);
		this.value = value;
		this.character = character;
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

}
