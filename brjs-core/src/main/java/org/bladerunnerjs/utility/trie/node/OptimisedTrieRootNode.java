package org.bladerunnerjs.utility.trie.node;


public class OptimisedTrieRootNode<T> implements TrieNode<T>
{

	TrieNode<T>[] children;
	
	public OptimisedTrieRootNode(TrieNode<T>[] children)
	{
		this.children = children;
	}

	@Override
	public TrieNode<T> getNextNode(char character)
	{
		for (TrieNode<T> trieNode : children) {
			if (trieNode != null && trieNode.getChar() == character) {
				return trieNode;
			}
		}
		return null;
	}
	
	@Override
	public TrieNode<T> getOrCreateNextNode(char character)
	{
		return getNextNode(character);
	}
	
	@Override
	public char getChar()
	{
		return '\u0000';
	}
	
	@Override
	public T getValue()
	{
		return null;
	}

	@Override
	public void setValue(T value)
	{	
	}
	
	@Override
 	public TrieNode<T>[] getChildren()
 	{
 		return null;
 	}

    @Override
 	public int compareTo(TrieNode<T> compareNode)
 	{
 		return 0;
 	}
}
