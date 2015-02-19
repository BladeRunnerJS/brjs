package org.bladerunnerjs.api.spec.exception;

/**
 * Thrown when the test configuration file does not contain any browser-related configuration.
*/

public class NoBrowsersDefinedException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoBrowsersDefinedException()
	{
		super("Could not find any configured browsers. Please check your test configuration.");
	}

	public NoBrowsersDefinedException(String confPath) 
	{
		super("Could not find any configured browsers. Please check your test configuration file inside \n\t'" + confPath + "'");
	}
}
