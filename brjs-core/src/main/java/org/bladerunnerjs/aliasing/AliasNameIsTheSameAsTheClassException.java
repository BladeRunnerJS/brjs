package org.bladerunnerjs.aliasing;

import java.io.File;


public class AliasNameIsTheSameAsTheClassException extends AliasException
{
	private static final long serialVersionUID = -6174127010172874714L;

	public AliasNameIsTheSameAsTheClassException(File aliasesFile, String aliasName)
	{
		super("The alias '"+aliasName+"' defined within '"+aliasesFile.getPath()+"' cannot have the same name as it's defaalt class.");
	}

}
