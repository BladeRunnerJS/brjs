package org.bladerunnerjs.utility.trie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public class Trie<T>
{
	private static final char[] DELIMETERS = " \t\r\n.,;(){}<>[]+-*/'\"".toCharArray();
	private RootTrieNode root = new RootTrieNode();
	private int readAheadLimit = 1;
	
	public void add(String key, T value) throws EmptyTrieKeyException, TrieKeyAlreadyExistsException {
		if (key.length() < 1)
		{
			throw new EmptyTrieKeyException();
		}
		
		TrieNode node = root;
		TrieNode previousNode = null;
		for( char character : key.toCharArray() )
		{
			previousNode = node;
			node = node.getOrCreateNextNode( character );
		}
		
		if (node instanceof LeafTrieNode)
		{
			throw new TrieKeyAlreadyExistsException(key);
		}
		
		TrieNode leafNode = new LeafTrieNode<>( (AbstractTrieNode)node, value);
		previousNode.replaceChildNode(node, leafNode);
		
		readAheadLimit = Math.max(readAheadLimit, key.length() + 1);
	}
	
	public boolean containsKey(String key) {
		return (get(key) == null) ? false : true;
	}
	
	public T get(String key)
	{
		TrieNode node = root;
		
		for( char character : key.toCharArray() )
		{
			node = node.getNextNode( character );
			
			if (node == null)
			{
				return null;
			}
		}
		
		if (!(node instanceof LeafTrieNode)) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		T value = (T) ((LeafTrieNode<?>) node).getValue();
		return value;
	}
	
	public List<T> getMatches(Reader reader) throws IOException
	{
		if (!reader.markSupported()) {
			reader = new BufferedReader(reader);
		}
		
		List<T> matches = new LinkedList<T>();
		TrieMatcher matcher = new TrieMatcher();
		int nextChar, prevChar = 0;
		
		while ((nextChar = reader.read()) != -1)
		{
			processChar(matches, (char) nextChar, (char) prevChar, matcher, reader);
			prevChar = nextChar;
		}
		processChar(matches, '\n', (char) prevChar, matcher, reader);
		
		return matches;	
	}
	
	private void processChar(List<T> matches, char nextChar, char prevChar, TrieMatcher matcher, Reader reader) throws IOException
	{
		if (matcher.atRootOfTrie)
		{
			reader.mark(readAheadLimit);
		}
		
		TrieNode nextNode = matcher.next(nextChar);
		
		if (nextNode == null)
		{
			if (matcher.previousNode instanceof LeafTrieNode && (isDelimiter(prevChar) || isDelimiter(nextChar)))
			{
				@SuppressWarnings("unchecked")
				LeafTrieNode<T> leafNode = (LeafTrieNode<T>) matcher.previousNode;
				matches.add( leafNode.getValue() );
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
		TrieNode currentNode;
		TrieNode previousNode;
		boolean atRootOfTrie;
		
		TrieMatcher()
		{
			reset();
		}
		
		TrieNode next(char nextChar)
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
