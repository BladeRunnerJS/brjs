package org.bladerunnerjs.utility.trie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.bladerunnerjs.utility.trie.exception.EmptyTrieKeyException;
import org.bladerunnerjs.utility.trie.exception.TrieKeyAlreadyExistsException;
import org.bladerunnerjs.utility.trie.node.BasicRootTrieNode;
import org.bladerunnerjs.utility.trie.node.OptimisedTrieLeafNode;
import org.bladerunnerjs.utility.trie.node.OptimisedTrieRootNode;
import org.bladerunnerjs.utility.trie.node.OptimisedTrieTrunkLeafNode;
import org.bladerunnerjs.utility.trie.node.OptimisedTrieTrunkNode;
import org.bladerunnerjs.utility.trie.node.TrieNode;

public class Trie<T>
{
	private static final int CHILD_SIZE_OPTIMIZATION_THRESHOLD = 5;

	private static final char[] DELIMETERS = " \t\r\n.,;(){}<>[]+-*/'\"\\\"\'\\'".toCharArray();
	
	private TrieNode<T> root = new BasicRootTrieNode<>();
	private int readAheadLimit = 1;
	private boolean trieOptimized = false;
	
	private int largestChildList = 0;
	
	public void add(String key, T value) throws EmptyTrieKeyException, TrieKeyAlreadyExistsException, TrieLockedException {
		if (trieOptimized) {
			throw new TrieLockedException();
		}
		
		if (key.length() < 1)
		{
			throw new EmptyTrieKeyException();
		}
		
		TrieNode<T> node = root;
		for( char character : key.toCharArray() )
		{
			node = node.getOrCreateNextNode( character );
			int nodeSize = node.size();
			largestChildList = (nodeSize > largestChildList) ? largestChildList : nodeSize;
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
	
	
	public void optimize() {
		if (isOptimized()) {
			return;
		}
		trieOptimized = true;
		root = createOptimisedTrieNode(root);
		System.gc();
	}

	public boolean isOptimized() {
		return trieOptimized;
	}
	
	public boolean needsOptimizing()
	{
		return largestChildList > CHILD_SIZE_OPTIMIZATION_THRESHOLD;
	}
	
	private TrieNode<T> createOptimisedTrieNode(TrieNode<T> trieNode) {
		char trieNodeChar = trieNode.getChar();
		T trieNodeValue = trieNode.getValue();
		
		TrieNode<T>[] trieNodeChildren = getOrderedTrieNodeChildren(trieNode);
		
		if (trieNodeChildren.length > 0) {
			
			@SuppressWarnings("unchecked")
			TrieNode<T>[] optimisedTrieNodeChildren = new TrieNode[trieNodeChildren.length];
			
			for (int childNum = 0; childNum < trieNodeChildren.length; childNum++) {
				optimisedTrieNodeChildren[childNum] = createOptimisedTrieNode( trieNodeChildren[childNum] );
			}
			
			if (trieNode == root) {
				return new OptimisedTrieRootNode<>(optimisedTrieNodeChildren);
			} else if (trieNodeValue != null) {
				return new OptimisedTrieTrunkLeafNode<T>(trieNodeChar, trieNodeValue, optimisedTrieNodeChildren);
			} else {
				return new OptimisedTrieTrunkNode<>(trieNodeChar, optimisedTrieNodeChildren);
			}
		} else {
			return new OptimisedTrieLeafNode<T>(trieNodeChar, trieNodeValue);
		}
	}
	
	private TrieNode<T>[] getOrderedTrieNodeChildren(TrieNode<T> node) {
		TrieNode<T>[] nodeChildren = node.getChildren();
		Arrays.sort(nodeChildren);
		return nodeChildren;
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
	
	private void processChar(List<T> matches, char nextChar, char prevChar, TrieMatcher matcher, Reader reader) throws IOException
	{
		if (matcher.atRootOfTrie)
		{
			reader.mark(readAheadLimit);
		}
		
		TrieNode<T> nextNode = matcher.next(nextChar);
		
		if (nextNode == null)
		{
			T previousValue = matcher.previousNode.getValue();
			if (previousValue != null && (isDelimiter(prevChar) || isDelimiter(nextChar)))
			{
				//TODO best data structure to make this efficient?
				if(!matches.contains(previousValue)){
					matches.add( previousValue );
				}
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
