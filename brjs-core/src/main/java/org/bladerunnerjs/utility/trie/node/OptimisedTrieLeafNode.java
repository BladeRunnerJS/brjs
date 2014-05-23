package org.bladerunnerjs.utility.trie.node;

public class OptimisedTrieLeafNode<T> implements TrieNode<T>
{
	
	private char character;
	private T value;
	
	public OptimisedTrieLeafNode(char character, T value)
	{
		this.value = value;
		this.character = character;
	}

	@Override
	public char getChar()
	{
		return character;
	}

	@Override
	public TrieNode<T> getNextNode(char character)
	{
		return null;
	}

	@Override
	public TrieNode<T> getOrCreateNextNode(char character)
	{
		return null;
	}
	
	@Override
	public T getValue()
	{
		return value;
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
