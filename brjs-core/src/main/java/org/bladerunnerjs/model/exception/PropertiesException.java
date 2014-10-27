package org.bladerunnerjs.model.exception;


public class PropertiesException extends Exception
{
	public static final String ERROR_GETTING_PROPERTIES_EXCEPTION = "There was an error getting properties";
	public static final String ERROR_SETTING_PROPERTIES_EXCEPTION = "There was an error setting properties";
	
	private static final long serialVersionUID = 1L;
	private String message;
	
	public PropertiesException(Exception e) {
		super(e);
		message = e.getMessage();
	}

	public PropertiesException(String message) {
		super(message);
		this.message = message;
	}
	
	public PropertiesException(String message, Exception e) {
		super(message, e);
		this.message = message + "; " + e.getMessage();
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
