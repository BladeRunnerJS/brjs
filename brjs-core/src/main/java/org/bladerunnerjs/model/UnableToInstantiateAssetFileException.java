package org.bladerunnerjs.model;

import org.apache.commons.lang3.StringUtils;


public class UnableToInstantiateAssetFileException extends Exception
{
	private static final long serialVersionUID = 6076369548188355671L;

	public UnableToInstantiateAssetFileException(Exception ex, Class<? extends Asset> assetFileType, Class<?>... constructorArgTypes)
	{
		super("Exception while attempting to create an instance of '"+assetFileType.getCanonicalName()+"'. Make sure it has constructor the accepts the types '"+StringUtils.join(constructorArgTypes)+"'", ex);
	}
}
