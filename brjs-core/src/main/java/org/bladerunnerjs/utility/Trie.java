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
	private int readAheadLimit = 1;
	
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
		readAheadLimit = Math.max(readAheadLimit, key.length() + 1);
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
		if (!reader.markSupported())
		{
			throw new RuntimeException(this.getClass().getSimpleName() + " only supports readers that support 'marks' - (reader.markSupported() == true)");
		}
		
		List<T> matches = new LinkedList<T>();
		TrieMatcher matcher = new TrieMatcher();
		int nextChar;
		
		while ((nextChar = reader.read()) != -1)
		{
			processChar(matches, (char) nextChar, matcher, reader);
		}
		processChar(matches, '\n', matcher, reader);
		
		return matches;	
	}
	
	private void processChar(List<T> matches, char nextChar, TrieMatcher matcher, Reader reader) throws IOException
	{
		if (matcher.atRootOfTrie)
		{
			reader.mark(readAheadLimit);
		}
		
		TrieNode<T> nextNode = matcher.next(nextChar);
		
		if (nextNode == null)
		{
			T trieValue = matcher.previousNode.getValue();
			if (trieValue != null && isDelimiter(nextChar))
			{
				matches.add(trieValue);
				reader.mark(readAheadLimit);
			}
			matcher.reset();
			reader.reset();
		}
	}

	private boolean isDelimiter(char nextChar)
	{
		return ArrayUtils.contains(DELIMETERS, nextChar);
	}
	
	private class TrieMatcher {
		TrieNode<T> currentNode;
		TrieNode<T> previousNode;
		boolean atRootOfTrie;
		
		TrieMatcher()
		{
			reset();
		}
		
		TrieNode<T> next(char nextChar)
		{
			previousNode = currentNode;
			currentNode = currentNode.getNextNode(nextChar);
			atRootOfTrie = false;
			return currentNode;
		}
		
		void reset()
		{
			currentNode = root;
			previousNode = null;
			atRootOfTrie = true;
		}
	}
}
