package org.bladerunnerjs.model.exception.test;

public class BrowserNotFoundException extends Exception {

	private static final long serialVersionUID = -7934305990312796421L;

	public BrowserNotFoundException()
	{
		super("Could not find the browser on disk. Please check your test configuration file.");
	}

	public BrowserNotFoundException(String message) {
		super(message);
	}
}
