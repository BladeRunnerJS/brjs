package org.bladerunnerjs.utility.trie;


public class TrieLockedException extends Exception
{
	private static final long serialVersionUID = 1L;

	public TrieLockedException()
	{
		super("The Trie has been optimised and can't accept any more values.");
	}
}
