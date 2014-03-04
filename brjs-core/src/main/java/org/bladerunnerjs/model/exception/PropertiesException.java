package org.bladerunnerjs.model.exception;


public class PropertiesException extends Exception
{
	public static final String ERROR_GETTING_PROPERTIES_EXCEPTION = "There was an error getting properties";
	public static final String ERROR_SETTING_PROPERTIES_EXCEPTION = "There was an error setting properties";
	
	private static final long serialVersionUID = 1L;
	
	public PropertiesException(String msg, Exception ex)
	{
		super(msg, ex);
	}
}
