package org.bladerunnerjs.utility.trie;


public interface TrieNode
{
	TrieNode getNextNode(char character);
	char getChar();
	TrieNode getOrCreateNextNode(char character);
	TrieNode createNextNode(char character, TrieNode child) throws TrieNodeChildAlreadyExistsException;
	void replaceChildNode(TrieNode oldNode, TrieNode newNode);
}
