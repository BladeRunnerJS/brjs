package org.bladerunnerjs.model.exception;

/**
 * Thrown when the required configuration could not be resolved.
*/

public class ConfigException extends Exception
{
	private static final long serialVersionUID = 1L;
	private String message;
	
	public ConfigException(String message) {
		super(message);
		this.message = message;
	}
	
	public ConfigException(Throwable e) {
		super(e);
		message = e.getMessage();
	}

	public ConfigException(String message, Exception e) {
		super(message, e);
		this.message = message + "; " + e.getMessage();
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
