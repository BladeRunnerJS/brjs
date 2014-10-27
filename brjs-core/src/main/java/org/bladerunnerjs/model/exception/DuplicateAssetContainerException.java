package org.bladerunnerjs.model.exception;

/**
 * Class derived from RuntimeException -> Exception -> Throwable -> Object.
 * Thrown when the default asset container exists in two locations, since it may only exist in one.
*/

public class DuplicateAssetContainerException extends RuntimeException
{
	private static final long serialVersionUID = -2402426315431900548L;

	public DuplicateAssetContainerException(String assetContainerType, String firstPath, String secondPath) {
		super("The 'default "+assetContainerType+"' exists in two locations, '"+firstPath+"' and '"+secondPath+"'. Default asset containers can only exist in a single location.");
	}
	
	public DuplicateAssetContainerException(String message, Object... params) {
		super( String.format(message, params) );
	}
	
}
