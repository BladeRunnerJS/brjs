package org.bladerunnerjs.model;

import org.apache.commons.lang3.StringUtils;


public class AssetFileInstantationException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public AssetFileInstantationException(String message) {
		super(message);
	}
	
	public AssetFileInstantationException(Exception ex, Class<? extends Asset> assetFileType, Class<?>... constructorArgTypes)
	{
		super("Exception while attempting to create an instance of '"+assetFileType.getCanonicalName()+"'. Make sure it has constructor the accepts the types '"+StringUtils.join(constructorArgTypes)+"'", ex);
	}

	public AssetFileInstantationException(Exception e) {
		super(e);
	}
}
