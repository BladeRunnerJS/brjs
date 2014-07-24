package org.bladerunnerjs.utility.trie.node;

import java.util.regex.Pattern;

public interface TrieNode<T> extends Comparable<TrieNode<T>>
{
	TrieNode<T> getNextNode(char character);
	char getChar();
	TrieNode<T> getOrCreateNextNode(char character);
	T getValue();
	void setValue(T value);
	void setValue(T value, Pattern matchPattern);
	TrieNode<T>[] getChildren();
	int size();
	Pattern getMatchPattern();
}
