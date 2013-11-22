package org.bladerunnerjs.model.utility;


public class TrieKeyAlreadyExistsException extends Exception
{
	private static final long serialVersionUID = -140219453731533725L;

	public TrieKeyAlreadyExistsException(String key)
	{
		super("The key '"+key+"' already exists");
	}
}
