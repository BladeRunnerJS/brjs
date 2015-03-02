package org.bladerunnerjs.api.model.exception;

/**
 * Thrown when the required configuration could not be resolved.
*/

public class ConfigException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public ConfigException(String message) {
		super(message);
	}
	
	public ConfigException(Throwable e) {
		super(e);
	}

	public ConfigException(String message, Exception e) {
		super(message + "; " + e.getMessage(), e);
	}
}
