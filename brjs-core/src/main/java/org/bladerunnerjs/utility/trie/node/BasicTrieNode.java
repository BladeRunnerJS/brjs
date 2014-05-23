package org.bladerunnerjs.utility.trie.node;

import java.util.LinkedList;
import java.util.List;


public class BasicTrieNode<T> implements TrieNode<T>
{
	private T value;
	private final char nodeChar;
	private List<TrieNode<T>> children = new LinkedList<>();
	
	public BasicTrieNode(char nodeChar)
	{
		this(nodeChar, null);
	}
	
	public BasicTrieNode(char nodeChar, T value)
	{
		this.nodeChar = nodeChar;
		this.value = value;
	}
	
	@Override
	public TrieNode<T> getOrCreateNextNode(char character)
	{
		TrieNode<T> node = getNextNode(character);
		if (node != null) {
			return node;
		}
		node = new BasicTrieNode<T>(character);
		children.add( node );
		return node;
	}

	@Override
	public TrieNode<T> getNextNode(char character)
	{
		for (TrieNode<T> trieNode : children) {
			if (trieNode.getChar() == character) {
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
	
	@Override
	public char getChar() {
		return nodeChar;
	}
	
	@SuppressWarnings("unchecked")
	public TrieNode<T>[] getChildren()
	{
		return children.toArray( new TrieNode[0] );
	}

	@Override
	public int compareTo(TrieNode<T> compareNode)
	{
		TrieNode<T>[] compareNodeChildren = compareNode.getChildren();
		if (compareNodeChildren.length == children.size()) {
			return 0;
		}
		return (compareNodeChildren.length < children.size()) ? -1 : 0;
	}
	
}
