package org.bladerunnerjs.model.exception;


public class PropertiesException extends Exception
{

	public static final String ERROR_GETTING_PROPERTIES_EXCEPTION = "There was an error getting properties";
	public static final String ERROR_SETTING_PROPERTIES_EXCEPTION = "There was an error setting properties";
	
	public PropertiesException(String msg, Exception ex)
	{
		super(msg, ex);
	}

	private static final long serialVersionUID = 9116224210218595864L;

}
