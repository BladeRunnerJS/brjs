package org.bladerunnerjs.model.exception;

import java.io.IOException;

public class ConfigException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public ConfigException(String message) {
		super(message);
	}
	
	public ConfigException(Throwable e) {
		super(e);
	}

	public ConfigException(String message, IOException e) {
		super(message, e);
	}
}
