package org.bladerunnerjs.model.exception;

import java.io.File;


public class MultipleNodesForPathException extends Exception
{
	private static final long serialVersionUID = 1L;

	public MultipleNodesForPathException(File childPath, String methodName)
	{
		super( "There are multiple nodes for the path '" + childPath.getPath() + "'. Use the " + methodName + " method instead." );
	}
}
