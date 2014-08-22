package org.bladerunnerjs.utility.trie.node;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;


public class BasicTrieNode<T> implements TrieNode<T>
{
	private T value;
	private final char nodeChar;
	private List<TrieNode<T>> children = new LinkedList<>();
	private List<Character> separators;
	private char primarySeparator;
	private Pattern matchPattern;
	
	public BasicTrieNode(char nodeChar, char primarySeperator, List<Character> seperators)
	{
		this(nodeChar, null, primarySeperator, seperators);
	}
	
	public BasicTrieNode(char nodeChar, T value, char primarySeperator, List<Character> seperators)
	{
		this.nodeChar = nodeChar;
		this.value = value;
		this.primarySeparator = primarySeperator;
		this.separators = seperators;
	}
	
	@Override
	public TrieNode<T> getOrCreateNextNode(char character)
	{
		TrieNode<T> node = getNextNode(character);
		if (node != null) {
			return node;
		}
		node = new BasicTrieNode<T>(character, primarySeparator, separators);
		children.add( node );
		return node;
	}

	@Override
	public TrieNode<T> getNextNode(char character)
	{
		for (TrieNode<T> trieNode : children) {
			char trieNodeChar = trieNode.getChar();
			if (trieNodeChar == character) {
				return trieNode;
			}

			if (trieNodeChar == primarySeparator && separators.contains(character)) {
				return trieNode;
			}
		}
		return null;
	}
	
	public void setValue(T value)
	{
		setValue(value, null);
	}
	
	public void setValue(T value, Pattern matchPattern)
	{
		this.value = value;
		this.matchPattern = matchPattern;
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

	@Override
	public Pattern getMatchPattern()
	{
		return matchPattern;
	}
	
}
