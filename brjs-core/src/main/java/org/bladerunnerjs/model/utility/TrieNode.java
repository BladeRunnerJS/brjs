package org.bladerunnerjs.model.utility;

import java.util.HashMap;
import java.util.Map;


public class TrieNode<T>
{

	private static final char ROOT_NODE_CHAR = '\u0000';
	
	private Map<Character, TrieNode<T>> children = new HashMap<Character, TrieNode<T>>();
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
			children.put(character, new TrieNode<T>(character));
		}
		return getNextNode(character);
	}

	public TrieNode<T> getNextNode(char character)
	{
		return children.get(character);
	}
	
	public void setValue(T value)
	{
		this.value = value;
	}
	
	public T getValue()
	{
		return value;
	}

	public char getNodeChar()
	{
		return nodeChar;
	}
	
}
