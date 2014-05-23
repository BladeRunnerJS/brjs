package org.bladerunnerjs.utility.trie;


public class TrieNodeChildAlreadyExistsException extends Exception
{
	private static final long serialVersionUID = 1L;

	public TrieNodeChildAlreadyExistsException(char nodeChar, char childChar)
	{
		super("The node with char '"+nodeChar+"' already has a child with the value '"+childChar+"'.");
	}
}
