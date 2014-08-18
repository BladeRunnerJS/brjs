package org.bladerunnerjs.model.exception;


public class DuplicateAssetContainerException extends RuntimeException
{
	private static final long serialVersionUID = -2402426315431900548L;

	public DuplicateAssetContainerException(String assetContainerType, String firstPath, String secondPath) {
		super("The 'default "+assetContainerType+"' exists in two locations, '"+firstPath+"' and '"+secondPath+"'. Default asset containers can only exist in a single location.");
	}
}
