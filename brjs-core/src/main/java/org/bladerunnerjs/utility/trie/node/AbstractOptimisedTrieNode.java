package org.bladerunnerjs.utility.trie.node;

import java.util.List;


public abstract class AbstractOptimisedTrieNode<T> implements TrieNode<T>
{
	
	private List<Character> separators;
	private char primarySeparator;

	public AbstractOptimisedTrieNode(char primarySeperator, List<Character> seperators)
	{
		this.separators = seperators;
		this.primarySeparator = primarySeperator;
	}
	
	@Override
	public TrieNode<T> getNextNode(char character)
	{
		return null;
	}

	@Override
	public TrieNode<T> getOrCreateNextNode(char character)
	{
		return getNextNode(character);
	}
	
	@Override
	public T getValue()
	{
		unsupportedOptimisedTrieMethod();
    	return null;
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
		/* 
		 * This breaks Liskov *but* we don't want to store any values that aren't needed in Optimised Trie Nodes to reduce memory usage 
		 * and throwing this exception prevents us from using methods that will return dummy data and result in weird behaviour.
		 */
		throw new RuntimeException("This is an optimised TrieNode and doesn't support this method.");
	}
	
	protected final TrieNode<T> getNextNode(TrieNode<T>[] children, char character) {
		for (TrieNode<T> trieNode : children) {
			if (trieNode == null){
				return null;
			}
			char trieChar = trieNode.getChar();
			if(trieChar == character) {
				return trieNode;
			}
			if (trieChar == primarySeparator && separators.contains(character)) {
				return trieNode;
			}
		}
		return null;
	}
}
