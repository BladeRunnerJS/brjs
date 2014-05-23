package org.bladerunnerjs.utility.trie;

import java.util.LinkedList;
import java.util.List;


public class TrieNode<T>
{

	private static final char ROOT_NODE_CHAR = '\u0000';
	
	private List<TrieNode<T>> children = new LinkedList<>();
	private T value;
	private final char nodeChar;
	
	public TrieNode ()
	{
		nodeChar = ROOT_NODE_CHAR;
	}
	
	public TrieNode (char character)
	{
		nodeChar = character;
	}
	
	public TrieNode<T> getOrCreateNextNode(char character)
	{
		if (getNextNode(character) == null)
		{
			children.add( new TrieNode<T>(character) );
		}
		return getNextNode(character);
	}

	public TrieNode<T> getNextNode(char character)
	{
		for (TrieNode<T> trieNode : children) {
			if (trieNode.nodeChar == character) {
				return trieNode;
			}
		}
		return null;
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
