package org.bladerunnerjs.api.model.exception.name;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.App;

/**
 * Thrown when the app namespace could not be determined and one must be supplied instead. 
*/ 

public class UnableToAutomaticallyGenerateAppRequirePrefixException extends InvalidNameException
{
	private static final long serialVersionUID = 1L;

	public UnableToAutomaticallyGenerateAppRequirePrefixException(App app)
	{
		super("Unable to automatically calculate app namespace for app '" + app.getName() + "'. Please supply a namespace for this app.");
	}
}
