package org.bladerunnerjs.utility.trie;

import java.util.LinkedList;
import java.util.List;


public abstract class AbstractTrieNode implements TrieNode
{
	private final char nodeChar;
	private List<TrieNode> children = new LinkedList<>();
	
	public AbstractTrieNode(char nodeChar)
	{
		this.nodeChar = nodeChar;
	}
	
	public AbstractTrieNode(AbstractTrieNode cloneNode)
	{
		this.nodeChar = cloneNode.nodeChar;
		this.children = cloneNode.children;
	}
	
	@Override
	public TrieNode getOrCreateNextNode(char character)
	{
		TrieNode node = getNextNode(character);
		if (node != null) {
			return node;
		}
		try {
			return createNextNode(character, new TrunkTrieNode(character));
		} catch (TrieNodeChildAlreadyExistsException ex) {
			return null;
		}
	}
	
	@Override
	public TrieNode createNextNode(char character, TrieNode child) throws TrieNodeChildAlreadyExistsException
	{
		if (getNextNode(character) != null)
		{
			throw new TrieNodeChildAlreadyExistsException(nodeChar, character);
		}
		
		children.add( child );
		return child;
	}

	@Override
	public TrieNode getNextNode(char character)
	{
		for (TrieNode trieNode : children) {
			if (trieNode.getChar() == character) {
				return trieNode;
			}
		}
		return null;
	}
	
	@Override
	public void replaceChildNode(TrieNode oldNode, TrieNode newNode)
	{
		children.remove(oldNode);
		children.add(newNode);
	}
	
	@Override
	public char getChar() {
		return nodeChar;
	}
	
}
