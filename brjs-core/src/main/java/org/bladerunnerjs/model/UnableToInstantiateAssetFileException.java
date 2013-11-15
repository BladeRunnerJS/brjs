package org.bladerunnerjs.model;

import org.apache.commons.lang3.StringUtils;


public class UnableToInstantiateAssetFileException extends Exception
{
	private static final long serialVersionUID = 6076369548188355671L;

	public UnableToInstantiateAssetFileException(Class<? extends AssetFile> assetFileType, Class<?>... constructorArgTypes)
	{
		super("Exception while attempting to create an instance of '"+assetFileType.getCanonicalName()+"'. It must define a constructor the accepts the types '"+StringUtils.join(constructorArgTypes)+"'");
	}
}
