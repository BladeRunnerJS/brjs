package org.bladerunnerjs.utility.trie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.bladerunnerjs.utility.trie.exception.EmptyTrieKeyException;
import org.bladerunnerjs.utility.trie.exception.TrieKeyAlreadyExistsException;
import org.bladerunnerjs.utility.trie.node.BasicRootTrieNode;
import org.bladerunnerjs.utility.trie.node.TrieNode;

public class Trie<T>
{
	private static final Pattern MATCH_ALL_PATTERN = Pattern.compile(".*", Pattern.DOTALL);
	
	private static final char[] DELIMETERS = " \t\r\n.,;(){}<>[]+-*/'\"\\\"\'\\'".toCharArray();
	
	private TrieNode<T> root;
	private int readAheadLimit = 1;
	private Map<String, TrieNode<T>> trieLookup = new HashMap<String, TrieNode<T>>();
	private List<Character> seperators;
	private char primarySeperator;
	
	public Trie() {
		this('\u0000');
	}
	
	public Trie(char primarySeperator, Character... seperators) {
		this.primarySeperator = primarySeperator;
		this.seperators = Arrays.asList(seperators);
		root = new BasicRootTrieNode<>(primarySeperator, this.seperators);
	}
	
	public void add(String key, T value) throws EmptyTrieKeyException, TrieKeyAlreadyExistsException {
		add(key, value, MATCH_ALL_PATTERN);
	}
	
	public void add(String key, T value, Pattern matchPattern) throws EmptyTrieKeyException, TrieKeyAlreadyExistsException {
		
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
		
		node.setValue(value, matchPattern);
		trieLookup.put(key, node);
		readAheadLimit = Math.max(readAheadLimit, key.length() + 1);
	}
	
	public boolean containsKey(String key) {
		return (trieLookup.get(key) != null);
	}
	
	public T get(String key)
	{
		TrieNode<T> node = getNode(key);
		
		if (node == null) {
			return null;
		}
		
		return node.getValue();
	}
	
	public List<T> getMatches(Reader reader) throws IOException
	{
		if (!reader.markSupported()) {
			reader = new BufferedReader(reader);
		}
	
		char charAtPointOfFirstMatch = '\u0000';
		StringBuilder matchString = new StringBuilder();
		List<T> matches = new LinkedList<T>();
		TrieMatcher matcher = new TrieMatcher();
		int nextChar, prevChar = 0;
		
		while ((nextChar = reader.read()) != -1)
		{
			charAtPointOfFirstMatch = processChar(matches, charAtPointOfFirstMatch, (char) nextChar, (char) prevChar, matcher, reader, matchString);
			prevChar = nextChar;
		}
		charAtPointOfFirstMatch = processChar(matches, charAtPointOfFirstMatch, '\n', (char) prevChar, matcher, reader, matchString);
		
		return matches;	
	}
	
	
	private TrieNode<T> getNode(String key) {
		TrieNode<T> node = root;
		
		for( char character : key.toCharArray() )
		{
			node = node.getNextNode( character );
			
			if (node == null)
			{
				return null;
			}
		}
		
		return node;
	}
	
	private char processChar(List<T> matches, char charAtPointOfFirstMatch, char nextChar, char prevChar, TrieMatcher matcher, Reader reader, StringBuilder matchString) throws IOException
	{
		if (matcher.atRootOfTrie)
		{
			reader.mark(readAheadLimit);
			charAtPointOfFirstMatch = prevChar;
		}
		
		TrieNode<T> nextNode = matcher.next(nextChar);
		
		if (nextNode == null)
		{
			T previousValue = matcher.previousNode.getValue();
			if (previousValue != null && (isDelimiter(prevChar) || isDelimiter(nextChar)))
			{
				//TODO best data structure to make this efficient?
				if(!matches.contains(previousValue)){
					Pattern matchPattern = matcher.previousNode.getMatchPattern();
					if (matchPattern.matcher(charAtPointOfFirstMatch+matchString.toString()+nextChar).matches()) {
						matches.add( previousValue );
					}
				}
				reader.mark(readAheadLimit);
			}
			matcher.reset();
			reader.reset();
			matchString.setLength(0);
		} else {
			matchString.append(nextChar);
		}
		
		return charAtPointOfFirstMatch;
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
