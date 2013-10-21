package org.bladerunnerjs.model.exception.name;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.App;


public class UnableToAutomaticallyGenerateAppNamespaceException extends InvalidNameException
{
	private static final long serialVersionUID = 5666303784767589496L;

	public UnableToAutomaticallyGenerateAppNamespaceException(App app)
	{
		super("Unable to automatically calculate app namespace for app '" + app.getName() + "'. Please supply a namespace for this app.");
	}
}
