package org.bladerunnerjs.utility.trie;


public class LeafTrieNode<T> extends AbstractTrieNode
{
	private T value;
		
	public LeafTrieNode (AbstractTrieNode replaceNode, T value)
	{
		super(replaceNode);
		setValue(value);
	}
	
	public void setValue(T value)
	{
		this.value = value;
	}
	
	public T getValue()
	{
		return value;
	}
	
}
