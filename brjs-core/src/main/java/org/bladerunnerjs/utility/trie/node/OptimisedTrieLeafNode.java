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
    	unsupportedOptimisedTrieMethod();
    }
    
    @Override
 	public TrieNode<T>[] getChildren()
 	{
    	unsupportedOptimisedTrieMethod();
    	return null;
 	}

    @Override
 	public int compareTo(TrieNode<T> compareNode)
 	{
    	unsupportedOptimisedTrieMethod();
    	return 0;
 	}

	@Override
	public int size()
	{
		unsupportedOptimisedTrieMethod();
		return -1;
	}
	
	private void unsupportedOptimisedTrieMethod() {
		throw new RuntimeException("This is an optimised TrieNode and doesn't support this method.");
	}

}
