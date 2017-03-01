package org.bladerunnerjs.utility;

public class AppDeploymentFailedException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public AppDeploymentFailedException(Exception e) {
		super(e);
	}
}
