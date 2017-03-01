package org.bladerunnerjs.api.model.exception;

/**
 * Thrown when an error has been encountered while setting the properties of a plugin or node. 
*/ 

public class PropertiesException extends Exception
{
	public static final String ERROR_GETTING_PROPERTIES_EXCEPTION = "There was an error getting properties";
	public static final String ERROR_SETTING_PROPERTIES_EXCEPTION = "There was an error setting properties";
	
	private static final long serialVersionUID = 1L;
	
	public PropertiesException(Exception e) {
		super(e);
	}

	public PropertiesException(String message) {
		super(message);
	}
	
	public PropertiesException(String message, Exception e) {
		super(message + "; " + e.getMessage(), e);
	}
}
