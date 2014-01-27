package org.bladerunnerjs.utility;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public class Trie<T>
{
	private static final char[] DELIMETERS = " \t\r\n.,;(){}<>[]+-*/'\"".toCharArray();
	private TrieNode<T> root = new TrieNode<T>();
	private int largestKeyLength = 0;
	
	public void add(String key, T value) throws EmptyTrieKeyException, TrieKeyAlreadyExistsException {
		if (key.length() < 1)
		{
			throw new EmptyTrieKeyException();
		}
		
		TrieNode<T> node = root;
		for( char character : key.toCharArray() )
		{
			node = node.getOrCreateNextNode( character );
		}
		
		if (node.getValue() != null)
		{
			throw new TrieKeyAlreadyExistsException(key);
		}
		node.setValue(value);
		largestKeyLength = Math.max(largestKeyLength, key.length());
	}
	
	public boolean containsKey(String key) {
		return (get(key) == null) ? false : true;
	}
	
	public T get(String key)
	{
		TrieNode<T> node = root;
		
		for( char character : key.toCharArray() )
		{
			node = node.getNextNode( character );
			
			if (node == null)
			{
				return null;
			}
		}
		
		return node.getValue();
	}
	
	public List<T> getMatches(Reader reader) throws IOException
	{
		List<T> matches = new LinkedList<T>();
		
		TrieMatcher matcher = new TrieMatcher();
		
		int latestCharVal;
		boolean foundCompleteMatch = true;
		
		if (!reader.markSupported())
		{
			throw new RuntimeException(this.getClass().getSimpleName() + " only supports readers that support 'marks' - (reader.markSupported() == true)");
		}
		
		reader.mark(largestKeyLength+1);
		while ((latestCharVal = readNextChar(reader, matcher, foundCompleteMatch)) != -1)
		{
			if (matcher.startedReadingNewChars)
			{
				reader.mark(largestKeyLength+1);
			}
			char latestChar = (char) latestCharVal;
			
			foundCompleteMatch = processChar(matches, latestChar, matcher);
			if (foundCompleteMatch)
			{
				reader.mark(largestKeyLength+1);
			}
		}
		processChar(matches, '\n', matcher);
		
		return matches;	
	}

	private int readNextChar(Reader reader, TrieMatcher matcher, boolean foundCompleteMatch) throws IOException
	{
		if (matcher.currentNode == root)
		{
			reader.reset();
		}
		return reader.read();
	}
	
	private boolean processChar(List<T> matches, char nextChar, TrieMatcher matcher)
	{
		boolean foundCompleteMatch = false;
		
		TrieNode<T> nextNode = matcher.next(nextChar);
		
		if (nextNode == null)
		{
			T matcherValue = matcher.previousNode.getValue();
			if (matcherValue != null && matchesDelimeter(nextChar))
			{
				matches.add(matcherValue);
				foundCompleteMatch = true;
			}
			matcher.reset();
		}
		
		if (matcher.startedReadingNewChars && !matchesDelimeter(nextChar))
		{
			matcher.reset();			
		}
		
		return foundCompleteMatch;
	}


	private boolean matchesDelimeter(char nextChar)
	{
		return ArrayUtils.contains(DELIMETERS, nextChar);
	}
	
	private class TrieMatcher {
		TrieNode<T> currentNode;
		TrieNode<T> previousNode;
		boolean startedReadingNewChars;
		
		TrieMatcher()
		{
			reset();
		}
		
		TrieNode<T> next(char nextChar)
		{
			previousNode = currentNode;
			currentNode = currentNode.getNextNode(nextChar);
			startedReadingNewChars = false;
			return currentNode;
		}
		
		void reset()
		{
			currentNode = root;
			previousNode = null;
			startedReadingNewChars = true;
		}
	}
}
