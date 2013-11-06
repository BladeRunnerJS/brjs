package org.bladerunnerjs.model.utility;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

public class Trie<T>
{
	private TrieNode<T> root = new TrieNode<T>();
	
	public void add(String key, T value) {
		TrieNode<T> node = root;
		
		for( char character : key.toCharArray() )
		{
			node = node.getOrCreateNextNode( character );
		}
		
		node.setValue(value);
	}
	
	public Object get(String key)
	{
		TrieNode<T> node = root;
		
		for( char character : key.toCharArray() )
		{
			node = node.getNextNode( character );
		}
		
		if (node == null)
		{
			return null;
		}
		
		return node.getValue();
	}
	
	public List<T> getMatches(Reader reader) throws IOException
	{
		List<T> matches = new LinkedList<T>();
		
		TrieMatcher matcher = new TrieMatcher();
		
		int latestCharVal;
		while ((latestCharVal = reader.read()) != -1)
		{
			char latestChar = (char) latestCharVal;
			processChar( matches, latestChar, matcher);
		}
		processChar( matches, '\n', matcher);
		
		return matches;
		
	}

	
	private void processChar(List<T> matches, char nextChar, TrieMatcher matcher)
	{
		TrieNode<T> nextNode = matcher.next(nextChar);
		if (nextNode == null)
		{
			T matcherValue = matcher.previousNode.getValue();
			if (matcherValue != null)
			{
				matches.add(matcherValue);
			}
			matcher.reset();
		}
		return;
	}


	private class TrieMatcher {
		TrieNode<T> currentNode;
		TrieNode<T> previousNode;
		
		TrieMatcher()
		{
			reset();
		}
		
		TrieNode<T> next(char nextChar)
		{
			previousNode = currentNode;
			currentNode = currentNode.getNextNode(nextChar);
			return currentNode;
		}
		
		void reset()
		{
			currentNode = root;
			previousNode = null;
		}
	}
	
	
}
