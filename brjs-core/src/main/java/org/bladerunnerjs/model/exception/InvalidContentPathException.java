package org.bladerunnerjs.model.exception;

import org.bladerunnerjs.plugin.ContentPlugin;


public class InvalidContentPathException extends ModelOperationException
{
	private static final long serialVersionUID = 1716077006805580210L;

	public InvalidContentPathException(ContentPlugin contentPlugin, String requestPrefix, String contentPath)
	{
		super("The content plugin '"+contentPlugin+"' has invalid content paths. "+
				"Content paths cannot have a common root where the root for one path is expected to be a file, "+
				"for example 'plugin/content/path' and 'plugin/content' is not allowed and should instead be "+
				"'plugin/content/path' and 'plugin/moreContent'.");
	}

}
