package org.bladerunnerjs.model;


public class InstanceOfShouldntBeInvokedException extends RuntimeException
{
	private static final long serialVersionUID = 1005007633653118598L;

	public InstanceOfShouldntBeInvokedException()
	{
		super("'instanceOf' shouldn't be called on this class. The Plugin probably isn't wrapped in a VirtualProxy.");
	}
	
}
