package org.bladerunnerjs.model.exception;

import org.bladerunnerjs.plugin.ContentPlugin;

/**
 * Class derived from ModelOperationException - Exception - Throwable - Object.
 * Thrown when an incorrect content plugin request prefix has been specified.
*/

public class IncorectContentPathPrefixException extends ModelOperationException
{
	private static final long serialVersionUID = 1716077006805580210L;

	public IncorectContentPathPrefixException(ContentPlugin contentPlugin, String requestPrefix, String contentPath)
	{
		super("The content path '"+contentPath+"' for the content plugin '"+contentPlugin.getClass().getSimpleName()+"' is invalid. "+
					"All content paths should begin with the content plugin's request prefix.");
	}

}
