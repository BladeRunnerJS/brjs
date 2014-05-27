package org.bladerunnerjs.utility.trie.node;


public class OptimisedTrieTrunkNode<T> extends AbstractOptimisedTrieNode<T>
{

	private char character;
	private TrieNode<T>[] children;
	
	public OptimisedTrieTrunkNode(char character, TrieNode<T>[] children)
	{
		this.character = character;
		this.children = children;
	}

	@Override
	public TrieNode<T> getNextNode(char character)
	{
		return getNextNode(children, character);
	}
	
	@Override
	public char getChar()
	{
		return character;
	}
	
}
