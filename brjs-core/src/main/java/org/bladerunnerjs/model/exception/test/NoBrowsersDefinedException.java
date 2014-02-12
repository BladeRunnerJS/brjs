package org.bladerunnerjs.model.exception.test;

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
