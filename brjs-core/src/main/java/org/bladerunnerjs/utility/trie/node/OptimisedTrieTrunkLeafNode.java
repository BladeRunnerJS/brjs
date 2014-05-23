package org.bladerunnerjs.utility.trie.node;


public class OptimisedTrieTrunkLeafNode<T> implements TrieNode<T>
{
	
 	private char character;
 	private T value;
	private TrieNode<T>[] children;
 	
 	public OptimisedTrieTrunkLeafNode(char character, T value, TrieNode<T>[] children)
 	{
 		this.character = character;
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
 		return character;
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
