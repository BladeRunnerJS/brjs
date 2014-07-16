package org.bladerunnerjs.utility.trie.node;

import java.util.List;

public class BasicRootTrieNode<T> extends BasicTrieNode<T>
{
	public BasicRootTrieNode(char primarySeperator, List<Character> seperators) {
		super('\u0000', primarySeperator, seperators);
	}
}
