package org.bladerunnerjs.model.utility;

import java.util.HashMap;
import java.util.Map;


public class TrieNode<T>
{

	private Map<Character, TrieNode<T>> children = new HashMap<Character, TrieNode<T>>();
	private T value;
	
	public TrieNode<T> getOrCreateNextNode(char character)
	{
		if (getNextNode(character) == null)
		{
			children.put(character, new TrieNode<T>());
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

}
