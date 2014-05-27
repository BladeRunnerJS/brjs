package org.bladerunnerjs.utility.trie.node;


public class OptimisedTrieRootNode<T> extends AbstractOptimisedTrieNode<T>
{

	TrieNode<T>[] children;
	
	public OptimisedTrieRootNode(TrieNode<T>[] children)
	{
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
		return '\u0000';
	}
}
